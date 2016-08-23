package com.startandselect.agora;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAgoraListener} interface
 * to handle interaction events.
 * Use the {@link Agora_tab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Agora_tab extends Fragment  {

    private static final String ARG_DATA = "question_data";

    public QuestionAdapter mQuestionAdapter;
    public String mData;
    public ArrayList<DataQuestion> mQuestions;
    private Account_tab account;
    private OnAgoraListener mListener;

    //get rid of this:
    public static Fragment mainFragment = null;

    public static final String URL_POPULAR = "https://startandselect.com/api/ego/question/";
    public static final String URL_RECENT = "https://startandselect.com/api/ego/question/";
    public static final String URL_SEARCH = "https://startandselect.com/scripts/Search.php";
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
    public static Agora_tab newInstance(String data) {
        Agora_tab fragment = new Agora_tab();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            if(mData == null){
                mData = stringToApiString("Loading...");
                fetchQuestionList();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mainFragment = this;
        final Fragment thisFrag = (Fragment) this;
        final View output = inflater.inflate(R.layout.fragment_agora_tab, container, false);
        ListView agoraList = (ListView) output.findViewById(R.id.agora_list_view);
        agoraList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                DataQuestion q = mQuestions.get(i);
                FragmentManager fg = getActivity().getSupportFragmentManager();
                fg.saveFragmentInstanceState(thisFrag);
                Fragment frag = Detail_Question.newInstance(q.responses,q.text,q.creator.username);
                FragmentTransaction transaction = fg.beginTransaction();
                transaction.replace(R.id.main_content, frag);
                transaction.addToBackStack(Master.FRAG_AGORA);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
            }
        });
        setQuestionList(stringToConsumable(mData), output);
        return output;
    }

    public void fetchQuestionList(){
        ApiRequest request = new ApiRequest("question/");
        request.add("limit", "40");
        new DownloadQuestions().execute(request);
    }
    public void setQuestionList(DataResource data){
        setQuestionList(resourceToConsumable(data));
    }
    public void setQuestionList(DataResource data, View root){
        setQuestionList(resourceToConsumable(data), root);
    }
    public void setQuestionList(ArrayList<DataQuestion> objects){
        setQuestionList(objects, getView());
    }
    public void setQuestionList(ArrayList<DataQuestion> objects, View root){
        ListView agoraList = (ListView)root.findViewById(R.id.agora_list_view);
        mQuestions = objects;
        mQuestionAdapter = new QuestionAdapter(getContext(), R.id.question_view, objects);
        agoraList.setAdapter(mQuestionAdapter);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public static String stringToApiString(String input){
        return String.format("{objects: [{text: '%s', responses: [{ text: '%s' }]}]}", input, "No Responses.");
    }
    public ArrayList<DataQuestion> stringToConsumable(String input){
        return resourceToConsumable(new DataResource(input));
    }
    public ArrayList<DataQuestion> resourceToConsumable(DataResource data){
        ArrayList<DataQuestion> output = new ArrayList<>();
        try{
            JSONArray objects = data.objects;
            for( int i = 0; i < objects.length(); ++i ){
                output.add(new DataQuestion(objects.getJSONObject(i)));
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
        return output;
    }
    public class DownloadQuestions extends AsyncTask<ApiRequest, Integer, DataResource>{
        protected DataResource doInBackground(ApiRequest... requests){
            try{
                ApiRequest request = requests[0];
                if(Looper.myLooper() == null)Looper.prepare();
                URL url = new URL(request.URL);
                HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                connect.setRequestMethod("GET");
                if(false) {
                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(request.params.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                }
                connect.connect();
                InputStream is = new BufferedInputStream(connect.getInputStream());
                String data = Common.convertinputStreamToString(is);
                mData = data;
                return new DataResource(data);
            }
            catch (UnknownHostException e){
                //TextView prompt = (TextView)getActivity().findViewById(R.id.)
            }
            catch (Exception e){
                throw new RuntimeException(e.toString());
            }
            return null;
        }
        protected void onPostExecute(DataResource data) {
            setQuestionList(data);
        }

    }
}
/**/