package com.startandselect.agora;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAgoraListener} interface
 * to handle interaction events.
 * Use the {@link Agora_tab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Agora_tab extends Fragment implements OnAgoraListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "Account";

    // TODO: Rename and change types of parameters
    private Account_tab account;
    private OnAgoraListener mListener;

    //get rid of this:
    public static Fragment mainFragment = null;

    public static final int FILTER_MODE_POPULAR = 0;
    public static final int FILTER_MODE_RECENT = 1;
    public static final int FILTER_MODE_SEARCH = 2;
    public static final int FILTER_MODE_TAGS = 3;
    public static final int FILTER_MODE_NONE = 4;
    private String current_url = URL_POPULAR;
    private int filter_mode = 0;
    private String searchQuery = null;
    private String tags = null;

    public Agora_tab() {
        // Required empty public constructor
    }
    public static Agora_tab newInstance() {
        Agora_tab fragment = new Agora_tab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainFragment = this;
        final View output = inflater.inflate(R.layout.fragment_agora_tab, container, false);
        final ScrollView scrollView = (ScrollView) output.findViewById(R.id.tab_agora_scroll);

        //This might not be necessary vvvvv
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(output.getWindowToken(), 0);
                return false;
            }
        });

        scrollView.requestFocus();
        getQuestions();
        return output;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAgoraListener) {
            mListener = (OnAgoraListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoadingInterface");
        }
    }


    public void addQuestion(ViewGroup thisContainer, JSONObject data){
        addQuestion(thisContainer, data, 0);
    }
    public void addQuestion(ViewGroup thisContainer, JSONObject data, int index){
        thisContainer.addView(new QuestionCard(thisContainer.getContext(), data), index);
    }
    public void addQuestion(String data, int index){
        try{
            addQuestion(new JSONObject(data), index);
        }catch (Exception e){
            e.toString();
        }
    }
    public void addQuestion(String data){
        addQuestion(data, 0);
    }
    public void addQuestion(JSONObject data, int index){
        try {
            //TODO fix this null exception? Think about it at least...
            getQuestionContainer().addView(new QuestionCard(mainFragment.getContext(), data), index);
        }catch (Exception e){
            //Failed to have a mainFragment. Don't crash, no biggie.
            e.toString();
        }
    }
    public ViewGroup getQuestionContainer(){
        if(mainFragment==null)return null;
        return (ViewGroup) mainFragment.getActivity().findViewById(R.id.tab_agora_linearlayout);
    }
    public void setAccount(Account_tab _account){
        account = _account;
    }
    public void updateQuestions(final JSONObject arr){
        try {
            final ViewGroup thisContainer = getQuestionContainer();
            if(thisContainer != null){
                ((Activity)thisContainer.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            thisContainer.removeAllViews();
                            JSONArray questions = arr.getJSONArray("objects");
                            thisContainer.setPadding(10,10,10,10);
                            for(int i = 0 ; i < questions.length(); ++i){
                                addQuestion(thisContainer, questions.getJSONObject(i), i);
                            }
                        }
                        catch(Exception e){
                            thisContainer.addView(new QuestionCard(getContext(), null));
                        }
                    }
                });
            }else{
                throw new RuntimeException("FUCKED");
            }
        }catch(Exception e){
            //Container not ready
            throw new RuntimeException(e.toString());
        }
    }
    public void processData(final String data){
        try {
            updateQuestions(new JSONObject(data));
        }catch(Exception e){
            //Container not ready
            throw new RuntimeException(e.toString());
        }
    }
    public void getQuestions(){
        final Activity activity = getActivity();
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    URL url = new URL(current_url);
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    RestParam parameters = new RestParam();

                    if(searchQuery != null){
                        parameters.add("query", searchQuery);
                    }else if(tags != null){
                        parameters.add("tag", tags);
                    }

                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    connect.connect();

                    InputStream is = new BufferedInputStream(connect.getInputStream());
                    String data = Common.convertinputStreamToString(is);
                    processData(data);
                }catch (Exception e){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to hookup with server...", Toast.LENGTH_SHORT).show();
                            final Button failed = new Button(getContext());
                            failed.setText(R.string.failed_agora_getAll);
                            failed.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    failed.setText(R.string.retry_agora_getAll);
                                    getQuestions();
                                }
                            });
                            final ViewGroup thisContainer = (ViewGroup) getActivity().findViewById(R.id.tab_agora_linearlayout);
                            if(thisContainer != null) {
                                thisContainer.removeAllViews();
                                thisContainer.addView(failed);
                            }
                        }
                    });
                }
                return null;
            }
        };
        net.execute();
    }
    public void setFilterMode(int filterMode){
        filter_mode = filterMode;
        switch (filterMode){
            case FILTER_MODE_POPULAR:
                current_url = URL_POPULAR;
                break;
            case FILTER_MODE_RECENT:
                current_url = URL_RECENT;
                break;
            case FILTER_MODE_SEARCH:
                current_url = URL_SEARCH;
                tags = null;
                break;
            case FILTER_MODE_TAGS:
                current_url = URL_POPULAR;
                searchQuery = null;
                break;
            case FILTER_MODE_NONE:
                current_url = URL_POPULAR;
                tags = null;
                searchQuery = null;
                break;

        }

        ((ViewPager)((Activity)getContext()).findViewById(R.id.container)).setCurrentItem(0, true);
        getQuestions();
    }
    public void setFilterTags(String filterTags){
        tags = filterTags;
        setFilterMode(FILTER_MODE_TAGS);
    }
    public void setSearchQuery(String _searchQuery){
        searchQuery = _searchQuery;
        setFilterMode(FILTER_MODE_SEARCH);
    }
    public String getFilterTags(){
        return tags;
    }
    public Integer getFilterMode(){
        return filter_mode;
    }
    public String getSearchQuery(){
        return searchQuery;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}