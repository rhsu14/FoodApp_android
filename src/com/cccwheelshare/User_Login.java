package com.cccwheelshare;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class User_Login extends Activity
{ 
    private static EditText     EMAIL;
    private static EditText     PASS;
    private static Resources    r;
    private static TextView     STATUS;
    private static ToggleButton FONT;

    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.user_login );

        Globals.setAppFont( (ViewGroup) findViewById( R.id.User_Login ),
                            Globals.Font.on ? Typeface.createFromAsset( getAssets(), Globals.Font.mangal )
                                            : Typeface.DEFAULT );

        r       = this.getResources();
        EMAIL   = (EditText)     findViewById( R.id.EMAIL_Field );
        PASS    = (EditText)     findViewById( R.id.PASS_Field );
        STATUS  = (TextView)     findViewById( R.id.STATUS );
        FONT    = (ToggleButton) findViewById( R.id.Change_Font );

        EMAIL.setText( Globals.currentUser.EMAIL() );
        FONT.setChecked( Globals.Font.on );
        
       
    }
    


    public void login( final View v )
    {
        STATUS.setText( r.getString( R.string.User_Logging_In ) );

        Globals.currentUser = new User();
        Globals.currentUser.EMAIL( EMAIL.getText().toString().trim() );
        Globals.currentUser.PASSWORD( PASS.getText().toString().trim() );
        
        startActivityForResult( new Intent( this, Restaurant_List.class ), 0 );
        //new Async_Login().execute( Globals.URL.logingoogle );
    }
    
    private class Async_Login extends AsyncTask<String, Void, Boolean>
    {
        protected void    onPostExecute( final Boolean B )  { after(B); }
        protected Boolean doInBackground( final String... S )
        {
            try                       { return during(S[0]); }
            catch( ParseException e ) { Globals.exception = Globals.ParseException;
                                        e.printStackTrace(); }
            catch( JSONException e )  { Globals.exception = Globals.JSONException;
                                        e.printStackTrace(); }
            catch( IOException e )    { Globals.exception = Globals.IOException;
                                        e.printStackTrace(); }
            return null;
        }
    }
    
    private Boolean during( final String URL ) throws ClientProtocolException, IOException, JSONException
    {
        return loginJSON(URL); 
    }
    
    private void after( final Boolean success )
    {
        if( success == null )
        {
            STATUS.setText( Globals.exception );
        }
        else if ( success )
        {
            STATUS.setText( r.getString( R.string.Generic_Success ) );

            startActivityForResult( new Intent( this, Tabs.class ), 0 );
        }
        else
        {
            STATUS.setText( r.getString( R.string.Try_Again_Text ) );
        }
    }

    private Boolean loginJSON( final String url ) throws ClientProtocolException, IOException, JSONException
    {
        JSONObject json = Globals.currentUser.tojson();
        
        JSONObject result = Globals.getJSON( url, json );
        if (result.getBoolean( Globals.JSON.success ))
        {
            Globals.currentUser.ID( result.getInt( Globals.JSON.userId ) );
            Globals.currentUser = new User( result );
        }
        return result.getBoolean( Globals.JSON.success );
    }
    
    public void toggleFont( final View v )
    {
        Globals.Font.on = FONT.isChecked();
        startActivityForResult( new Intent( this, User_Login.class ), 0 );
    }

    public void register( final View v )
    {
        Globals.currentUser.EMAIL( EMAIL.getText().toString().trim() );
        startActivityForResult( new Intent( this, User_Register.class ), 0 );
    }
    
    public boolean onCreateOptionsMenu( Menu menu )
    {
        //getMenuInflater().inflate( R.menu.user_login, menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      // action with ID action_refresh was selected
      case R.id.restaurants:
          startActivityForResult( new Intent( this, Restaurant_List.class ), 0 );
        break;
      // action with ID action_settings was selected
      case R.id.checkout:
          startActivityForResult( new Intent( this, Basket.class ), 0 );
        break;
      default:
        break;
      }

      return true;
    } 
}
