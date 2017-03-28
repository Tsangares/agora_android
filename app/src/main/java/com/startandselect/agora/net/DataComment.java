package com.startandselect.agora.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by astro on 2/17/17.
 */
public class DataComment implements Parcelable {
    public DataUser creator = null;
    public String text = "Default Comment";
    public int id = -1;
    public DataComment(){}
    public DataComment(JSONObject data){
        update(data);
    }
    public void update(JSONObject data){
        try{
            text = data.getString("text");
            id = data.getInt("id");
            try {
                creator = new DataUser(data.getJSONObject("creator"));
            }catch (Exception e){
                creator = null;
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public static ArrayList<DataComment> getArray(String data){
        ArrayList<DataComment> output = new ArrayList<>();
        try{
            JSONArray arr = new JSONArray(data);
            for(int i = 0; i < arr.length(); ++i){
                output.add(new DataComment(arr.getJSONObject(i)));
            }
        }catch(Exception e){
            throw new RuntimeException(e.toString());
        }
        return output;
    }
    //Parcel Attributes
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(creator, 0);
        dest.writeString(text);
        dest.writeInt(id);
    }
    public DataComment(Parcel in){
        creator = in.readParcelable(DataUser.class.getClassLoader());
        text = in.readString();
        id = in.readInt();
    }
    public static final Creator CREATOR = new Creator() {
        public DataResource createFromParcel(Parcel in) {
            return new DataResource(in);
        }
        public DataResource[] newArray(int size) {
            return new DataResource[size];
        }
    };
}
