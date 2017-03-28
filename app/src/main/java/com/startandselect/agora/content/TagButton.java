package com.startandselect.agora.content;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Apollonian on 5/12/2016.
 */
public class TagButton extends Button{
    public String tag = null;
    public TagButton(Context context, JSONObject tagData) {
        super(context);
        try {
            tag = tagData.getString("tag");
            setText(tag);
            setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((OnAgoraListener) getContext()).setFilterTags(tag);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
