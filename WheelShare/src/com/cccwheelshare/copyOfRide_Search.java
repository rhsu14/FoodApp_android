package com.cccwheelshare;

import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;

public class copyOfRide_Search extends FragmentActivity
{
    /*private static EditText   DATE;
    private static EditText   DEST;
    private static RadioGroup OFFER;
    private static Resources  r;
    private static CheckBox   SMOKING;
    private static EditText   SRC;
    private static Date       startTime;
    private static String     status;
    private static TextView   STATUS;
    private static EditText   TIME;*/

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.restaurant_list );

        Globals.setAppFont( (ViewGroup) findViewById( R.id.Ride_Search ),
                            Globals.Font.on ? Typeface.createFromAsset( getAssets(), Globals.Font.mangal )
                                            : Typeface.DEFAULT );

       /* r       = this.getResources();
        DATE    = (EditText)   findViewById( R.id.DEPART_DATE );
        DEST    = (EditText)   findViewById( R.id.DEST_Field );
        OFFER   = (RadioGroup) findViewById( R.id.OFFER_Radio );
        SMOKING = (CheckBox)   findViewById( R.id.SMOKING_CheckBox );
        SRC     = (EditText)   findViewById( R.id.SRC_Field );
        STATUS  = (TextView)   findViewById( R.id.STATUS );
        TIME    = (EditText)   findViewById( R.id.DEPART_TIME );
        */
        //registerDevice();
    }
}
    
    
/*public void login( final View v )
{
    STATUS.setText( r.getString( R.string.User_Logging_In ) );

    Globals.currentUser = new User();
    Globals.currentUser.EMAIL( EMAIL.getText().toString().trim() );
    Globals.currentUser.PASSWORD( PASS.getText().toString().trim() );
    
    startActivityForResult( new Intent( this, Ride_Search.class ), 0 );
    //new Async_Login().execute( Globals.URL.logingoogle );
}
    
    */
    
    /*

    
    
    
    
    
    
    
    private void registerDevice()
    {
        status  = Globals.Empty;
        status += "\n"+r.getString( R.string.Device_ID )+Globals.SENDER_ID;

        GCMRegistrar.checkDevice( this );
        GCMRegistrar.checkManifest( this );

        final String regId = GCMRegistrar.getRegistrationId( this );
        status += "\n"+r.getString( R.string.Device_ID_GCM )+regId;
        
        if( regId.equals( Globals.Empty ) )
        {
            GCMRegistrar.register( this, Globals.SENDER_ID );
            status += "\n"+r.getString( R.string.Device_Registered );
        }
        else
        {
            status += "\n"+r.getString( R.string.Device_Already_Registered );
        }
        
        STATUS.setText( status );
    }

    public void cityLookup( final View v )
    {
        status = Globals.Empty;
        EditText CITY = null;
        
        if( v.getId() == R.id.SRC_Field )       { CITY = SRC; }
        else if( v.getId() == R.id.DEST_Field ) { CITY = DEST; }
        else                                    { status += "\n"+r.getString( R.string.Ride_Bad_City_Field ); }

        if( status.equals( Globals.Empty ) && !CITY.equals( null ) )
        {
            Globals.createAlertDialog( new AlertDialog.Builder( this ),
                                       Globals.CITY_LIST,
                                       r.getString( R.string.Ride_City_Title ),
                                       CITY ).show();
        }
        else
        {
            STATUS.setText( status );
        }
    }
    
    public void showDatePickerDialog( final View v )
    {
        new DatePickerFragment( DATE ).show( getSupportFragmentManager(), "datePicker" );
    }
        
    public void showTimePickerDialog( final View v )
    {
        new TimePickerFragment( TIME ).show( getSupportFragmentManager(), "timePicker" );
    }

    @SuppressWarnings( "deprecation" )
    public void searchSubmit( final View v )
    {
        status = Globals.Empty;

        Integer offer    = OFFER.getCheckedRadioButtonId();
        Boolean offering = null;
        if( offer.equals( R.id.OFFER_Driver ) )         { offering = false; }
        else if( offer.equals( R.id.OFFER_Passenger ) ) { offering = true; }
        else                                            { status += "\n"+r.getString( R.string.Generic_Bad_RadioButton ); }

        // TODO Need to match DATE and TIME separately. Currently creates a new startTime if either is blank. Not good enough.
        String date = DATE.getText().toString().trim();
        String time = TIME.getText().toString().trim();
        if( time.equals( Globals.Empty ) && date.equals( Globals.Empty ) ) { startTime = new Date(); }
        else                               { startTime = new Date( date+ " " +time ); }
        
        if( status.equals( Globals.Empty ) )
        {
            Globals.currentRide = new Ride();
            Globals.currentRide.SRC( SRC.getText().toString().trim() );
            Globals.currentRide.DEST( DEST.getText().toString().trim() );
            Globals.currentRide.SMOKING( SMOKING.isChecked() );
            Globals.currentRide.OFFERING( offering );
            Globals.currentRide.STARTTIME( startTime );
            
            new Async_Ride_Search().execute();
        }
        else
        {
            STATUS.setText( status );
        }
    }
    
    private class Async_Ride_Search extends AsyncTask<Void, Void, Boolean>
    {
        @Override protected void onPreExecute() { before(); }
        protected Boolean doInBackground( final Void... V )
        {
            try                       { return during(); }
            catch( ParseException e ) { Globals.exception = Globals.ParseException; e.printStackTrace(); }
            catch( JSONException e )  { Globals.exception = Globals.JSONException;  e.printStackTrace(); }
            catch( IOException e )    { Globals.exception = Globals.IOException;    e.printStackTrace(); }
            return null;
        }
        @Override protected void onPostExecute( final Boolean B ) { after( B ); }
    }
    
    private void before()
    {
        STATUS.setText( r.getString( R.string.Ride_Searching ) );
    }
    
    private Boolean during() throws ClientProtocolException, JSONException, IOException
    {
        return this.searchJSON();
    }
    
    private void after( final Boolean success )
    {
        if( success == null )
        {
            STATUS.setText( Globals.exception );
        }
        else if ( success )
        {
            STATUS.setText( r.getString( R.string.Ride_Search_Success_Prefix )+" "+
                            Globals.MATCHED_RIDES.size()+" "+
                            r.getString( R.string.Ride_Search_Success_Postfix ) );

            startActivityForResult( new Intent( this, Ride_Search_Results.class ), 0 );
        }
        else
        {
            STATUS.setText( r.getString( R.string.Ride_Search_Results_None ) );
            startActivityForResult( new Intent( this, Ride_Search_Results.class ), 0 );
        }
    }
   

    private Boolean searchJSON() throws JSONException, ClientProtocolException, IOException
    {
        JSONObject json = Globals.currentRide.tojson();
        
        JSONObject result = Globals.getJSON( Globals.URL.getRides, json );

        JSONObject rides = result.getJSONObject( Globals.JSON.rides );
        
        Globals.MATCHED_RIDES.clear();
        for( int i = 0; i < result.getInt( Globals.JSON.qty ); ++i )
        {
            Globals.MATCHED_RIDES.add( new Ride( rides.getJSONObject( Globals.Empty+i ) ) );
        }
        
        return result.getBoolean( Globals.JSON.success );
    }
    
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.ride_search, menu );
        return true;
    }
}
*/

