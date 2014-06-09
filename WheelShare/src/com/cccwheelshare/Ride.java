package com.cccwheelshare;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Ride
{
    private int           driverId,
                          rideId,
                          maxPass,
                          remPass;
    private String        src,
                          dest;
    private double        cost;
    private boolean       smoking,
                          offering;
    private Date          startTime;
//                          curTime;
    private List<Integer> riderIds = new ArrayList<Integer>();
    private JSONObject    riderId_list;

    //TODO ESCROW?!?!

    public Ride()
    {
        this.rideId    = 0;
        this.driverId  = -1;
        this.maxPass   = 1;
        this.remPass   = this.maxPass;
        this.src       = "";
        this.dest      = "";
        this.cost      = 0.0;
        this.smoking   = false;
        this.offering  = false;
        this.startTime = new Date();
//        this.curTime   = new Date();
        this.riderIds  = new ArrayList<Integer>();
    }

    public Ride( int           driverId,
                 int           maxPass,
                 String        src,
                 String        dest,
                 double        cost,
                 boolean       smoker,
                 boolean       offering,
                 Date          startTime,
//                 Date          curTime,
                 List<Integer> riderIds )
    {
        this.driverId  = driverId;
        this.maxPass   = maxPass;
        this.remPass   = this.maxPass;
        this.src       = src;
        this.dest      = dest;
        this.cost      = cost;
        this.smoking   = smoker;
        this.offering  = offering;
        this.startTime = startTime;
//        this.curTime   = curTime;
        this.riderIds  = riderIds;
    }

    public Ride( JSONObject ride ) throws JSONException
    {
        this.driverId     = ride.getInt( "driverId" );
        this.rideId       = ride.getInt( "rideId" );
        this.maxPass      = ride.getInt( "maxPass" );
        this.src          = ride.getString( "src" );
        this.dest         = ride.getString( "dest" );
        this.cost         = ride.getDouble( "cost" );
        this.smoking      = ride.getBoolean( "smoking" );
        this.offering     = ride.getBoolean( "offering" );
        this.startTime    = new Date( ride.getLong( "startTime" ) );
//        this.curTime      = new Date( ride.getInt( "curTime" ) );
        this.remPass      = ride.getInt( "remPass" );
        
        this.riderId_list = ride.getJSONObject( "riderIds" );

        for( int i = 0; i < this.riderId_list.length(); ++i )
        {
            this.riderIds.add( this.riderId_list.getInt( ""+i ) );
        }
    }

    // Getters and setters
    public int           ID()                                   { return this.rideId; }
    public void          ID(final int rideId)                   { this.rideId = rideId; }
    public int           DRIVERID()                             { return this.driverId; }
    public void          DRIVERID(final int driverId)           { this.driverId = driverId; }
    public int           MAXPASS()                              { return this.maxPass; }
    public void          MAXPASS(final int maxPass)             { this.maxPass = maxPass; }
    public int           REMPASS()                              { return this.remPass; }
    public void          REMPASS(final int remPass)             { this.remPass = remPass; }
    public String        SRC()                                  { return this.src; }
    public void          SRC(final String src)                  { this.src = src; }
    public String        DEST()                                 { return this.dest; }
    public void          DEST(final String dest)                { this.dest = dest; }
    public double        COST()                                 { return this.cost; }
    public void          COST(final double cost)                { this.cost = cost; }
    public boolean       SMOKING()                              { return this.smoking; }
    public void          SMOKING(final boolean smoking)         { this.smoking = smoking; }
    public boolean       OFFERING()                             { return this.offering; }
    public void          OFFERING(final boolean offering)       { this.offering = offering; }
    public Date          STARTTIME()                            { return this.startTime; }
    public void          STARTTIME(final Date startTime)        { this.startTime = startTime; }
//    public Date          CURTIME()                              { return this.curTime; }
//    public void          CURTIME(final Date curTime)            { this.curTime = curTime; }
    public List<Integer> RIDERIDS()                             { return this.riderIds; }
    public void          RIDERIDS(final List<Integer> riderIds) { this.riderIds = riderIds; }

    public String print()
    {
        return "rideId = "       +this.rideId
               +"\ndriverId = "  +this.driverId
               +"\nmaxPass = "   +this.maxPass
               +"\nremPass = "   +this.remPass
               +"\nsrc = "       +this.src
               +"\ndest = "      +this.dest
               +"\ncost = "      +this.cost
               +"\nsmoking = "   +this.smoking
               +"\noffering = "  +this.offering
               +"\nstartTime = " +this.startTime
//               +"\ncurTime = "   +this.curTime
               +"\nriderIds = "  +this.riderIds
               ;
    }

    public String printPretty()
    {
        return "Ride ID: "+this.rideId+
               "\n"+"User ID: "+this.driverId+
               "\n"+"From: "+this.src+
               "\n"+"To: "+this.dest+
               "\n"+"Cost: "+this.cost+
               "\n"+( this.smoking ? "Smoking allowed" : "No smoking" )+
               "\nLeaving by: "+this.startTime
               ;
    }

    private JSONObject riderIds_JSON() throws JSONException
    {
        JSONObject riderIds = new JSONObject();

        for( int i = 0; i < this.riderIds.size(); ++i )
        {
            riderIds.put( ""+i, this.riderIds.get(i) );
        }

        return riderIds;
    }

    public JSONObject tojson() throws JSONException
    {
        JSONObject ride_JSON = new JSONObject();

        ride_JSON.put( "driverId",  this.driverId );
        ride_JSON.put( "src",       this.src );
        ride_JSON.put( "dest",      this.dest );
        ride_JSON.put( "cost",      this.cost * 1.0 );
        ride_JSON.put( "maxPass",   this.maxPass );
        ride_JSON.put( "remPass",   this.remPass );
        ride_JSON.put( "smoking",   this.smoking );
        ride_JSON.put( "offering",  this.offering );
        ride_JSON.put( "startTime", this.startTime.getTime() );
//        ride_JSON.put( "curTime",   this.curTime.getTime() );
        ride_JSON.put( "rideId",    this.rideId );
        ride_JSON.put( "riderIds",  this.riderIds_JSON() );

        return ride_JSON;
    }

    public JSONObject tojson_addPass( final int userId ) throws JSONException
    {
        JSONObject ride_JSON = new JSONObject();

        ride_JSON.put( "rideId",   this.rideId );
        ride_JSON.put( "userId",   userId );
        ride_JSON.put( "offering", offering );

        return ride_JSON;
    }
    
    public boolean hasRider( final int userId )
    {
        return this.riderIds.contains( userId );
    }
}