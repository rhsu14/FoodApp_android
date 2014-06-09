package com.cccwheelshare;

import com.google.android.gcm.GCMRegistrar;
import com.cccwheelshare.Globals;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
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

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String TAG = "GCM";
    
    //TODO SWITCH FROM TESTING
    private static final String SERVER_URL = "http://cccwheelshare.appspot.com";

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register(final Context context, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/gcmreg";
        JSONObject params = new JSONObject();
        try{
            params.put("notifId", regId);
            params.put( "userId", Globals.currentUser.ID() );
        } catch(Exception e){
            e.printStackTrace();
        }
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
                return true;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
//                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
            catch( JSONException e )
            {
                Log.e(TAG, "JSON exception @ " + i, e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//        String message = context.getString(R.string.server_register_error,
//                MAX_ATTEMPTS);
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
           // post(serverUrl, params);
            post(serverUrl, new JSONObject());
            GCMRegistrar.setRegisteredOnServer(context, false);
//            String message = context.getString(R.string.server_unregistered);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
//            String message = context.getString(R.string.server_unregister_error,
//                    e.getMessage());
        }
        catch( JSONException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     * @throws JSONException 
     */
    private static void post(String endpoint, JSONObject params)
            throws IOException, JSONException {
//        URL url;
//        try {
//            url = new URL(endpoint);
//        } catch (MalformedURLException e) {
//            throw new IllegalArgumentException("invalid url: " + endpoint);
//        }
//        StringBuilder bodyBuilder = new StringBuilder();
//        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
//        // constructs the POST body using the parameters
//        while (iterator.hasNext()) {
//            Entry<String, String> param = iterator.next();
//            bodyBuilder.append(param.getKey()).append('=')
//                    .append(param.getValue());
//            if (iterator.hasNext()) {
//                bodyBuilder.append('&');
//            }
//        }
//        String body = bodyBuilder.toString();
//        Log.v(TAG, "Posting '" + body + "' to " + url);
//        byte[] bytes = body.getBytes();
//        HttpURLConnection conn = null;
//        try {
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//            conn.setFixedLengthStreamingMode(bytes.length);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type",
//                    "application/x-www-form-urlencoded;charset=UTF-8");
//            // post the request
//            OutputStream out = conn.getOutputStream();
//            out.write(bytes);
//            out.close();
//            // handle the response
//            int status = conn.getResponseCode();
//            if (status != 200) {
//              throw new IOException("Post failed with error code " + status);
//            }
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout( httpParameters, 4000 );
        HttpConnectionParams.setSoTimeout( httpParameters, 6000 );

        HttpPost httpPost = new HttpPost( endpoint );
        StringEntity stringEntity = new StringEntity( params.toString() );  
        stringEntity.setContentType( new BasicHeader( HTTP.CONTENT_TYPE, "application/json" ) );
        httpPost.setEntity( stringEntity );

        HttpEntity entity = new DefaultHttpClient( httpParameters ).execute( httpPost ).getEntity();
        
        if(entity != null)
        {
            String blah = EntityUtils.toString(entity);
            Log.i(TAG, blah);
            //JSONObject result = new JSONObject(blah);

//            if( result.getBoolean("success") ) // USER/PASS EXISTS AND VER
//            {
//                Log.i(TAG, result.toString());
//            }
            // ELSE NEW USER && email sent || NOT VER USER && check email
        }
      }
}