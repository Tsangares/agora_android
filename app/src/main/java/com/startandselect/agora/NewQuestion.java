package com.startandselect.agora;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class NewQuestion extends Fragment {
    public static final String data_name = "question";
    EditText    inputQuestion   = null;
    EditText    inputTags       = null;
    Button      submit          = null;
    TextView    prompt          = null;

    public NewQuestion(){

    }
    public static NewQuestion newInstance(){
        NewQuestion frag = new NewQuestion();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View output = inflater.inflate(R.layout.activity_new_question, container, false);
        inputQuestion  = (EditText) output.findViewById(R.id.newquestion_questioninput);
        inputTags      = (EditText) output.findViewById(R.id.newquestion_taginput);
        submit         = (Button)   output.findViewById(R.id.newquestion_submit);
        prompt         = (TextView) output.findViewById(R.id.newquestion_prompt);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check to see if legit question
                if(inputQuestion.getText().toString().equals("")){
                    prompt.setText(R.string.newquestion_error_noquestion);
                }else if(inputTags.getText().toString().equals("")){
                    prompt.setText(R.string.newquestion_error_notags);
                }else{
                    prompt.setText(R.string.newquestion_success);
                    sendNewQuestionData();
                }
            }
        });
        return output;
    }


    public void processNewQuestion(final String data){
        //Process the data from the server.
        //Send the data back to the other activity.

    }
    public void sendNewQuestionData(){
        //Get the data from the server.
        final Activity  activity = getActivity();
        final String    questionText = inputQuestion.getText().toString();
        final String    tagsText     = inputTags.getText().toString();
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    Toast.makeText(activity, "Asking Server", Toast.LENGTH_SHORT).show();
                    URL url = new URL("https://startandselect.com/scripts/UploadQuestion.php");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    //post attributes
                    RestParam parameters = new RestParam();
                    parameters.add("question", questionText);
                    parameters.add("tags", tagsText);

                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    connect.connect();

                    InputStream is = new BufferedInputStream(connect.getInputStream());
                    processNewQuestion(Common.convertinputStreamToString(is));
                }catch (Exception e){
                    e.toString();
                }
                return null;
            }
        };
        net.execute();
    }
}
