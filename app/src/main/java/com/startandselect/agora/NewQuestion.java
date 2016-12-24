package com.startandselect.agora;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewQuestion extends Fragment {
    public static final String data_name = "question";
    EditText    inputQuestion   = null;
    EditText    inputTags       = null;
    FloatingActionButton submit = null;
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
        submit         = (FloatingActionButton)   output.findViewById(R.id.newquestion_submit);
        prompt         = (TextView) output.findViewById(R.id.newquestion_prompt);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check to see if legit question
                if(inputQuestion.getText().toString().equals("")){
                    prompt.setText(R.string.newquestion_error_noquestion);
                }else if(inputTags.getText().toString().equals("")){
                    prompt.setText(R.string.newquestion_error_notags);
                }else {
                    prompt.setText(R.string.newquestion_success);
                    sendNewQuestionData();
                }
            }
        });
        if(!Profile.getLoggedIn()){
            inputQuestion.setEnabled(false);
            inputTags.setEnabled(false);
            prompt.setText(R.string.newquestion_error_not_logged_in);
        }else{
            inputQuestion.setEnabled(true);
            inputTags.setEnabled(true);
        }
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
                    URL url = new URL("http://api.iex.ist/full/question");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    if(!Profile.getLoggedIn()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                EditText prompt = (EditText) getView().findViewById(R.id.newquestion_questioninput);
                                prompt.setError(getString(R.string.newquestion_error_not_logged_in));
                            }
                        });
                        return null;
                    }
                    connect.setRequestProperty("Authorization", "ApiKey "+ Profile.getUsername()+":"+Profile.getApiKey());
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
                }catch(FileNotFoundException k) {
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView prompt = (TextView) getView().findViewById(R.id.newquestion_prompt);
                            prompt.setText(R.string.newquestion_error_noconnection);
                        }
                    });
                }catch (final Exception e){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView prompt = (TextView) getView().findViewById(R.id.newquestion_prompt);
                            prompt.setText(e.toString());
                        }
                    });
                }
                return null;
            }
        };
        net.execute();
    }
}
