package com.startandselect.agora;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Apollonian on 5/12/2016.
 */
public class Common {
    public static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

}
class EndOfList extends LinearLayout {
    public static final int LOADING = 1;
    public static final int END = 2;

    private ProgressBar loading = null;
    private TextView list_end = null;

    public EndOfList(Context c){
        super(c);
        construct();
    }
    public EndOfList(Context c, int input){
        super(c);
        construct();
        switch (input){
            default:
            case LOADING:
                setLoading();
                break;
            case END:
                setEnd(null);
                break;
        }
    }
    public void construct(){
        setGravity(Gravity.CENTER_HORIZONTAL);
        setPadding(3,3,3,3);
        loading = new ProgressBar(getContext());
        loading.setIndeterminate(true);
        loading.setClickable(false);
        list_end = new TextView(getContext());
        list_end.setClickable(false);
        list_end.setText("No More Questions");
        list_end.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    public void setLoading(){
        //Check to see if view is already there?
        removeAllViews();
        addView(loading);
    }
    public void setEnd(String input){
        if(input == null) input = "No More Questions";
        list_end.setText(input);
        removeAllViews();
        addView(list_end);
    }
}
class ApiRequest{
    public String URL = "";
    private final static String URL_BASE = "http://api.iex.ist";
    private final static String URL_FULL = URL_BASE+"/full/";
    private final static String URL_MIN = URL_BASE+"/min/";
    public final static int FULL = 0;
    public final static int MIN = 1;
    public final static int NONE = 2;
    public final static int BASE = 3;
    public final static int APPEND = 99;
    public final static int REPLACE = 100;
    public final static String GET = "GET";
    public final static String POST = "POST";
    private String method = null;
    private int handle = REPLACE;
    public RestParam params = null;
    public ApiRequest(){

    }
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
                URL = URL_BASE+_URL;
                break;
            case NONE:
                URL = _URL;
                break;
        }
        return URL;
    }
    public String getUrlGET(){
        return URL + "?" + params.toString();
    }
    public void setHandle(int _handle){
        handle = _handle;
    }
    public int getHandle(){
        return handle;
    }
    public RestParam add(String key, String value){
        if(params == null){
            params = new RestParam();
        }
        params.add(key,value);
        return params;
    }
    public String toJSON(){
        return params.toJSON();
    }
    public String toString(){return params.toString();}
}
class RestParam{
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
interface OnAccountListener{
    void setMyQuestions(String input);
    void addMyQuestions(Integer input);
    Integer getMyQuestions();
    void setMyResponses(String input);
    void addMyResponses(Integer input);
    Integer getMyResponses();
    void setMyVotes(String input);
    void addMyVotes(Integer input);
    Integer getMyVotes();
    void setQuestionVotes(String input);
    void addQuestionVotes(Integer input);
    Integer getQuestionVotes();
    void setResponseVotes(String input);
    void addResponseVotes(Integer input);
    Integer getResponseVotes();
    void setTotalResponses(String input);
    void addTotalResponses(Integer input);
    Integer getTotalResponses();
    void setProfileName(String newName);
    Integer getUser_id();
}
interface OnAgoraListener{
    void setFilterMode(int filterMode);
    void setFilterTags(String filterTags);
    void setSearchQuery(String searchQuery);
    String getFilterTags();
    Integer getFilterMode();
    String getSearchQuery();
}