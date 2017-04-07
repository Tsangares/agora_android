package com.startandselect.agora.content;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.startandselect.agora.R;
import com.startandselect.agora.net.DataGeneric;
import com.startandselect.agora.net.DataQuestion;

import java.util.ArrayList;

/**
 * Created by astro on 3/27/17.
 */

public class AgoraAdapter extends ArrayAdapter<DataGeneric> {
    public ArrayList<DataGeneric> objects;
    public final static int layout = R.layout.list_item;
    public AgoraAdapter(Context context, ArrayList<DataGeneric> _objects) {
        super(context, layout, _objects);
        this.objects = _objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layout, parent, false);
        }
        DataGeneric i = objects.get(position);
        if(i != null){
            try {
                String text = i.text;
                TextView title = (TextView)v.findViewById(R.id.title);
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
