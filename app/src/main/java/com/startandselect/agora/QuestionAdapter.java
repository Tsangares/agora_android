package com.startandselect.agora;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Examiner on 8/4/16.
 */
public class QuestionAdapter extends ArrayAdapter<DataQuestion> {
    public ArrayList<DataQuestion> objects;

    public QuestionAdapter(Context context, int textViewResourceId, ArrayList<DataQuestion> _objects) {
        super(context, textViewResourceId, _objects);
        this.objects = _objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.question, parent, false);

        }
        DataQuestion i = objects.get(position);
        if(i != null){
            try {
                String text = i.text;
                TextView title = (TextView)v.findViewById(R.id.question_title);
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
