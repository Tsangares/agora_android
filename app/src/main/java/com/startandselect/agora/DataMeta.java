package com.startandselect.agora;

import android.database.DatabaseErrorHandler;
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

class DataResource implements Parcelable{
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
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataResource createFromParcel(Parcel in) {
            return new DataResource(in);
        }
        public DataResource[] newArray(int size) {
            return new DataResource[size];
        }
    };
}
class DataUser implements Parcelable {
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
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataUser createFromParcel(Parcel in) {
            return new DataUser(in);
        }
        public DataUser[] newArray(int size) {
            return new DataUser[size];
        }
    };
}
class DataQuestion implements Parcelable{
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
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataResource createFromParcel(Parcel in) {
            return new DataResource(in);
        }
        public DataResource[] newArray(int size) {
            return new DataResource[size];
        }
    };
}
class DataResponse implements Parcelable {
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
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataResource createFromParcel(Parcel in) {
            return new DataResource(in);
        }
        public DataResource[] newArray(int size) {
            return new DataResource[size];
        }
    };
}

class DataModule implements Parcelable {
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
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataResource createFromParcel(Parcel in) {
            return new DataResource(in);
        }
        public DataResource[] newArray(int size) {
            return new DataResource[size];
        }
    };
}
class DataComment implements Parcelable {
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
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataResource createFromParcel(Parcel in) {
            return new DataResource(in);
        }
        public DataResource[] newArray(int size) {
            return new DataResource[size];
        }
    };
}
class DataBuilder{
    private DataBuilder(){

    }
    public static DataQuestion buildQuestion(String question){
        DataQuestion temp = new DataQuestion();
        temp.text = question;
        return temp;
    }
    public static DataResource buildResource(){
        return null;
    }
}