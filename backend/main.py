#Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import webapp2
import logging
import cgi
import urllib2
import json

from google.appengine.ext import db

from google.appengine.api import users

from google.appengine.api import xmpp

from google.appengine.api import mail

from os import environ

from recaptcha import captcha
import hashlib
from os import urandom
from binascii import hexlify # For urandom/hash stringification

SALT_LEN = 32
CODE_LEN = 30

# Creates a class of users to be inserted into the database.
class User(db.Model):

    username    = db.StringProperty()                   # username
    password    = db.StringProperty()                   # password
    name        = db.StringProperty(default = "")       # Actual name
    email       = db.StringProperty()                   # email address of user
    smoker      = db.BooleanProperty(default = False) # smoking boolean
    age         = db.IntegerProperty(default = 21)   # age of the user
    rating      = db.IntegerProperty(default = 0)     # user's rating
    notifId     = db.StringProperty(default = "")      # user's GCM notification ID
    salt        = db.StringProperty()

    def toDict(self):

        # Defines the json object to pass to the activities containing the data
        # of the user db object.

        user = {}
        
        user["username"]  = self.username
        user["smoker"]    = self.smoker
        user["age"]       = self.age
        user["email"]     = self.email
        user["name"]      = self.name
        user["rating"]    = self.rating
        user["salt"]      = self.salt

        return user

class RideObject(db.Model):
    driverId    = db.IntegerProperty()
    src         = db.StringProperty()
    dest        = db.StringProperty()
    maxPass     = db.IntegerProperty()
    remPass     = db.IntegerProperty()
    cost        = db.FloatProperty()
    smoking     = db.BooleanProperty()
    offering    = db.BooleanProperty()
    startTime   = db.IntegerProperty()
    riderIds    = db.ListProperty(int)

    def toDict(self):
        ride = {}

        ride["driverId"]  = self.driverId
        ride["src"]       = self.src
        ride["dest"]      = self.dest
        ride["maxPass"]   = self.maxPass
        ride["remPass"]   = self.remPass
        ride["startTime"] = self.startTime
        ride["cost"]      = self.cost
        ride["smoking"]   = self.smoking
        ride["offering"]  = self.offering
        
        riders = {}
        
        j = 0
        for id in self.riderIds:
            riders[str(j)] = id
            j += 1
            
        ride ["riderIds"] = riders

        return ride

class NotVerUser(db.Model):
    email    = db.StringProperty()  # email
    password = db.StringProperty()  # password
    code     = db.StringProperty()  # verification code
    salt     = db.StringProperty()

    def toDict(self):
        resp = {}
        resp["email"]    = self.email
        resp["password"] = self.password
        resp["code"]     = self.code
        resp["salt"]     = self.salt
        return resp

class NewRegUserHandler(webapp2.RequestHandler):
       def post(self):
                 salt     = str(hexlify(urandom(SALT_LEN)))
                 code     = str(hexlify(urandom(CODE_LEN)))
                 password = str(hashlib.sha256(salt + self.request.get("pwd")).hexdigest())
                 user = NotVerUser(email = self.request.get("email"), password = password, salt = salt, code = code)
                 user.put()
                 mail.send_mail(sender   = "wheelshareapp@gmail.com",
                                                  to        = user.email,
                                                  subject = "Wheelshare Registration Verification Email",
                                                  body    = """
                                                          Dear user,
                                                          Your account needs to be verified. Please visit cccwheelsharetest.appspot.com/initcap/""" + str(user.code) + """\n

                                                          - The Wheelshare Development Team""")
                 resp                    = user.toDict()
                 resp["success"] = False
                 resp["userId"]  = user.key().id()
                 resp["message"] = "BLAAARGH."
                 
                 self.response.write("Thank you! A verification email has been sent to your email.");

class InitCAPTCHA(webapp2.RequestHandler):
        def get(self, code):
                self.response.write("""<form action="/vercap"> """)
                self.response.write("""<input name="code" type="hidden" value=""" + code +""">""")
                chtml = captcha.displayhtml(
                  public_key = "6LcCYe8SAAAAALsqtQmjcaT_YGMdXUUchvJVoIgN",
                  use_ssl = False,
                  error = None)
                self.response.write(chtml)
                self.response.write("""<input type="submit" value="Submit"> """)
                self.response.write("</form>")

class VerifyCAPTCHA(webapp2.RequestHandler):
        def get(self):
                challenge = self.request.get('recaptcha_challenge_field')
                response  = self.request.get('recaptcha_response_field')
                remoteip  = environ['REMOTE_ADDR']

                cResponse = captcha.submit(
                      challenge,
                      response,
                      "6LcCYe8SAAAAAL4_401ToB3iSuJisouROiEs536m",
                      remoteip)

                if cResponse.is_valid:
                      # password = hashlib.hashlib.sha256(os.urandom(32) || [self.request.password | lookup_password_from_code(self.response.code)])                      
                    badUser = NotVerUser.gql("WHERE code = :1", self.request.get('code')).get()
                    if(badUser):
                        goodUser = User(email = badUser.email, password = badUser.password, username = badUser.email, salt=badUser.salt)
                        goodUser.put()
                        badUser.delete()
                        # delete badUser at this stage 
                        self.response.write("THANKEES FOR REGISTERING " + goodUser.username)
                        self.response.write("""<form action="/loginweb" method="get"><input type="submit" value="Login"></form> """)
                    else:
		        self.response.write("No such unregistered user exists. Sorry!");

                else:
                     error = cResponse.error_code

class LoginWebHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write(
            """ Welcome to WheelShare! Login: <br /> 
                <form action="/logincheck" method="post"> 
                    <input type="text" name="email" placeholder="email" required><br />
                    <input type="password" name="pwd" placeholder="pw" required><br />
                    <input type="submit" value="Login">
                </form>
            """
        )
	
class LoginCheckHandler(webapp2.RequestHandler):
    def post(self):
	# CHECK CREDENTIALS
	user = User.gql("WHERE email = :1", self.request.get("email")).get()
	if(user and user.password == str(hashlib.sha256(user.salt + self.request.get("pwd")).hexdigest()) ):
	    #verified!
	    self.response.write(""" 
	      Congrats! You are logged in!
	      RIDES and a basic MENU would be here on the app side, but this is the web interface. 
	      """)
	else:
	    #not verified! faker!
	    self.response.write(""" 
	      Either the username or password is incorrect
	      """)
	

class RegWebHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write(
            """ Welcome to WheelShare! Regsiter: <br /> 
                <form action="/reg" method="post"> 
                    <input type="text" name="email" placeholder="email" required><br />
                    <input type="password" name="pwd" placeholder="pw" required><br />
                    <input type="submit" value="Register">
                </form>
            """
        )


#############################
# Original Register handler #
#############################
class RegisterUserHandler(webapp2.RequestHandler):
        
    def post (self):
            
        data = json.loads(self.request.body)

        resp ={}

        user1 = GqlQuery("SELECT * FROM User WHERE email = :1 AND password = :2", data["email"], data["password"]).get()

        if (user1 and (user1.email == data["email"])):
            if (user1.password == data["password"]):
                resp = user1.toDict()
                resp["userId"]  = user1.key().id()
                resp["success"] = True
                resp["message"] = "Account already exists and verified and good to go"
            else:
                resp["success"] = False
                resp["message"] = "Email already in use"
        else:
            user2 = NotVerUser().gql("WHERE email = :1 AND password = :2", data["email"], data["password"]).get()

            if (user2 and user2.email == data["email"]):
                if (user2.password == data["password"]):
                    resp["success"] = False
                    resp["message"] = "Check your Email!"
                else:
                    resp["success"] = False
                    resp["message"] = "Email already in use"
            else:
                if (mail.is_email_valid(data["email"])):
                    user = NotVerUser(email = data["email"], password = data["password"], code = "abc" + str(123))
                    user.put()
                    mail.send_mail(sender   = "wheelshareapp@gmail.com",
                                    to    = user.email,
                                    subject = "Wheelshare Registration Verification Email",
                                    body    = """
                                        Dear user,
                                        Your account has been verified. Please visit cccwheelshare.appspot.com/verify/"""
                                        + str(user.key().id()) + "/" +str(user.code) + """.
                                        Thank you for using Wheelshare!

                                        - The Wheelshare Development Team""")
                    resp            = user.toDict()
                    resp["success"] = False
                    resp["userId"]  = user.key().id()
                    resp["message"] = "BLAAARGH."
                else:
                    resp["success"] = False
                    resp["message"] = "Please enter a valid email."

        return self.response.write(json.dumps(resp))

##########################
# Original Login handler #
##########################
class LoginUserHandler(webapp2.RequestHandler):

    def post(self):
            
        data = json.loads(self.request.body)

        resp = {}

        user = GqlQuery("SELECT * FROM User WHERE email = :1 AND password = :2", data["email"], data["password"]).get()

        if user:
            resp = user.toDict()
            resp["success"] = True
            resp["userId"]  = user.key().id()
                
        else:
            resp ["success"] = False
            resp ["message"] = "Login attempt failed"

        return self.response.write(json.dumps(resp))

class AddRideHandler(webapp2.RequestHandler):
    def post (self):

        data = json.loads(self.request.body)
        
        ride = RideObject(
            driverId  = data["driverId"],
            src     = data["src"],
            dest      = data["dest"],
            maxPass = data["maxPass"],
            remPass = data["remPass"],
            startTime = data["startTime"],
            cost      = float(data["cost"]),
            smoking = bool(data["smoking"]),
            offering  = bool(data["offering"]),
            riderIds  = [] )

        try:
            # ride.riderIds = data["riderIds"]
            ride.put()
        finally:
            pass
        
        resp = {}

        if ride.key().id() == 0:
            resp["success"] = False

        else:
            temp = ride.toDict()
            temp["rideId"]  = ride.key().id()
            resp["ride"]    = temp
            resp["success"] = True

        return self.response.write(json.dumps(resp))

class GetRidesHandler(webapp2.RequestHandler):
    def post (self):
        data = json.loads(self.request.body)
        
        # start will all rides
        ride_query = RideObject().all()

        # show only rides that can be booked
        ride_query = ride_query.filter('remPass >', 0)

        # filter by smoking prefrence
        ride_query = ride_query.filter('smoking =', bool(data["smoking"]))

        # filter by offering vs requesting
        ride_query = ride_query.filter('offering =', bool(data["offering"]))

        # filter by source city if available
        if data["src"]:
            ride_query = ride_query.filter('src =', data["src"])

        # filter by destination city if available
        if data["dest"]:
            ride_query = ride_query.filter('dest =', data["dest"])

        # fetch 100 of the matches
        rides = ride_query.fetch(100)

        resp = {}
        oneRide = {}
        manyRides = {}

        for j,ride in enumerate(rides):
            oneRide = ride.toDict()
            oneRide["rideId"] = ride.key().id()
            manyRides[str(j)] = oneRide

        resp["rides"] = manyRides
            
        if len(manyRides) > 0:
            resp["success"] = True
            resp["message"] = "Rides found."

        else:
            resp["success"] = False
            resp["message"] = "No rides match your query."
        
        resp["qty"] = len(manyRides)

        return self.response.write(json.dumps(resp))

#update ride needed!

# Ride sorter class:

class SortRidesHandler(webapp2.RequestHandler):
    def post(self):
        sortType = json.loads(self.request.body)

        if sortType == "src":
            return None
        elif sortType == "dest":
            return None
        elif sortType == "cost":
            return None
        elif sortType == "maxPass":
            return None
        elif sortType == "remPass":
            return None

class UpdateRideHandler(webapp2.RequestHandler):
    def post(self):
        data = json.loads(self.request.body)

        resp = {}

        # Add a single rider to a ride
        if data["rideId"]:
            ride = RideObject().get_by_id(data["rideId"])

            if ride:
                if data["userId"]:
                    if ride.remPass > 0:
                        if bool(data["offering"]):
                            ride.riderIds.append(data["userId"])
                            resp["message"] = "You have booked this ride"
                        else:
                            ride.riderIds.append(ride.driverId)
                            ride.driverId = data["userId"]
                            resp["message"] = "You have fulfilled this ride"

                        ride.remPass -= 1
                        ride.put()
                        resp["success"] = True
                    else:
                        resp["success"] = False
                        resp["message"] = "The ride is full!"

                else:
                    resp["success"] = True
                    resp["message"] = "other operation"

                temp = {}
                temp = ride.toDict()
                temp["rideId"] = ride.key().id()
                resp["ride"] = temp

        else:
            resp["success"] = False
            resp["message"] = "Did not specify a rideId!"

        return self.response.write(json.dumps(resp))

class OldVerifyUserHandler(webapp2.RequestHandler):
    def get(self, id, code):
        user = NotVerUser.get_by_id(int(id))
        if not user : # NO USER FOR ID
            self.response.write("THAR BE AN ERROR WITH THE ID")
        elif code == user.code : # verified
            # move them to proper DB
            goodUser = User(email = user.email, password = user.password, username = user.email)
            goodUser.put()
            user.delete()
            self.response.write("THANKEES FOR REGISTERING " + goodUser.username)
        else: # IMPOSSIBRU
            self.response.write("YOU SHALL NOT PASS")

class GCMRelayHandler(webapp2.RequestHandler):
    def post(self):
        data = json.loads(self.request.body)
        GCMURL = "https://android.googleapis.com/gcm/send"
        APIKEY = "AIzaSyCYIxR4ptFBVgVpfSyntL5RruvGlM8iJ1g"

        # INPUT INCLUDES USERID TO NOTIFY
        # QUERY DB FOR NOTIFID FOR USERID
        # TODO - SWITCH FROM SINGLE USER MESSAGES TO MULTICAST MESSAGE

        toUser = User.get_by_id(data["toUserId"])
        notifIdList = []

        resp = {}

        if (toUser and (not (toUser.notifId == None))):
            notifIdList.append(toUser.notifId)

            logging.info("data is = %s", data["data"])

            # SEND JSON POST REQUEST TO GCMURL W/ HEADER APIKEY AS SHOWN IN EXAMPLE

            json_data = {"registration_ids": notifIdList, "data" : { "data" : data["data"] }}

            #json_data = {"data" : data["data"], "registration_ids": notifIdList }

            myKey = "key=" + APIKEY
            mdata = json.dumps(json_data)
            headers = {'Content-Type': 'application/json', 'Authorization': myKey}
            req = urllib2.Request(GCMURL, mdata, headers)
            f = urllib2.urlopen(req)
            response = json.loads(f.read())

            if response ['failure'] == 0:
                resp['success'] = True
            else:
                resp['success'] = False
        else:
            resp["success"] = False

            # THEN NOT SURE - PARSE ERROR CODES? RESEND?
            # RESEARCH!

        return self.response.write(json.dumps(resp))

class GCMRegisterHandler(webapp2.RequestHandler):
    def post(self):
        data = json.loads(self.request.body)

        userId  = data["userId"]
        notifId = data["notifId"]

        user = User.get_by_id(userId)
        user.notifId = notifId
        user.put()

        resp = {}
        resp["userId"]  = userId
        resp["notifId"] = notifId

        return self.response.write(json.dumps(resp))

class GetUserHandler(webapp2.RequestHandler):
    def post(self):
        data = json.loads(self.request.body)

        user = User.get_by_id(data["userId"])

        resp = {}

        if user:
            temp = {}
            temp["email"] = user.email
            temp["name"] = user.name
            resp["user"] = temp
            resp["success"] = True

        else:
            temp["email"] = "no email"
            temp["name"] = "no name"
            resp["user"] = temp
            resp["success"] = False

        return self.response.write(json.dumps(resp))

class FinishRideHandler(webapp2.RequestHandler):
    def post(self):

        logging.debug("Hello")
        
        data = json.loads(self.request.body)

        ride = RideObject.get_by_id(data["rideId"])

        resp = {}

        gcm = {}

        if (ride):

            inter_data = {}

            inter_data["activity"] = "escrow"

            inter_data["escrow"] = ride.cost * len(ride.riderIds)

            gcm["toUserId"] = ride.driverId

            gcm["data"] =  json.dumps(inter_data)

            logging.info("data is = %s", json.dumps(gcm))

            headers = {'Content-Type': 'application/json'}

            url = "http://cccwheelshare.appspot.com/gcmout"

            mdata = json.dumps(gcm)

            req = urllib2.Request(url, mdata, headers)
            
            f = urllib2.urlopen(req)
            
            response = json.loads(f.read())

            ride.delete()
            
            resp["success"] = True

        else:
            resp["success"] = False
        
        return self.response.write(json.dumps(resp))

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write("Hello World!")
        self.response.write("""<form action="/loginweb" method="get"><input type="submit" value="Login"></form> """)
        self.response.write("""<form action="/regweb" method="get"><input type="submit" value="Register"></form> """)
        

logging.getLogger().setLevel(logging.DEBUG)

app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/login', LoginUserHandler),
    #('/logingoogle', logingoogle),
    ('/verify/([0-9]+)/([\w\d]+)', OldVerifyUserHandler),
    ('/register', RegisterUserHandler),
    #('/updateUser', UpdateUserHandler),
    ('/addRide', AddRideHandler),
    ('/getRides', GetRidesHandler),
    ('/sortRides', SortRidesHandler),
    ('/updateRide', UpdateRideHandler),
    ('/gcmout', GCMRelayHandler),
    ('/gcmreg', GCMRegisterHandler),
    ('/getUser', GetUserHandler),
    ('/completeRide', FinishRideHandler),

    #new handlers
    ('/loginweb/?', LoginWebHandler),
    ('/logincheck/?', LoginCheckHandler),
    ('/regweb/?', RegWebHandler),
    ('/reg/?', NewRegUserHandler),
    ('/initcap/(.+)', InitCAPTCHA),
    ('/vercap', VerifyCAPTCHA)
], debug=True)
