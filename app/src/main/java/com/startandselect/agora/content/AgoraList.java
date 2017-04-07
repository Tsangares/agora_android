package com.startandselect.agora.content;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.startandselect.agora.net.DataGeneric;
import com.startandselect.agora.net.DataResource;
import com.startandselect.agora.net.api.GetList;

import org.json.JSONArray;

public abstract class AgoraList extends ListFragment {
    public String type = null;
    public AgoraList() {
    }

    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("none, faggot stop asking.");
        new Net().download();
    }
    public abstract void onEndExecute(JSONArray objects);
    public class Net extends GetList {
        public AsyncTask download(){
            return init(type,0,20,null);
        }
        public AsyncTask download(Integer offset, Integer limit, Integer order){
            return init(type, offset, limit, order);
        }
        protected void onPostExecute(String data) {
             if(data != null) {
                 onEndExecute(new DataResource(data).objects);
            }
        }
    }
}
