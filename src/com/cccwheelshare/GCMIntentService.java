package com.cccwheelshare;

import static com.cccwheelshare.Globals.SENDER_ID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
	
    private static final String TAG = "GCMIntentService";
    private static int id = 0;

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
//        generateNotification(context, "REGISTERED", "");
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
//            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        //String message = getString(R.string.gcm_message);
        // notifies user
//        String message = intent.getExtras().getString("TYPE");
        String data = intent.getExtras().getString( "data" );
        try{
            JSONObject ref = new JSONObject(data);
            
            generateNotification(context, data, ref.getString("activity"));
        } catch(JSONException e){ generateNotification(context, data, "JSON ERROR"); }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        //String message = getString(R.string.gcm_deleted, total);
        // notifies user
    }

    @Override
    public void onError(Context context, String errorId) {
//        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
//        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @SuppressWarnings( "deprecation" )
    private static void generateNotification(Context context, String message, String where) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        String mymessage = "";
        
        if(where.equals( "pending" )){
            mymessage = context.getResources().getString(R.string.GCM_pending_message);
        }
        else if(where.equals( "complete" )){
//            notificationIntent = new Intent(context, Ride_Completion.class);
            mymessage = context.getResources().getString(R.string.GCM_complete_message);
        }
        else if(where.equals( "escrow" )){
            mymessage = context.getResources().getString(R.string.GCM_escrow_message);
        }
        else{
            mymessage = "";
        }
        
        Notification notification = new Notification(icon, mymessage, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent;
        
        if(where.equals( "pending" )){
            notificationIntent = new Intent(context, Ride_Pending.class);
        }
        else if(where.equals( "complete" )){
//            notificationIntent = new Intent(context, Ride_Completion.class);
            notificationIntent = new Intent(context, Tabs.class);
        }
        else{
            notificationIntent = new Intent(context, Tabs.class);
        }
        
        notificationIntent.putExtra("data", message);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent intent =
                PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        
        notification.setLatestEventInfo(context, title, mymessage, intent);
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        notificationManager.notify(id++, notification);
    }

}