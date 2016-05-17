package com.startandselect.agora;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Apollonian on 5/12/2016.
 */
public class TagButton extends Button{
    public TagButton(Context context, JSONObject tag){
        super(context);
        try {
            setText(tag.getString("tag"));
            setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }catch (Exception e){
            throw new RuntimeException (e.toString());
        }
    }

}
