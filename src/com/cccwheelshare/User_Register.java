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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class User_Register extends Activity
{
    private static EditText  EMAIL;
    private static EditText  NAME;
    private static EditText  PASS;
    private static EditText  PASSV;
    private static Resources r;
    private static String    status;
    private static TextView  STATUS;

    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.user_register );

        Globals.setAppFont( (ViewGroup) findViewById( R.id.User_Register ),
                            Globals.Font.on ? Typeface.createFromAsset( getAssets(), Globals.Font.mangal )
                                            : Typeface.DEFAULT );

        r      = this.getResources();
        EMAIL  = (EditText) findViewById( R.id.EMAIL_Field );
        STATUS = (TextView) findViewById( R.id.STATUS );
        NAME   = (EditText) findViewById( R.id.NAME_Field );
        PASS   = (EditText) findViewById( R.id.PASS_Field );
        PASSV  = (EditText) findViewById( R.id.PASSV_Field );
        
        EMAIL.setText( Globals.currentUser.EMAIL() );
    }

    public void register( final View v )
    {
        STATUS.setText( r.getString( R.string.User_Registering ) );
        status = Globals.Empty;

        String name  = NAME.getText().toString().trim();
        if( name.equals(Globals.Empty) ) { status += "\n"+r.getString( R.string.User_Bad_Name ); }
        
        String email = EMAIL.getText().toString().trim();
        if( email.equals(Globals.Empty) ) { status += "\n"+r.getString( R.string.User_Bad_Email ); }
        
        String pass  = PASS.getText().toString().trim();
        if( pass.equals(Globals.Empty) ) { status += "\n"+r.getString( R.string.User_Bad_Password ); }
        
        String passv = PASSV.getText().toString().trim();      
        if( passv.equals(Globals.Empty) )           { status += "\n"+r.getString( R.string.User_Bad_Password_Verify ); }
        else if( !pass.equals( passv ) ) { status += "\n"+r.getString( R.string.User_Bad_Password_Match ); }
        
        if( status.equals( Globals.Empty ) )
        {
            Globals.currentUser = new User();
            Globals.currentUser.EMAIL( email );
            Globals.currentUser.PASSWORD( pass );
            Globals.currentUser.NAME( name );

            new async_register().execute();
        }
        else
        {
            STATUS.setText( status );
        }
    }

    private class async_register extends AsyncTask<Void, Void, Boolean>
    {
        protected void    onPostExecute( final Boolean B )  { after(B); }
        protected Boolean doInBackground( final Void... V )
        {
            try                       { return during(); }
            catch( ParseException e ) { Globals.exception = Globals.ParseException; e.printStackTrace(); }
            catch( JSONException e )  { Globals.exception = Globals.JSONException;  e.printStackTrace(); }
            catch( IOException e )    { Globals.exception = Globals.IOException;    e.printStackTrace(); }
            return null;
        }
    }
    
    private Boolean during() throws ClientProtocolException, JSONException, IOException
    {
        return registerJSON();
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
            PASS.setText( Globals.Empty );
            PASSV.setText( Globals.Empty );
            startActivityForResult( new Intent( this, User_Login.class ), 0 );
        }
        else
        {
            STATUS.setText( r.getString( R.string.Try_Again_Text ) );
        }
    }

    private Boolean registerJSON() throws JSONException, ClientProtocolException, IOException
    {
        status = Globals.Empty;
        JSONObject json = Globals.currentUser.tojson();
        
        JSONObject result = Globals.getJSON( Globals.URL.register, json );
        
        if(result.getBoolean( Globals.JSON.success ))
        {
            Globals.currentUser = new User( result );
            Globals.currentUser.ID( result.getInt( Globals.JSON.userId ) );
        }
        status += result.toString();
        return result.getBoolean( Globals.JSON.success );
    }

    public void login( final View v )
    {
        Globals.currentUser.EMAIL( EMAIL.getText().toString().trim() );
        startActivityForResult( new Intent( this, User_Login.class ), 0 );
    }

    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.user_register, menu );
        return true;
    }
}
