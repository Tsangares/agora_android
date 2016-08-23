package com.startandselect.agora;

import com.google.android.gms.common.api.Api;

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
class ApiRequest{
    public String URL = "";
    public final static String URL_BASE = "https://startandselect.com/api/";
    public final static String URL_FULL = URL_BASE+"full/";
    public final static String URL_MIN = URL_BASE+"min/";
    public final static int FULL = 0;
    public final static int MIN = 1;
    public RestParam params = null;
    public ApiRequest(){

    }
    public ApiRequest(String _URL){
        setURL(_URL);
    }
    public ApiRequest(String _URL, int _type){
        setURL(_URL, _type);
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
        }
        return URL;
    }
    public RestParam add(String key, String value){
        if(params == null){
            params = new RestParam();
        }
        params.add(key,value);
        return params;
    }
}
class RestParam{
    public String output = "";
    public int count = 0;
    public void add(String key, String value){
        if(count++ != 0)output += "&";
        output += key + "=" + value;
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