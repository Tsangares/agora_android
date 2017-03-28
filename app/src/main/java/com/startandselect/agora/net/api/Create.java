package com.startandselect.agora.net.api;

import android.os.AsyncTask;

import com.startandselect.agora.net.ApiRequest;

/**
 * Created by astro on 2/21/17.
 */

/**
 * Untested.
 */
public abstract class Create extends Fetch {
    public AsyncTask init(String type, String text, String id) {
        return init(type, text, id, null);
    }
    public AsyncTask init(String type, String text, String id, String parent_id){
        ApiRequest api = new ApiRequest();
        api.setURL(type + "/", ApiRequest.FULL);
        api.setMethod(ApiRequest.POST);
        api.add("text", text);
        if(parent_id != null)api.add("parent_id",parent_id);
        return execute(api);
    }
}
