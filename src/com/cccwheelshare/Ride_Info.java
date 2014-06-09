package com.cccwheelshare;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Ride_Info extends FragmentActivity
{
    private static TextView  BOOK;
    private static TextView  COMPLETE;
    private static Resources r;
    private static TextView  RIDE_INFO;
    private static String    status;
    private static TextView  STATUS;

    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.ride_info );

        Globals.setAppFont( (ViewGroup) findViewById( R.id.Ride_Info ),
                            Globals.Font.on ? Typeface.createFromAsset( getAssets(), Globals.Font.mangal )
                                            : Typeface.DEFAULT );

        BOOK      = (Button)   findViewById( R.id.BOOK_Button );
        COMPLETE  = (Button)   findViewById( R.id.COMPLETE_RIDE_Button );
        r         = this.getResources();
        RIDE_INFO = (TextView) findViewById( R.id.RIDE_INFO_Text );
        STATUS    = (TextView) findViewById( R.id.STATUS );
    }
    
    protected void onResume()
    {
        super.onResume();

        toggle();
    }

    private void toggle()
    {
        status                   = Globals.Empty;
        String  book             = Globals.Empty;
        Boolean book_visible     = null;
        Boolean complete_visible = null; 
        
        Integer driverId = Globals.selectedRide.DRIVERID();
        Integer userId   = Globals.currentUser.ID();
        
        if( driverId.equals( userId ) )
        {
            status          += "\n"+r.getString( R.string.Ride_Info_Owned_Text );
            book_visible     = false;
            complete_visible = Globals.selectedRide.OFFERING(); 
        }
        else
        {
            complete_visible = false;

            if( Globals.selectedRide.OFFERING() )
            {
                if( Globals.selectedRide.hasRider( Globals.currentUser.ID() ) )
                {
                    status      += "\n"+r.getString( R.string.Ride_Info_Already_Booked );
                    book_visible = false;
                }
                else
                {
                    book         = r.getString( R.string.Ride_Info_Book_Button );
                    book_visible = true;
                }
            }
            else
            {
                book         = r.getString( R.string.Ride_Info_Fulfill_Button );
                book_visible = true;
            }
        }
        
        if( book_visible.equals( null ) )     { status += "\n"+r.getString( R.string.Ride_Book_Bad_Visibility ); }
        if( complete_visible.equals( null ) ) { status += "\n"+r.getString( R.string.Ride_Bad_Complete_Visibility ); }

        BOOK.setVisibility( book_visible ? View.VISIBLE : View.GONE );
        BOOK.setText( book );
        COMPLETE.setVisibility( complete_visible ? View.VISIBLE : View.GONE );
        COMPLETE.setText( r.getString( R.string.Ride_Complete_Button ) );
        RIDE_INFO.setText( Globals.selectedRide.printPretty() );
        STATUS.setText( status );
    }
    
    public void bookRide( final View v ) throws ClientProtocolException, JSONException, IOException
    {
        proceed( Globals.currentRide.OFFERING() ? Ride_Payment.class
                                                : Ride_Booking.class );
    }
    
    public void completeRide( final View v )
    {
        STATUS.setText( Globals.Empty );
        new Async().execute( Globals.URL.gcmout );
        new Async().execute( Globals.URL.completeRide );
    }
        
    private class Async extends AsyncTask<String, Void, Boolean>
    {
        String[] S;
        protected Boolean doInBackground( final String... S )
        {
            this.S = S;
            try                       { return JSON( S[0] ); }
            catch( ParseException e ) { Globals.exception = Globals.ParseException; }
            catch( JSONException e )  { Globals.exception = Globals.JSONException; }
            catch( IOException e )    { Globals.exception = Globals.IOException; }
            return null;
        }
        protected void onPostExecute( final Boolean B ) { after( B, S[0] ); }
    }
    
    private Boolean JSON( final String url ) throws ClientProtocolException, IOException, JSONException
    {
        JSONObject data   = new JSONObject();
        JSONObject json   = new JSONObject();
        JSONObject result = new JSONObject();
        
        if( url.equals( Globals.URL.gcmout ) )
        {
            data = Globals.selectedRide.tojson();
            data.put( "activity", "complete" );
            JSONArray ids = new JSONArray();
            ids.put( Globals.selectedRide.DRIVERID() );
            for( int i : Globals.selectedRide.RIDERIDS() ){
                ids.put( i );
            }
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
        else if( url.equals( Globals.URL.completeRide ) )
        {
            json = Globals.selectedRide.tojson();
            result = Globals.getJSON( url, json );
        }
        
        return result.getBoolean( Globals.JSON.success ); 
    }
    
    private void after( final Boolean success, final String url )
    {
        if( success == null )
        {
            status += "\n"+Globals.exception;
        }
        if( success )
        {
            status += "\n"+r.getString( R.string.Generic_Success );
        }
        else
        {
            status += "\n"+r.getString( R.string.Generic_Failure );
        }

        STATUS.setText( status );
        
        if( url.equals( Globals.URL.completeRide ) && success )
        {
            proceed( Tabs.class );
        }
    }
    
    private void proceed( Class<?> page )
    {
        startActivityForResult( new Intent( this, page ), 0 );
    }
    
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.ride_info, menu );
        return true;
    }
}
