package com.startandselect.agora.net;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by astro on 2/17/17.
 */
public class DataQuestion implements Parcelable {
    public DataUser creator = null;
    public ArrayList<DataResponse> responses = new ArrayList<>();
    public String text = "Default Question";
    public int id = -1;

    public DataQuestion(){}
    public DataQuestion(JSONObject data){
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
                responses = DataResponse.getArray(data.getString("responses"));
            }catch (Exception e){
                responses = new ArrayList<>();
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
        dest.writeParcelable(creator, 0);
        dest.writeParcelableArray(responses.toArray(new DataResponse[responses.size()]),0);
        dest.writeString(text);
        dest.writeInt(id);
    }
    public DataQuestion(Parcel in){
        creator = in.readParcelable(DataUser.class.getClassLoader());
        responses = new ArrayList<>(Arrays.asList((DataResponse[])in.readParcelableArray(DataResponse.class.getClassLoader())));
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
