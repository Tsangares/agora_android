package com.startandselect.agora.account;

import android.content.Context;
import android.widget.Toast;

import com.startandselect.agora.net.DataUser;

/**
 * Created by Examiner on 10/24/16.
 */
public final class Profile {
    private static Boolean loggedin = false; //I want to delete this.
    private static String username = "Doe";
    private static String apikey = null;
    private static Integer id = -2;
    private static Integer myQuestions = 0;
    private static Integer myResponses = 0;
    private static Integer myVotes = 0;
    private static Integer QuestionVotes = 0;
    private static Integer ResponseVotes = 0;
    private static Integer TotalResponses = 0;

    private Profile(){}

    public static void initialize(Context theContext, String data){
        try{
            initialize(new DataUser(data));
        }catch (Exception e){
            Toast.makeText(theContext,"Failed to get profile data.", Toast.LENGTH_LONG).show();
            e.toString();
        }
    }
    public static void initialize(DataUser data){
        if(data.key!=null)loggedin = true;
        setProfileName(data.username);
        setMyQuestions(data.my_questions);
        setMyResponses(data.my_responses);
        setMyVotes(data.my_votes);
        setApiKey(data.key);
        setQuestionVotes(-1);
        setResponseVotes(-1);
        setTotalResponses(-1);
    }
    public static void clear(){
        loggedin = false;
        setApiKey(null);
        setId(-1);
        setTheme(0);
        setProfileName("Doe");
        setMyQuestions(0);
        setMyResponses(0);
        setMyVotes(0);
        setQuestionVotes(0);
        setResponseVotes(0);
        setTotalResponses(0);
    }
    public static Boolean getLoggedIn(){return loggedin;}
    public static void setApiKey(String key){apikey = key;}
    public static String getApiKey(){return apikey;}
    public static void setTheme(Integer input){id = input;}
    public static Integer getTheme(){return id;}
    public static void setId(Integer input){id = input;}
    public static Integer getId(){return id;}
    public static void setUsername(String input){username = input;}
    public static String getUsername(){return username;}
    public static void setMyQuestions(String input){
        setMyQuestions(Integer.parseInt(input));
    }
    public static void setMyQuestions(Integer input){
        myQuestions = input;
    }
    public static void addMyQuestions(Integer input){
        myQuestions+=input;
    }
    public static Integer getMyQuestions(){
        return myQuestions;
    }
    public static void setMyResponses(String input){
        setMyResponses(Integer.parseInt(input));
    }
    public static void setMyResponses(Integer input){
        myResponses = input;
    }
    public static void addMyResponses(Integer input){
        myResponses += input;
    }
    public static Integer getMyResponses(){
        return myResponses;
    }
    public static void setMyVotes(String input){
        setMyVotes(Integer.parseInt(input));
    }
    public static void setMyVotes(Integer input){
        myVotes = input;
    }
    public static void addMyVotes(Integer input){
        myVotes += input;
    }
    public static Integer getMyVotes(){
        return myVotes;
    }
    public static void setQuestionVotes(String input){
        setQuestionVotes(Integer.parseInt(input));
    }
    public static void setQuestionVotes(Integer input){
        QuestionVotes = input;
    }
    public static void addQuestionVotes(Integer input){
        QuestionVotes += input;
    }
    public static Integer getQuestionVotes(){
        return QuestionVotes;
    }
    public static void setResponseVotes(String input){
        setResponseVotes(Integer.parseInt(input));
    }
    public static void setResponseVotes(Integer input){
        ResponseVotes = input;
    }
    public static void addResponseVotes(Integer input){
        ResponseVotes += input;
    }
    public static Integer getResponseVotes(){
        return ResponseVotes;
    }
    public static void setTotalResponses(String input){
        setTotalResponses(Integer.parseInt(input));
    }
    public static void setTotalResponses(Integer input){
        TotalResponses = input;
    }
    public static void addTotalResponses(Integer input){
        TotalResponses += input;
    }
    public static Integer getTotalResponses(){
        return TotalResponses;
    }
    public static void setProfileName(String newName){
        username = newName;
    }
}
