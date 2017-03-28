package com.startandselect.agora.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by astro on 2/17/17.
 */
public class DataModule implements Parcelable {
    public DataUser creator = null;
    public ArrayList<DataComment> comments = null;
    public String text = "Default Module Text";
    public String title = "Default Module Title";
    public int id = -1;
    public DataModule(){}
    public DataModule(JSONObject data){
        update(data);
    }
    public void update(JSONObject data){
        try{
            text = data.getString("text");
            title = data.getString("title");
            id = data.getInt("id");
            try {
                creator = new DataUser(data.getJSONObject("creator"));
            }catch (Exception e){
                creator = null;
            }
            try{
                comments = DataComment.getArray(data.getString("comments"));
            }catch (Exception e){
                comments = new ArrayList<>();
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public static ArrayList<DataModule> getArray(String data){
        ArrayList<DataModule> output = new ArrayList<>();
        try{
            JSONArray arr = new JSONArray(data);
            for(int i = 0; i < arr.length(); ++i){
                output.add(new DataModule(arr.getJSONObject(i)));
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
        dest.writeParcelableArray(comments.toArray(new DataModule[comments.size()]),0);
        dest.writeString(text);
        dest.writeString(title);
        dest.writeInt(id);
    }
    public DataModule(Parcel in){
        creator = in.readParcelable(DataUser.class.getClassLoader());
        comments = new ArrayList<>(Arrays.asList((DataComment[])in.readParcelableArray(DataComment.class.getClassLoader())));
        text = in.readString();
        title = in.readString();
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
