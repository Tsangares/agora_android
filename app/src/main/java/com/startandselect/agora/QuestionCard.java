package com.startandselect.agora;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
 * Created by Apollonian on 4/17/2016.
 */
public class QuestionCard extends CardView{
    public boolean toggle = true;
    public LinearLayout container = null;
    public TextView QuestionTitle = null;
    public ResponseContainer rContainer = null;
    public QuestionCard(Context context, JSONObject obj){
        super(context);
        //reset this to apply to less than api 21
        setUseCompatPadding(true);
        //setGravity(Gravity.CENTER_HORIZONTAL);
        try {
            //card = new CardView(context);
            setClickable(true);
            setCardElevation(2f);
            setContentPadding(5,15,5,15);
            setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            container = new LinearLayout(context);
            container.setOrientation(container.VERTICAL);

            QuestionTitle = new TextView(getContext());
            QuestionTitle.setTextSize(25f);
            QuestionTitle.setText(obj.getString("question"));
            QuestionTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            container.addView(QuestionTitle);

            setClickable(true);
            rContainer = new ResponseContainer(context, obj.getJSONArray("responses"), obj.getInt("id"), obj.getInt("creator"));
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toggle){
                        toggle = false;
                        container.addView(rContainer);
                    }else{
                        toggle = true;
                        container.removeView(rContainer);
                    }
                }
            });
            addView(container);
        }catch(Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}
class ResponseContainer extends LinearLayout{
    public ResponseContainer(Context context, JSONArray array, int question_id, int question_creator){
        super(context);
        setPadding(15,5,5,10);
        setAlpha(.6f);
        setOrientation(VERTICAL);
        try {
            for (int i = 0; i < array.length(); ++i) {
                addView(new Response(context, array.getJSONObject(i)));
            }
            addView(new NewResponse(context, question_id, question_creator));
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
}
class NewResponse extends LinearLayout {
    public boolean ready = false;
    public EditText input = null;
    public Button submit = null;
    private Integer question_id = -1;
    private Integer question_creator = -1;
    public NewResponse(Context context, int _question_id, int _question_creator) {
        super(context);
        question_id = _question_id;
        question_creator = _question_creator;
        buildResponseField();
    }
    public void buildResponseField(){
        setOrientation(VERTICAL);
        input = new EditText(getContext());
        input.setHint("New Response");
        input.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        submit = new Button(getContext());
        submit.setText("add new response");
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
                    submit.setText("submit");
                    addView(input, 0);
                    input.requestFocus();
                    ScrollView scrollView = (ScrollView) (getRootView().findViewById(R.id.tab_agora_scroll));
                    scrollToView(scrollView, input);
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
            JSONObject arr = new JSONObject(data);
            this.addView(new Response(getContext(),arr));
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
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    URL url = new URL("http://startandselect.com/scripts/UploadResponse.php");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    RestParam parameters = new RestParam();
                    parameters.add("response", newResponseText);
                    parameters.add("question_id", question_id.toString());
                    parameters.add("question_creator", question_creator.toString());
                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.get());
                    writer.flush();
                    writer.close();
                    os.close();

                    connect.connect();

                    final InputStream is = new BufferedInputStream(connect.getInputStream());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                processData(Common.convertinputStreamToString(is));
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
                    e.toString();
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
    public boolean toggle = false;
    public ImageView check = null;
    public Button title = null;
    public Response(Context context, JSONObject obj){
        super(context);
        setGravity(Gravity.CENTER_VERTICAL);
        title = new Button(context);
        //title.setBackgroundColor(ContextCompat.getColor(context, R.color.coloaPrimaryLight));
        title.setTextSize(15f);
        title.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        check = new ImageView(context);
        toggleCheck();
        try {
            title.setText(obj.getString("response"));
        }catch (Exception e){
            title.setText("Sorry, debug: " + e.toString());
        }
        addView(check);
        addView(title);
        check.setClickable(true);
        title.setClickable(true);
        OnClickListener toggleVote = new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheck();
            }
        };
        check.setOnClickListener(toggleVote);
        title.setOnClickListener(toggleVote);
    }
    public void toggleCheck(){
        if(toggle){
            toggle = false;
            check.setImageResource(R.drawable.ic_check_box_black_24px);
        }
        else {
            toggle = true;
            check.setImageResource(R.drawable.ic_check_box_outline_blank_black_24px);
        }
    }
}
