package com.startandselect.agora;

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