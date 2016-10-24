package com.startandselect.agora;

import android.accounts.Account;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Apollonian on 4/17/2016.
 */
public class QuestionCard extends CardView{
    public boolean toggle = true;
    public LinearLayout container = null;
    public TextView QuestionTitle = null;
    public ResponseContainer rContainer = null;
    public Integer id = null;
    public Integer creator = null;
    public Boolean inDetail = false;
    public QuestionCard(Context context, JSONObject obj){
        super(context);
        //reset this to apply to less than api 21
        setUseCompatPadding(true);
        //setGravity(Gravity.CENTER_HORIZONTAL);
        try {
            //card = new CardView(context);

            setCardElevation(2f);
            setContentPadding(5,15,5,15);
            setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);
            QuestionTitle = new TextView(getContext());
            QuestionTitle.setTextSize(25f);
            QuestionTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            container.addView(QuestionTitle);

            if(obj != null) {
                update(obj);
                setClickable(true);
                QuestionTitle.setText(obj.getString("question"));
                buildResponses(obj.getJSONArray("responses"));
                container.addView(rContainer);
                QuestionTitle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rContainer.getVisibility() == GONE) rContainer.setVisibility(VISIBLE);
                        else rContainer.setVisibility(GONE);
                    }
                });
                rContainer.setVisibility(GONE);
            }else{
                QuestionTitle.setText("No Questions Found");
            }
            addView(container);
        }catch(Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public void hideVotes(){
        for (int i = 0; i < rContainer.getChildCount(); ++i) {
            View obj = rContainer.getChildAt(i);
            if (obj instanceof Response) {
                Response response = (Response)obj;
                response.hideVote(true);
            }
        }
    }
    public void showDetail(){
        try {
            inDetail = true;
            for (int i = 0; i < rContainer.getChildCount(); ++i) {
                View obj = rContainer.getChildAt(i);
                if (obj instanceof Response) {
                    Response response = (Response)obj;
                    if(i == 0){
                        response.gold();
                    }else if(i == 1){
                        response.silver();
                    }else if(i == 2){
                        response.bronze();
                    }

                }
            }
        }catch(Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public void buildResponses(JSONArray responses){
        if(rContainer == null) {rContainer = new ResponseContainer(getContext(), this, responses); return;}
        rContainer.removeAllViews();
        rContainer.update(this, responses, true);
    }
    public void processVote(JSONObject input){
        try{
            JSONArray responses = input.getJSONArray("responses");
            //sort responses:
            buildResponses(sortResponses(responses));
            showDetail();
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public static JSONArray sortResponses(JSONArray input){
        List<JSONObject> jsons = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            try {
                jsons.add(input.getJSONObject(i));
            }catch(Exception e){
                throw new RuntimeException("This error will never happen: " + e.toString());
            }
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            public int compare(JSONObject lhs, JSONObject rhs){
                try {
                    Integer lid = lhs.getInt("votes");
                    Integer rid = rhs.getInt("votes");
                    return -lid.compareTo(rid);
                }catch (Exception e){
                    throw new RuntimeException(e.toString());
                }
            }
        });
        return new JSONArray(jsons);
    }
    public void update(JSONObject input){
        try{
            id = input.getInt("id");
            creator = input.getInt("creator");
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}