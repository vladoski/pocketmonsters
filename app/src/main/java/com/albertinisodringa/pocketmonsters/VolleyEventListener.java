package com.albertinisodringa.pocketmonsters;

/**
 * The interface Volley event listener.
 * Used as callback for the volley async requests to the API.
 */
public interface VolleyEventListener {
    /**
     * On success callback.
     *
     * @param returnFromCallback the return from callback
     */
    void onSuccess(Object returnFromCallback);

    /**
     * On failure callback.
     *
     * @param error the error
     */
    void onFailure(Exception error);
}
