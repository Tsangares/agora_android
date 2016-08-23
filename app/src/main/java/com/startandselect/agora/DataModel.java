package com.startandselect.agora;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Examiner on 8/21/16.
 */

public class DataModel{
    public JSONObject _data;
    public DataModel(){}
    public String toString() {
        return _data.toString();
    }
    public void update(JSONObject data){
        data = _data;
    }
}
class DataMeta extends DataModel{
    public String next;
    public String previous;
    public int limit;
    public int offset;
    public int total_count;
    public DataMeta(){}
    public DataMeta(JSONObject data){
        update(data);
    }
    @Override
    public void update(JSONObject data){
        super.update(data);
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
}
class DataResource extends DataModel{
    public DataMeta meta;
    public JSONArray objects;
    public DataResource(){}
    public DataResource(String data){
        try{
            update(new JSONObject(data));
        }catch (Exception e){

        }
    }
    public DataResource(JSONObject data){
        update(data);
    }
    @Override
    public void update(JSONObject data){
        super.update(data);
        try{
            meta = new DataMeta(data.getJSONObject("meta"));
            objects = data.getJSONArray("objects");
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}
class DataUser extends DataModel {
    public String username;
    public int total_questions;
    public int total_responses;
    public int total_votes;
    public DataUser(){}
    public DataUser(JSONObject data){
        update(data);
    }
    public void update(JSONObject data){
        super.update(data);
        try{
            username = data.getString("username");
            total_questions = data.getInt("total_questions");
            total_responses = data.getInt("total_responses");
            total_votes = data.getInt("total_votes");
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}
class DataQuestion extends DataModel{
    public String text;
    public DataUser creator;
    public String responses_data = null;
    public ArrayList<DataResponse> responses = new ArrayList<>();

    public DataQuestion(){}
    public DataQuestion(JSONObject data){
        update(data);
    }
    public void update(JSONObject data){
        super.update(data);
        try{
            text = data.getString("text");
            creator = new DataUser(data.getJSONObject("creator"));
            JSONArray arr = data.getJSONArray("responses");
            responses_data = data.getString("responses");
            for(int i = 0; i < arr.length(); ++i){
                responses.add(new DataResponse(arr.getJSONObject(i)));
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}
class DataResponse extends DataModel {
    public String text;
    public DataUser creator;

    public DataResponse(){}
    public DataResponse(JSONObject data){
        update(data);
    }
    public void update(JSONObject data){
        super.update(data);
        try{
            text = data.getString("text");
            creator = new DataUser(data.getJSONObject("creator"));
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}

