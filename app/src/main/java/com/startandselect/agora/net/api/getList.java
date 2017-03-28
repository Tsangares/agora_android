package com.startandselect.agora.net.api;

import android.os.AsyncTask;

import com.startandselect.agora.net.ApiRequest;

/**
 * Created by astro on 2/21/17.
 */
public abstract class GetList extends Fetch {
    /**
     * ^TESTED:%UNTESTED
     * ^@param limit
     * ^@param offset
     * %@param order
     * %@param filter
     * @return
     */
    public AsyncTask init(String type){
        return init(type,0,20,null);
    }
    public AsyncTask init(String type, Integer offset){
        return init(type,offset,20,null);
    }
    public AsyncTask init(String type, Integer offset,Integer limit, Integer order){
        ApiRequest api = new ApiRequest();
        api.setURL(type + "/", ApiRequest.FULL);
        api.setMethod(ApiRequest.GET);
        api.add("limit", limit.toString());
        api.add("offset", offset.toString());
        api.add("order", getOrder(order));
        return execute(api);
    }
}
