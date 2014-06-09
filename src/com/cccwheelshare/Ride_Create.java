package com.cccwheelshare;

import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

public class Ride_Create extends FragmentActivity
{
    private static EditText   COST;
    private static EditText   DATE;
    private static EditText   DEST;
    private static EditText   MAXPASS;
    private static TableRow   MAXPASS_Row;
    private static RadioGroup OFFER;
    private static Resources  r;
    private static CheckBox   SMOKING;
    private static EditText   SRC;
    private static Date       startTime;
    private static String     status;
    private static TextView   STATUS;
    private static EditText   TIME;

    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.ride_create );

        Globals.setAppFont( (ViewGroup) findViewById( R.id.Ride_Create ),
                            Globals.Font.on ? Typeface.createFromAsset( getAssets(), Globals.Font.mangal )
                                            : Typeface.DEFAULT );

        COST        = (EditText)   findViewById( R.id.COST_Field );
        DATE        = (EditText)   findViewById( R.id.DEPART_DATE );
        DEST        = (EditText)   findViewById( R.id.DEST_Field );
        MAXPASS     = (EditText)   findViewById( R.id.MAXPASS_Field );
        MAXPASS_Row = (TableRow)   findViewById( R.id.MAXPASS_Row );
        OFFER       = (RadioGroup) findViewById( R.id.OFFER_Radio );
        r           = getResources();
        SMOKING     = (CheckBox)   findViewById( R.id.SMOKER_CheckBox );
        SRC         = (EditText)   findViewById( R.id.SRC_Field );
        STATUS      = (TextView)   findViewById( R.id.STATUS );
        TIME        = (EditText)   findViewById( R.id.DEPART_TIME );

        SRC.setText( Globals.currentRide.SRC() );
        DEST.setText( Globals.currentRide.DEST() );
        COST.setText( ((Double) Globals.currentRide.COST()).toString() );
        SMOKING.setChecked( Globals.currentRide.SMOKING() );
//        TIME.setText( global.currentRide.STARTTIME(). );
//        DATE.setText( global.currentRide.STARTTIME() );
        
        offerSwitch( null );
    }

    public void offerSwitch( final View v )
    {
        status = Globals.Empty;
        
        Integer offer = OFFER.getCheckedRadioButtonId();

        if( offer.equals( R.id.OFFER_Driver ) )
        {
            MAXPASS_Row.setVisibility( View.VISIBLE );
        }
        else if( offer.equals( R.id.OFFER_Passenger ) )
        {
            MAXPASS_Row.setVisibility( View.GONE );
        }
        else
        {
            status += "\n"+r.getString( R.string.Generic_Bad_RadioButton );
        }
        
        STATUS.setText( status );
    }
    
    public void cityLookup( final View v )
    {
        String statusCheck = Globals.Empty;
        
        EditText CITY = null;
        
        if(      v.getId() == R.id.SRC_Field )  { CITY = SRC; }
        else if( v.getId() == R.id.DEST_Field ) { CITY = DEST; }
        else                                    { status += "\n"+r.getString( R.string.Ride_Bad_City_Field ); }
        
        if( statusCheck.equals( Globals.Empty ) && !CITY.equals( null ) )
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
    public void createSubmit( final View v )
    {
        status = Globals.Empty;
        
        String src = SRC.getText().toString().trim();
        
        if( src.equals( Globals.Empty ) )
        {
            status += "\n"+r.getString( R.string.Ride_Bad_Src_Missing );
        }

        String dest = DEST.getText().toString().trim();
        
        if( dest.equals( Globals.Empty ) )
        {
            status += "\n"+r.getString( R.string.Ride_Bad_Dest_Missing );
        }

        String costStr = COST.getText().toString().trim();
        Double cost = null;
        
        if( costStr.equals( Globals.Empty ) )
        {
            cost = 0.0;
            status += "\n"+r.getString( R.string.Ride_Bad_Cost_Missing );
        }
        else
        {
            cost = Double.parseDouble( costStr );

            if( cost < 0 )
            {
                status += "\n"+r.getString( R.string.Ride_Bad_Cost_Invalid );
            }
        }

        Integer offer    = OFFER.getCheckedRadioButtonId();
        Integer maxPass  = null;
        Boolean offering = null;
        
        if( offer.equals( R.id.OFFER_Driver ) )
        {
            offering = true;
            String maxPassStr = MAXPASS.getText().toString().trim();

            if( maxPassStr.equals( Globals.Empty ) )
            {
                maxPass = 1;
                status += "\n"+r.getString( R.string.Ride_Bad_maxPass_Missing );
            }
            else
            {
                maxPass = Integer.parseInt( maxPassStr );
                if( maxPass <= 0 ) { status += "\n"+r.getString( R.string.Ride_Bad_maxPass_Invalid ); } }
        }
        else if( offer.equals( R.id.OFFER_Passenger ) ) { offering = false;
                                                          maxPass  = 1; }
        else { status += "\n"+r.getString( R.string.Generic_Bad_RadioButton ); }
        
        // check date and time
        String date = DATE.getText().toString().trim();
        String time = TIME.getText().toString().trim();
        
        if( date.equals( Globals.Empty ) && 
                time.equals( Globals.Empty ) ) { startTime = new Date(); }
        else                               { startTime = new Date( date+ " " +time ); }

        if( //status.equals( Globals.Empty ) &&
            !cost.equals( null ) &&
            !maxPass.equals( null ) )
        {
            Globals.currentRide = new Ride();
            Globals.currentRide.DRIVERID( Globals.currentUser.ID() );
            Globals.currentRide.SRC( src );
            Globals.currentRide.DEST( dest );
            Globals.currentRide.SMOKING( SMOKING.isChecked() );
            Globals.currentRide.OFFERING( offering );
            Globals.currentRide.MAXPASS( maxPass );
            Globals.currentRide.REMPASS( maxPass );
            Globals.currentRide.COST( cost );
            Globals.currentRide.STARTTIME( startTime );

            new Async_Create().execute();
        }
        else
        {
            STATUS.setText( status );
        }
    }

    private class Async_Create extends AsyncTask<Void, Void, Boolean>
    {
        protected void onPreExecute() { before(); }
        protected Boolean doInBackground( final Void... V )
        {
            try                       { return during(); }
            catch( ParseException e ) { Globals.exception = Globals.ParseException; e.printStackTrace(); }
            catch( JSONException e )  { Globals.exception = Globals.JSONException;  e.printStackTrace(); }
            catch( IOException e )    { Globals.exception = Globals.IOException;    e.printStackTrace(); }
            return null;
        }
        protected void onPostExecute( final Boolean B ) { after(B); }
    }
    
    private void before()
    {
        STATUS.setText( r.getString( R.string.Ride_Creating ) );
    }
    
    private Boolean during() throws ClientProtocolException, JSONException, IOException
    {
        return createJSON();
    }

    private void after( final Boolean success )
    {
       if( success == null )
        {
            STATUS.setText( Globals.exception );
        }
        else if ( success )
        {
            STATUS.setText( r.getString( R.string.Ride_Create_Success ) );
            startActivityForResult( new Intent( this, Ride_Info.class ), 0 );
        }
        else
        {
            STATUS.setText( r.getString( R.string.Ride_Create_Failed ) );
        }
    }

    private Boolean createJSON() throws JSONException, ClientProtocolException, IOException
    {
        JSONObject result = Globals.getJSON( Globals.URL.addRide, Globals.currentRide.tojson() );
        Globals.selectedRide = new Ride( result.getJSONObject( Globals.JSON.ride ) );
        return result.getBoolean( Globals.JSON.success );
    }
    
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.ride_create, menu );
        return true;
    }
}