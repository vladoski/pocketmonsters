package com.albertinisodringa.pocketmonsters;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;

/**
 * Api error handler
 *
 * Handles all the errors that are thrown by ApiHandler
 */
public class ApiErrorHandler {
    private static final String NO_CONNECTION_TO_SERVER = "Unable to reach the server.\nCheck your internet settings.";
    private static final String WRONG_RESPONSE_FROM_SERVER = "Something wrong happened with the server, please retry.";
    private static final String GENERIC_ERROR = "Oopsie, something bad happened!";


    public static void handle(Exception exception, Context context) {

        if (exception instanceof VolleyError) {
            VolleyError volleyError = (VolleyError) exception;

            Toast.makeText(context, NO_CONNECTION_TO_SERVER, Toast.LENGTH_SHORT).show();
            Log.d(ApiErrorHandler.class.getName(), NO_CONNECTION_TO_SERVER);

            if (volleyError.networkResponse != null) {
                String body;
                final String statusCode = String.valueOf(volleyError.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(volleyError.networkResponse.data, "UTF-8");

                    Toast.makeText(context, WRONG_RESPONSE_FROM_SERVER, Toast.LENGTH_SHORT).show();
                    Log.d(ApiErrorHandler.class.getName(), body);
                } catch (Exception e) {
                    Log.d(ApiErrorHandler.class.getName(), e.getMessage());
                }
            }
        } else if (exception instanceof JSONException) {
            // TODO: find the bug, why is this exception being thrown so many times?
            Log.d(ApiErrorHandler.class.getName(), exception.getMessage());
        } else {
            Toast.makeText(context, NO_CONNECTION_TO_SERVER, Toast.LENGTH_SHORT).show();
            Log.d(ApiErrorHandler.class.getName(), exception.getMessage());
        }

    }
}
