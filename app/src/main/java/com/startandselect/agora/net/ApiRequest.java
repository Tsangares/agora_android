package com.startandselect.agora.net;

import java.net.URL;

/**
 * Created by astro on 2/17/17.
 */ //ApiRequest is supposed to hold all of the info needed to setup the headers to a REST request
//I am not sure what append, replace
public class ApiRequest{
    public String URL = "";

    //ENDPOINTS
    private final static String HOST = "http://api.iex.ist";
    private final static String URL_FULL = HOST +"/full/";
    private final static String URL_MIN = HOST +"/min/";

    //METHODS
    public final static String GET = "GET";
    public final static String POST = "POST";
    public final static String PATCH = "PATCH";

    //HELPER VARIABLES
    public final static int FULL = 0;
    public final static int MIN = 1;
    public final static int NONE = 2;
    public final static int BASE = 3;
    public final static int APPEND = 99;
    public final static int REPLACE = 100;

    //METHOD PLACEHOLDER
    private String method = null;

    //META INFO (used in making infinite lists)
    private int handle = REPLACE;

    //REQUEST DATA CONTAINER
    public RestParam data = null;

    public ApiRequest(){}
    public ApiRequest(String _URL){
        setURL(_URL);
    }
    public ApiRequest(String _URL, int _url_type){
        setURL(_URL, _url_type);
    }
    public ApiRequest(String _URL, int _url_type, int _handle){
        setURL(_URL, _url_type);
        setHandle(_handle);
    }
    public void setMethod(String input){
        method = input;
    }
    public String getMethod(){
        return method;
    }
    public String setURL(String _URL){
        return setURL(_URL, FULL);
    }
    public String setURL(String _URL, int type){
        switch (type) {
            default:
            case FULL:
                URL = URL_FULL+_URL;
                break;
            case MIN:
                URL = URL_MIN+_URL;
                break;
            case BASE:
                URL = HOST +_URL;
                break;
            case NONE:
                URL = _URL;
                break;
        }
        return URL;
    }
    public boolean hasData(){
        return (data != null && data.count >= 0);
    }
    public String getData(){
        if(method == GET){
            return data.toString();
        }else{
            return data.toJSON();
        }
    }
    public java.net.URL getURL(){
        try {
            if (method.equals(ApiRequest.GET)) {
                return new URL(getUrlGET());
            } else {
                return new URL(URL);
            }
        }catch (Exception e){
            e.toString();
            return null;
        }
    }
    public String getUrlGET(){
        return URL + "?" + data.toString();
    }
    public void setHandle(int _handle){
        handle = _handle;
    }
    public int getHandle(){
        return handle;
    }
    public RestParam add(String key, String value){
        if(data == null){
            data = new RestParam();
        }
        data.add(key,value);
        return data;
    }
    public String toJSON(){
        return data.toJSON();
    }
    public String toString(){return data.toString();}
}
