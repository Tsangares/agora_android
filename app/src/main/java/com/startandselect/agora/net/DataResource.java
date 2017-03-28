package com.startandselect.agora.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by astro on 2/17/17.
 */
public class DataResource implements Parcelable {
    public DataMeta meta = null;
    public JSONArray objects = null;
    private int handle = ApiRequest.REPLACE;
    private boolean add = false;
    public DataResource(){}
    public DataResource(String data){
        try{
            update(new JSONObject(data));
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public DataResource(String data, int _handle){
        try{
            update(new JSONObject(data));
            setHandle(_handle);
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public DataResource(JSONObject data){
        update(data);
    }
    public DataResource(JSONObject data, int _handle){
        setHandle(_handle);
        update(data);
    }
    public boolean getAdd(){
        return add;
    }
    public void setAdd(boolean input){
        add = input;
    }
    public void setHandle(int _handle){
        handle = _handle;
        switch (_handle) {
            case ApiRequest.APPEND:
                add = true;
                break;
            default:
                add = false;
                break;
        }
    }
    public int getHandle(){
        return handle;
    }
    public void update(JSONObject data){
        try{
            try {
                meta = new DataMeta(data.getJSONObject("meta"));
            }catch (Exception e){
                meta = null;
            }
            objects = data.getJSONArray("objects");
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
        dest.writeParcelable(meta, 0);
    }
    public DataResource(Parcel in){
        meta = in.readParcelable(DataMeta.class.getClassLoader());
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
