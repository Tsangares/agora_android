package com.startandselect.agora.net.api;

import android.os.AsyncTask;

import com.startandselect.agora.net.ApiRequest;

/**
 * Created by astro on 2/21/17.
 */

/**
 * Untested.
 */
public abstract class Edit extends Fetch {
    public AsyncTask init(String type, String text, Integer id){
        ApiRequest api = new ApiRequest();
        api.setURL(type + "/" + id + "/", ApiRequest.FULL);
        api.setMethod(ApiRequest.PATCH);
        api.add("text", text);
        return execute(api);
    }
}
