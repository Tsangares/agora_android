package com.startandselect.agora.net.api;

import android.os.AsyncTask;

import com.startandselect.agora.net.ApiRequest;

/**
 * Created by astro on 2/21/17.
 */

/**
 * 170317 - init untested.
 */
public class Account extends Fetch {
    public final String REGISTER = "register";
    public final String LOGIN = "login";
    public AsyncTask init(String username, String password, String type){
        ApiRequest api = new ApiRequest();
        api.setURL("user/" + type, ApiRequest.FULL);
        api.setMethod(ApiRequest.POST);
        api.add("username",username);
        api.add("password",password);
        return execute(api);
    }
    public AsyncTask login(String username, String password){
        return init(username,password,LOGIN);
    }
    public AsyncTask register(String username, String password){
        return init(username,password,REGISTER);
    }

}
