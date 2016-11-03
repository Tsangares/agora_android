package com.startandselect.agora;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Looper;
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

/**
 * Created by Examiner on 8/4/16.
 */
class ResponseContainer extends LinearLayout {
    public ResponseContainer(Context context, QuestionCard parent, JSONArray array){
        super(context);
        setPadding(15,5,5,10);
        setAlpha(.6f);
        setOrientation(LinearLayout.VERTICAL);
        update(parent, array, true);
    }
    public void update(QuestionCard parent, JSONArray array, boolean animate){
        try {
            for (int i = 0; i < array.length(); ++i) {
                addView(new Response(getContext(), parent, array.getJSONObject(i), animate));
            }
            if(main.anonymous || main.loggedin) {
                addView(new NewResponse(getContext(), parent));
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}
class NewResponse extends LinearLayout {
    public boolean ready = false;
    public EditText input = null;
    public Button submit = null;
    public QuestionCard parent = null;
    public NewResponse(Context context, QuestionCard _parent) {
        super(context);
        parent = _parent;
        buildResponseField();
    }
    public void buildResponseField(){
        setOrientation(VERTICAL);
        input = new EditText(getContext());
        input.setHint(R.string.newresponse_prompt_hint);
        input.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        submit = new Button(getContext());
        submit.setText(R.string.newresponse_prompt);
        submit.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(submit);
        OnFocusChangeListener keys = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                if (input.isFocused()) imm.showSoftInput(input, 0);
                else imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        };
        input.setOnFocusChangeListener(keys);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ready) {
                    ready = true;
                    submit.setText(R.string.newresponse_submit);
                    addView(input, 0);
                    input.requestFocus();
                    //ScrollView scrollView = (ScrollView) (getRootView().findViewById(R.id.tab_agora_scroll));
                    //scrollToView(scrollView, input);
                }else if(input.getText().toString() != ""){
                    submitResponse();
                }
            }
        });
    }
    public void rebuildResponseField(){
        removeAllViews();
        addView(input);
        addView(submit);
    }
    public void processData(final String data){
        try {
            removeAllViews();
            //Need o initialize server side to pass a JSON Object not a JSON array.
            JSONObject arr = (new JSONArray(data)).getJSONObject(0);
            this.addView(new Response(getContext(), parent, arr, true));
            ((OnAccountListener)getContext()).addMyResponses(1);
        }catch(Exception e){
            //Container not ready
            throw new RuntimeException(e.toString());
        }
    }
    public void submitResponse(){
        removeAllViews();
        final Button temp = new Button(getContext());
        temp.setText("...");
        addView(temp);
        final String newResponseText = input.getText().toString();
        final Activity activity = (Activity)(getContext());
        final Integer user_id = ((OnAccountListener)getContext()).getUser_id();
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    URL url = new URL("https://startandselect.com/scripts/UploadResponse.php");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    RestParam parameters = new RestParam();
                    parameters.add("response", newResponseText);
                    parameters.add("question_id", parent.id.toString());
                    if(user_id != null){
                        parameters.add("user_id", user_id.toString());
                    }
                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.get());
                    writer.flush();
                    writer.close();
                    os.close();

                    connect.connect();
                    final InputStream is = new BufferedInputStream(connect.getInputStream());

                    final String data = Common.convertinputStreamToString(is);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                processData(data);
                            }catch(Exception e){
                                temp.setText(R.string.failed_response_upload);
                                addView(temp);
                                temp.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        rebuildResponseField();
                                    }
                                });
                            }
                        }
                    });
                }catch (Exception e){
                    throw new RuntimeException(e.toString());
                }
                return null;
            }
        };
        net.execute();
    }
    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }
    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }
}
class Response extends LinearLayout{
    public boolean      toggle   = false;
    public ImageView check    = null;
    public Button       title    = null;
    public TextView _title   = null;
    public TextView     _votes   = null;
    public String       response = null;

    public QuestionCard parent   = null;
    public Integer      creator  = null;
    public Integer      id       = null;
    public Integer      numVotes = null;
    public float        textSize = 17f;
    public int          padding  = 20;
    public final long   duration = 100;

    public final static int NONE = 0;
    public final static int GOLD = 1;
    public final static int SILVER = 2;
    public final static int BRONZE = 3;
    public int color_state = NONE;
    public Response(Context context, QuestionCard _parent,JSONObject obj, boolean animate){
        super(context);
        update(obj);
        parent = _parent;
        setGravity(Gravity.CENTER_VERTICAL);
        try {
            response = obj.getString("response");
        }catch (Exception e){
            response = "error, my bad...";
            throw new RuntimeException(e.toString());
        }
        buildDetail();
        buildVote();
        if(parent.inDetail){
            showDetail(animate);
        }else{
            showVote(animate);
        }
    }
    public void buildDetail(){
        //Setup
        _title = new TextView(getContext());
        _votes  = new TextView(getContext());
        _title.setText(response);
        setColor();
        _title.setTextSize(textSize);
        _votes.setTextSize(textSize + 5f);
        _votes.setText(numVotes.toString());
        _title.setGravity(Gravity.CENTER);
        _votes.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,10,0,10);
        _title.setLayoutParams(layoutParams);
        _title.setPadding(padding,padding,padding,padding);
        _votes.setPadding(2*padding,0,2*padding,0);
    }
    public void buildVote(){
        title = new Button(getContext());
        check = new ImageView(getContext());
        check.setClickable(true);
        title.setClickable(true);
        title.setTextSize(textSize);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toggleCheck();
        title.setText(response);
        OnClickListener toggleVote = new OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.inDetail = true;
                toggleCheck();
                parent.hideVotes();
            }
        };
        check.setOnClickListener(toggleVote);
        title.setOnClickListener(toggleVote);
    }
    public ViewPropertyAnimator showDetail(boolean animate){
        setColor();
        if(animate) {
            _title.setScaleX(0);
            _title.setScaleY(0);
            _votes.setScaleX(0);
            _votes.setScaleY(0);
            ViewPropertyAnimator animator = _title.animate().scaleX(1).scaleY(1).setDuration(2 * duration);
            _votes.animate().scaleX(1).scaleY(1).start();
            addView(_title);
            addView(_votes, 0);
            animator.start();
            return animator;
        }else{
            addView(_title);
            addView(_votes, 0);
            return null;
        }
    }
    public ViewPropertyAnimator showVote(boolean animate) {
        if(animate) {
            title.setScaleX(0);
            title.setScaleY(0);
            check.setScaleX(0);
            check.setScaleY(0);
            ViewPropertyAnimator animator = title.animate().scaleX(1).scaleY(1).setDuration(2 * duration);
            check.animate().scaleX(1).scaleY(1).start();
            addView(title);
            addView(check, 0);
            animator.start();
            return animator;
        }else{
            addView(title);
            addView(check, 0);
            return null;
        }
    }
    public ViewPropertyAnimator hideDetail(boolean animate){
        if(animate) {
            ViewPropertyAnimator animator = _title.animate().scaleX(0).scaleY(0).setDuration(duration);
            animator.withEndAction(new Runnable() {
                @Override
                public void run() {
                    removeView(_title);
                }
            });
            _votes.animate().scaleY(0).scaleX(0).setDuration(duration).withEndAction(new Runnable() {
                @Override
                public void run() {
                    removeView(_votes);
                }
            });
            return animator;
        }else{
            removeView(_title);
            removeView(_votes);
            return null;
        }
    }
    public ViewPropertyAnimator hideVote(boolean animate){
        if(animate) {
            check.animate().scaleX(0).scaleY(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    removeView(check);
                }
            });
            ViewPropertyAnimator animator = title.animate().scaleY(0).scaleX(0).setDuration(duration);
            animator.withEndAction(new Runnable() {
                @Override
                public void run() {
                    removeView(title);
                }
            });
            return animator;
        }else{
            removeView(check);
            removeView(title);
            return null;
        }
    }
    public void switchToDetail(){
        title.animate().scaleY(0).scaleX(0).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                removeView(title);
                showDetail(true);
            }
        });
        check.animate().scaleX(0).scaleY(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                removeView(check);
            }
        });
    }
    public void switchToVote(){
        _title.animate().scaleX(0).scaleY(0).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                removeView(_title);
                showVote(true);
            }
        });
        _votes.animate().scaleY(0).scaleX(0).setDuration(duration).withEndAction(new Runnable() {
            @Override
            public void run() {
                removeView(_votes);
            }
        });
    }
    public void gold(){
        color_state = GOLD;
        setColor();
    }
    public void silver(){
        color_state = SILVER;
        setColor();
    }
    public void bronze(){
        color_state = BRONZE;
        setColor();
    }
    public void setColor(){
        switch (color_state){
            case GOLD:
                _title.setBackgroundResource(R.drawable.response_button_gold);
                break;
            case SILVER:
                _title.setBackgroundResource(R.drawable.response_button_silver);
                break;
            case BRONZE:
                _title.setBackgroundResource(R.drawable.response_button_bronze);
                break;
            case NONE:
                _title.setBackgroundResource(R.drawable.response_button);
                break;
        }
        _title.setPadding(padding, padding, padding, padding);
        _title.setTextColor(Color.WHITE);
    }
    public void update(JSONObject input){
        try{
            creator = input.getInt("creator");
            id      = input.getInt("id");
            numVotes= input.getInt("votes");
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public void toggleCheck(){
        if(toggle){
            toggle = false;
            check.setImageResource(R.drawable.ic_check_box_black_24px);
            //Submit vote
            sendVote();
        }
        else {
            toggle = true;
            check.setImageResource(R.drawable.ic_check_box_outline_blank_black_24px);
        }
    }
    public void processUpVote(final String data){
        //Process the data from the server.
        try {
            parent.processVote(new JSONObject(data));
            ((OnAccountListener)getContext()).addMyVotes(1);
        }catch(Exception e){
            //Container not ready
            throw new RuntimeException(e.toString());
        }
    }
    public void sendVote(){
        //Get the data from the server.
        final Activity activity = (Activity) getContext();
        final Integer user_id = ((OnAccountListener)getContext()).getUser_id();
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    URL url = new URL("https://startandselect.com/scripts/UpVote.php");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    //post attributes
                    //!! Not setup
                    RestParam parameters = new RestParam();
                    parameters.add("response_id", id.toString());
                    if(user_id != null) {
                        parameters.add("user_id", user_id.toString());
                    }
                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    connect.connect();

                    final InputStream is = new BufferedInputStream(connect.getInputStream());
                    final String data = Common.convertinputStreamToString(is);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                processUpVote(data);
                            }catch(Exception e){
                                //failed to upload the question
                                throw new RuntimeException(e.toString());
                            }
                        }
                    });
                }catch (Exception e){
                    e.toString();
                }
                return null;
            }
        };
        net.execute();
    }
}
