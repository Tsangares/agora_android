package com.startandselect.agora.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by astro on 2/17/17.
 */
public class DataUser implements Parcelable {
    public String username = "Doe";
    public String key = "";
    public Integer total_questions = 0;
    public Integer total_responses = 0;
    public Integer total_votes = 0;
    public Integer my_questions = 0;
    public Integer my_responses = 0;
    public Integer my_votes = 0;
    public DataUser(){}
    public DataUser(String data){
        try {
            update(new JSONObject(data));
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public DataUser(JSONObject data){
        update(data);
    }
    public void update(JSONObject data){
        try{
            username = data.getString("username");
            my_questions = data.getInt("total_questions");
            my_responses = data.getInt("total_responses");
            my_votes = data.getInt("total_votes");
            try{
                key = data.getString("key");
            }catch (Exception e){
                key = null;
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    //Parcel Attributes
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeInt(total_questions);
        dest.writeInt(total_responses);
        dest.writeInt(total_votes);
    }
    public DataUser(Parcel in){
        username = in.readString();
        total_questions = in.readInt();
        total_responses = in.readInt();
        total_votes = in.readInt();
    }
    public static final Creator CREATOR = new Creator() {
        @org.jetbrains.annotations.Contract("_ -> !null")
        public DataUser createFromParcel(Parcel in) {
            return new DataUser(in);
        }
        public DataUser[] newArray(int size) {
            return new DataUser[size];
        }
    };
}
