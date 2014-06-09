package com.cccwheelshare;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

// TODO  add strings to strings.xml
public class Ride_Booking extends Activity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.ride_payment );

        //new Async().execute( Globals.URL.gcmout );
        //new Async().execute( Globals.URL.updateRide );
    }
    
    private class Async extends AsyncTask<String, Void, Boolean>
    {
        protected Boolean doInBackground( final String... S )
        {
            try                       { return JSON( S[0] ); }
            catch( ParseException e ) { Globals.exception = Globals.ParseException; e.printStackTrace(); }
            catch( JSONException e )  { Globals.exception = Globals.JSONException;  e.printStackTrace(); }
            catch( IOException e )    { Globals.exception = Globals.IOException;    e.printStackTrace(); }
            return null;
        }
        protected void onPostExecute( final Boolean B ) { after( B ); }
    }
    
    private Boolean JSON( final String url ) throws ClientProtocolException, IOException, JSONException
    {
        JSONObject result = new JSONObject();
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        
        if( url.equals( Globals.URL.gcmout ) )
        {
            data.put( Globals.JSON.offering, Globals.selectedRide.OFFERING() );
            data.put( Globals.JSON.rideId, Globals.selectedRide.ID() );
            data.put( Globals.JSON.userId, Globals.currentUser.ID() );
            data.put( "activity", "pending" );
            
            JSONArray ids = new JSONArray();
            ids.put(Globals.selectedRide.DRIVERID());

            json.put( Globals.JSON.toUserId, ids );
            json.put( Globals.JSON.data, data.toString() );
            
            result = Globals.getJSON( url, json );
        }
        else if( url.equals( Globals.URL.updateRide ) )
        {
            json = Globals.selectedRide.tojson_addPass( Globals.currentUser.ID() );
            
            result = Globals.getJSON( url, json );
            
            Globals.selectedRide = new Ride( result.getJSONObject( Globals.JSON.ride ) );
        }
        
        return result.getBoolean( Globals.JSON.success ); 
    }
    
    private void after( final Boolean success )
    {
        setResult( RESULT_OK, new Intent().putExtra( "success", true ).putExtra( "message", "Ride booked" ) );
        finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.ride_payment, menu );
        return true;
    }
}
