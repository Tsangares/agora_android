package com.startandselect.agora;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;

/**
 * Things I added on plane:
 * I made this class
 * I made a response adapter
 * I made Agora_tab exchange itself, a fragment for a deailquestion view.
 * ^ I want it to also store info to rebuild where it came from.
 * I inflate the Detail_Question's response's data with a hidden TextView.
 * When creating Detail_Question it takes the text from a hidden TextView.
 */
public class Detail_Question extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARGS_RESPONSE_TITLE = "response_title";
    private static final String ARGS_RESPONSE_CREATOR = "response_creator";
    private static final String ARGS_QUESTION_ID = "question_id";
    private static final String ARGS_RESPONSES = "responses";

    public TextView voted;
    private ArrayList<DataResponse> mResponses;
    private String mResponse_title;
    private String mResponse_creator;

    //For the listview: to avoid multiple calls for last item
    private int preLast = -1;

    public Detail_Question() {
        // Required empty public constructor
    }
    public static Detail_Question newInstance(String title, String creator, ArrayList<DataResponse> responses) {
        Detail_Question fragment = new Detail_Question();
        Bundle args = new Bundle();
        args.putString(ARGS_RESPONSE_CREATOR, creator);
        args.putString(ARGS_RESPONSE_TITLE, title);
        args.putParcelableArrayList(ARGS_RESPONSES, responses);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResponses = getArguments().getParcelableArrayList(ARGS_RESPONSES);
            mResponse_title = getArguments().getString(ARGS_RESPONSE_TITLE, "Empty Question");
            mResponse_creator = getArguments().getString(ARGS_RESPONSE_CREATOR, "Doe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View output = inflater.inflate(R.layout.fragment_detail_question, container, false);
        ListView questionDetailList = (ListView)output.findViewById(R.id.detail_question_list);
        ResponseAdapter mResponseAdapter = new ResponseAdapter(getContext(), R.id.response_view, mResponses);
        questionDetailList.setAdapter(mResponseAdapter);
        ((TextView)output.findViewById(R.id.detail_question_title)).setText(mResponse_title);
        ((TextView)output.findViewById(R.id.detail_question_creator)).setText(mResponse_creator);
        return output;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView questionDetailList = (ListView) getActivity().findViewById(R.id.detail_question_list);
        questionDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vote(getResponseTitle(view));
            }
        });
        if (getVote() != null) displayDetails();
        questionDetailList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if( lastVisibleItem == totalItemCount ){
                    if(preLast != lastVisibleItem){
                        //nextSet();
                        preLast = lastVisibleItem;
                    }
                }
            }
        });
    }

    public TextView getResponseTitle(View v){
        return (TextView)v.findViewById(R.id.response_title);
    }
    public void vote(TextView v){
        if(voted != null) unVote(voted);
        if(voted == v){
            unVote(v);
            setVote(null);
        }else{
            v.setTypeface(Typeface.DEFAULT_BOLD);
            setVote(v);
        }
    }
    public void unVote(TextView v){
        v.setTypeface(Typeface.DEFAULT);
    }
    public TextView getVote(){
        return voted;
    }
    public TextView setVote(TextView v){
        if(voted == null)displayDetails();
        voted = v;
        return voted;
    }
    public void displayDetails(){
        ListView list = (ListView)getActivity().findViewById(R.id.detail_question_list);
        for(int i = 0; i < list.getChildCount(); ++i){
            list.getChildAt(i).findViewById(R.id.response_details).animate().x(1).start();
        }
    }
    public ArrayList<JSONObject> toConsumable(String data){
        try{
            //Is the data actually a JSONArray? Idk if this will work.
            return toConsumable(new JSONArray(data));
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public ArrayList<JSONObject> toConsumable(JSONArray data){
        ArrayList<JSONObject> output = new ArrayList<>();
        try{
            for( int i = 0; i < data.length(); ++i ){
                output.add(data.getJSONObject(i));
            }
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
        return output;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void fetchModuleList(){
        //Setup query and get data using DownloadModules
    }
    public void setAllModuleLists(String data){
        //Got all modules in a json array
        //resource.o
        //Setup mModules so the index matches up with the response index.
    }

    public class DownloadModules extends AsyncTask<ApiRequest, Integer, String> {
        protected String doInBackground(ApiRequest... requests){
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
                //mData = data;
                return data;
            }
            catch (UnknownHostException e){
                //setQuestionList("{objects: [{text: 'Unknown Host???', responses: []}]}");
            }
            catch (ConnectException e){
                ///setQuestionList("{objects: [{text: 'Connection Error', responses: []}]}");
            }
            catch (FileNotFoundException e){
                //setQuestionList("{objects: [{text: 'The IT guy is turning the computer on and off. SERVER PROBLEM', responses: []}]}");
            }
            catch (Exception e){
                throw new RuntimeException(e.toString());
            }
            return null;
        }
        protected void onPostExecute(String data) {
            setAllModuleLists(data);
        }

    }
    class ResponseAdapter extends ArrayAdapter<DataResponse> {

        public ArrayList<DataResponse> objects;

        public ResponseAdapter(Context context, int textViewResourceId, ArrayList<DataResponse> _objects) {
            super(context, textViewResourceId, _objects);
            this.objects = _objects;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent){
            View v = convertView;
            if(v == null){
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.response, parent, false);
            }
            final DataResponse i = objects.get(position);
            if(i != null){
                try {
                    String text = i.text;
                    TextView title = (TextView)v.findViewById(R.id.response_title);
                    ImageView btn_module = (ImageView)v.findViewById(R.id.response_modules_button);
                    if(title != null){
                        title.setText(text);
                    }
                    if(btn_module != null){
                        btn_module.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    FragmentManager fg = ((FragmentActivity) getContext()).getSupportFragmentManager();
                                    String creator = "username";
                                    try{
                                        creator = i.creator.username;
                                    } catch (NullPointerException e){
                                        creator = "missing";
                                    }
                                    Fragment frag = Detail_Response.newInstance(i.text, creator, i.id, mResponses.get(position).modules);
                                    FragmentTransaction transaction = fg.beginTransaction();
                                    transaction.replace(R.id.main_content, frag);
                                    transaction.addToBackStack("");
                                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    transaction.commit();
                                }catch (Exception e){
                                    throw new RuntimeException(e.toString());
                                }
                            }
                        });
                    }
                }catch(Exception e){
                    throw new RuntimeException(e.toString());
                }
            }
            return v;
        }

    }
}
