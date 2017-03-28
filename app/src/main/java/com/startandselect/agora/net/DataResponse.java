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
public class DataResponse implements Parcelable {
    public DataUser creator = null;
    public ArrayList<DataModule> modules= new ArrayList<>();
    public String text = "Default Response";
    public int id = -1;

    public DataResponse(){}
    public DataResponse(JSONObject data){
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
            try{
                modules = DataModule.getArray(data.getString("modules"));
            }catch (Exception e){
                modules = new ArrayList<>();
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public static ArrayList<DataResponse> getArray(String data){
        ArrayList<DataResponse> output = new ArrayList<>();
        try{
            JSONArray arr = new JSONArray(data);
            for(int i = 0; i < arr.length(); ++i){
                output.add(new DataResponse(arr.getJSONObject(i)));
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
        dest.writeParcelableArray(modules.toArray(new DataModule[modules.size()]),0);
        dest.writeString(text);
        dest.writeInt(id);
    }
    public DataResponse(Parcel in){
        creator = in.readParcelable(DataUser.class.getClassLoader());
        modules = new ArrayList<>(Arrays.asList((DataModule[])in.readParcelableArray(DataModule.class.getClassLoader())));
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
