package com.startandselect.agora.net.api;

import android.os.AsyncTask;

import com.startandselect.agora.net.ApiRequest;

/**
 * Created by astro on 2/21/17.
 */
public abstract class GetDetail extends Fetch {
    public AsyncTask init(String type, String id){
        ApiRequest api = new ApiRequest();
        api.setURL(type + "/"+id + "/", ApiRequest.FULL);
        api.setMethod(ApiRequest.GET);
        return execute(api);
    }
}
