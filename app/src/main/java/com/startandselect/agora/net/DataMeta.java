package com.startandselect.agora.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Examiner on 8/21/16.
 */

public class DataMeta implements Parcelable{
    public String next;
    public String previous;
    public int limit;
    public int offset;
    public int total_count;
    public DataMeta(){}
    public DataMeta(JSONObject data){
        update(data);
    }
    public void update(JSONObject data){
        try{
            next = data.getString("next");
            previous = data.getString("previous");
            limit = data.getInt("limit");
            offset = data.getInt("offset");
            total_count = data.getInt("total_count");
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
        dest.writeString(next);
        dest.writeString(previous);
        dest.writeInt(limit);
        dest.writeInt(offset);
        dest.writeInt(total_count);
    }
    public DataMeta(Parcel in){
        next = in.readString();
        previous = in.readString();
        limit = in.readInt();
        offset = in.readInt();
        total_count = in.readInt();
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataMeta createFromParcel(Parcel in) {
            return new DataMeta(in);
        }
        public DataMeta[] newArray(int size) {
            return new DataMeta[size];
        }
    };
}

