package com.startandselect.agora.net;

/**
 * Created by astro on 2/17/17.
 */
public class RestParam{
    public String output = "";
    public String json = "{ ";
    public int count = 0;
    public void add(String key, String value){
        if(count++ != 0)output += "&";
        output += key + "=" + value;
        json += "\"" + key+"\"" +": "+"\""+value+"\"" + ",";
    }
    public void put(String key, String value){
        add(key, value);
    }
    @Override
    public final String toString(){
        return output;
    }
    public final String get(){
        return output;
    }
    public final String toJSON(){
        return json.substring(0,json.length()-1)+" }";
    }
}
