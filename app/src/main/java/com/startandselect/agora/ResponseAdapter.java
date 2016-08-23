package com.startandselect.agora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Examiner on 8/20/16.
 */
public class ResponseAdapter extends ArrayAdapter<JSONObject> {

    public ArrayList<JSONObject> objects;

    public ResponseAdapter(Context context, int textViewResourceId, ArrayList<JSONObject> _objects) {
        super(context, textViewResourceId, _objects);
        this.objects = _objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.response, parent, false);
        }
        JSONObject i = objects.get(position);
        if(i != null){
            try {
                String text = i.getString("text");
                TextView title = (TextView)v.findViewById(R.id.response_title);
                if(title != null){
                    title.setText(text);
                }
            }catch(Exception e){
                throw new RuntimeException(e.toString());
            }
        }
        return v;
    }

}