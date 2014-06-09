package com.cccwheelshare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class Globals extends Application
{
    final public  static String     Empty          = "";
          public  static String     exception      = Empty;
    final public  static String     JSONException  = "JSONException";
    final public  static String     IOException    = "IOException";
    final public  static String     ParseException = "ParseException";
          public  static String     SENDER_ID      = "52899871954";
          public  static User       currentUser    = new User();
          public  static User       selectedUser   = new User();
          public  static Ride       currentRide    = new Ride();
          public  static Ride       selectedRide   = new Ride();
          public  static List<Ride> MATCHED_RIDES  = new ArrayList<Ride>();
    final public  static String     CREDITCARD     = "4819133634646832";
          public  static ArrayList<Item> Basket         = new ArrayList<Item>();

    final public static class Font
    {
              public  static Boolean on     = false;
        final public  static String  mangal = "fonts/mangal.ttf";       
    }
    
    final public static class URL
    {
        final private static String ROOT         = " ";//"http://cccwheelsharetest.appspot.com";
        final public  static String addRide      = ROOT+"/addRide";
        final public  static String completeRide = ROOT+"/completeRide";
        final public  static String gcmout       = ROOT+"/gcmout";
        final public  static String getRides     = ROOT+"/getRides";
        final public  static String getUser      = ROOT+"/getUser";
        final public  static String getUsername  = ROOT+"/getUsername";
        final public  static String login        = ROOT+"/login";
        final public  static String logingoogle  = ROOT+"/logingoogle";
        final public  static String register     = ROOT+"/register";
        final public  static String updateRide   = ROOT+"/updateRide";
    }
    
    final public static class JSON
    {
        final public static String complete = "complete";
        final public static String data     = "data";
        final public static String email    = "email";
        final public static String error    = "error";
        final public static String name     = "name";
        final public static String offering = "offering";
        final public static String qty      = "qty";
        final public static String ride     = "ride";
        final public static String rideId   = "rideId";
        final public static String rides    = "rides";
        final public static String success  = "success";
        final public static String toUserId = "toUserId";
        final public static String user     = "user";
        final public static String userId   = "userId";
    }
    
    public static final String[] CITY_LIST = { "Davis",
                                               "Hayward",
                                               "Fremont",
                                               "Los Angeles",
                                               "Sacramento",
                                               "San Francisco",
                                               "San Jose",
                                               "Vacaville" };
    
    public static String[] SORT_TYPES = { "From city (ascending)",
                                          "From city (descending)",
                                          "To city (ascending)",
                                          "To city (descending)",
                                          "Date/Time (ascending)",
                                          "Date/Time (descending)",
                                          "Cost (ascending)",
                                          "Cost (descending)" };

    public static AlertDialog createAlertDialog( AlertDialog.Builder listBuilder,
                                                 final CharSequence[] items,
                                                 final String title,
                                                 final EditText editText )
    {
        listBuilder.setTitle( title );
        listBuilder.setItems(
            items,
            new DialogInterface.OnClickListener()
            {
                public void onClick( DialogInterface dialog, int item )
                {
                    editText.setText( items[item] );
                }
            }
        );
        
        return listBuilder.create();
    }
    
    public static JSONObject getJSON( final String uri, final JSONObject json ) throws ClientProtocolException, IOException, JSONException
    {    
        final int timeoutConnection = 4000,
                  timeoutSocket     = 6000;
        
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout( httpParameters, timeoutConnection );
        HttpConnectionParams.setSoTimeout( httpParameters, timeoutSocket );

        HttpPost httpPost = new HttpPost( uri );
        StringEntity stringEntity = new StringEntity( json.toString() );  
        stringEntity.setContentType( new BasicHeader( HTTP.CONTENT_TYPE, "application/json" ) );
        httpPost.setEntity( stringEntity );

        HttpResponse response = new DefaultHttpClient( httpParameters ).execute( httpPost );
        
        if(response == null) return null;
        
        HttpEntity entity = response.getEntity();
        
        return entity.equals( null ) ? null
                                     : new JSONObject( EntityUtils.toString( entity ) );
    }
    
    public static final void setAppFont( ViewGroup mContainer, final Typeface mFont )
    {
        if( mContainer == null || mFont == null )
            return;

        final int mCount = mContainer.getChildCount();

        for( int i = 0; i < mCount; ++i )
        {
            final View mChild = mContainer.getChildAt( i );
            if(      mChild instanceof TextView )  { ( (TextView) mChild ).setTypeface( mFont ); }
            else if( mChild instanceof ViewGroup ) { setAppFont( (ViewGroup) mChild, mFont ); }
        }
    }
}
