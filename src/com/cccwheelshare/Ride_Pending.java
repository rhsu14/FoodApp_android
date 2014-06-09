package com.cccwheelshare;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Ride_Pending extends Activity
{
    
    private TextView INFO;
    private JSONObject data;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.ride_pending );

        Globals.setAppFont( (ViewGroup) findViewById( R.id.Ride_Pending ),
                            Globals.Font.on ? Typeface.createFromAsset( getAssets(), Globals.Font.mangal )
                                            : Typeface.DEFAULT );
        
        Bundle b = getIntent().getExtras();
        
        INFO = (TextView) findViewById( R.id.Ride_Pending_Text );
        if(b != null ){
            try
            {
                data = new JSONObject(b.getString( "data" ));
                INFO.setText( data.getString("userId") + " " + getApplicationContext().getString(R.string.Pending_Text) + " " + data.getString("rideId") );
            }
            catch( JSONException e )
            {
                INFO.setText( "JSONException" + e );
            }
        }
        else
            INFO.setText( getApplicationContext().getString(R.string.Pending_Error) );
    }
    
    @Override
    protected void onNewIntent (Intent intent){
        
        Bundle b = getIntent().getExtras();
        
        INFO = (TextView) findViewById( R.id.Ride_Pending_Text );
        if(b != null ){
            try
            {
                data = new JSONObject(b.getString( "data" ));
                INFO.setText( data.getString("userId") + " " + getApplicationContext().getString(R.string.Pending_Text) + " " + data.getString("rideId") );
            }
            catch( JSONException e )
            {
                INFO.setText( "JSONException" + e );
            }
        }
        else
            INFO.setText( getApplicationContext().getString(R.string.Pending_Error) );
        
    }
    
    private class Async extends AsyncTask<View, Void, Boolean>
    {
        
        protected Boolean doInBackground( final View... args )
        {
            try                       { return updateJSON(); }
            catch( ParseException e ) { e.printStackTrace(); }
            catch( JSONException e )  { e.printStackTrace(); }
            catch( IOException e )    { e.printStackTrace(); }
            
            return true;
        }
        
        @Override
        protected void onPostExecute( final Boolean success )
        {
            if( success )
            {
                Toast.makeText(getApplicationContext(), R.string.Accept_User, Toast.LENGTH_LONG).show();
                proceed();
            }
        }
    }
    
    private void proceed()
    {
        startActivityForResult( new Intent( this, Ride_Info.class ), 0 );
    }
    
    private Boolean updateJSON() throws ClientProtocolException, IOException, JSONException
    {
        // Slight change - since backend booking happens now on the acceptor's side, we have to use a different json object in the call
        
        JSONObject json = new JSONObject();
        json.put("rideId", Integer.parseInt(data.getString("rideId")));
        json.put("userId", Integer.parseInt(data.getString("userId")));
        json.put("offering", data.getBoolean("offering"));
        
        JSONObject result = Globals.getJSON( Globals.URL.updateRide, json );
        
        Globals.selectedRide = new Ride( result.getJSONObject( "ride" ) );

        return result.getBoolean( "success" );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.ride_pending, menu );
        return true;
    }
    
    public void accept(View v){
        if(data != null){
            new Async().execute(v);
            // notify bookee that they have been accepted?
        }
        else{
            //Future proofing in case we need an else
            INFO.setText("ACCEPT ERROR");
        }
    }
    
    public void deny(View v){
        if(data != null){
            // notify bookee that they have been denied?
            Toast.makeText(this, R.string.Deny_User, Toast.LENGTH_LONG).show();
        }
        else{
            //Future proofing in case we need an else
        }
    }

}
