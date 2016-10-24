package com.startandselect.agora;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
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
    public DataResource mData_resource = null;
    public static ArrayList<DataQuestion> mQuestions;
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
    private String default_data = "{objects: []}";
    private String searchQuery = null;
    private String tags = null;
    //For the listview: to avoid multiple calls for last item
    private int preLast = -1;

    private EndOfList endView;

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
        endView = new EndOfList(getContext());
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            if(mData == null){
                mData = default_data;
                fetchQuestionList();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View output = inflater.inflate(R.layout.fragment_agora_tab, container, false);
        return output;
    }
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView agoraList = (ListView) getView().findViewById(R.id.agora_list_view);
        final Fragment thisFrag = this;
        agoraList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                if(view.equals(endView)){
                    return;
                }
                DataQuestion q = mQuestions.get(i);
                FragmentManager fg = getActivity().getSupportFragmentManager();
                String creator = "username";
                try{
                    creator = q.creator.username;
                }catch (NullPointerException e){
                    //Nothing
                }
                Fragment frag = Detail_Question.newInstance(q.text, creator, mQuestions.get(i).responses);
                FragmentTransaction transaction = fg.beginTransaction();
                transaction.replace(R.id.main_content, frag);
                transaction.addToBackStack(Master.FRAG_AGORA);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit();
            }
        });
        agoraList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if( lastVisibleItem == totalItemCount){
                    if(preLast != lastVisibleItem){
                        setQuestionListNext();
                        preLast = lastVisibleItem;
                    }
                }
            }
        });
        agoraList.addFooterView(endView);
        setQuestionList(mData);

    }
    public void setQuestionListNext(){
        if(mData.equals(default_data))return;
        ListView agoraList = (ListView) getView().findViewById(R.id.agora_list_view);
        if(mData_resource != null && mData_resource.meta != null && !mData_resource.meta.next.equals("null")){
            ApiRequest request = new ApiRequest(mData_resource.meta.next, ApiRequest.BASE, ApiRequest.APPEND);
            new DownloadQuestions().execute(request);
        }else{
            endView.setEnd();
        }
    }
    public void fetchQuestionList(){
        ApiRequest request = new ApiRequest("question/", ApiRequest.FULL);
        new DownloadQuestions().execute(request);
    }
    public void setQuestionList(String data){
        setQuestionList(new DataResource(data));
    }
    public void setQuestionList(String data, View root){
        setQuestionList(new DataResource(data), root);
    }
    public void setQuestionList(DataResource data){
        if(data.getAdd()) {
            addQuestionList(resourceToConsumable(data));
        }else{
            setQuestionList(resourceToConsumable(data));
        }
    }
    public void setQuestionList(DataResource data, View root){
        setQuestionList(resourceToConsumable(data), root);
    }
    public void setQuestionList(ArrayList<DataQuestion> objects){
        setQuestionList(objects, getView());
    }
    public void setQuestionList(final ArrayList<DataQuestion> objects, final View root){
        setQuestionList(objects, root, false);
    }
    public void setQuestionList(final ArrayList<DataQuestion> objects, final View root, final boolean add){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(root==null)return;
                ListView agoraList = (ListView) root.findViewById(R.id.agora_list_view);
                if(objects != null && agoraList != null) {
                    mQuestionAdapter = new QuestionAdapter(getContext(), R.id.question_view, objects);
                    if(add){
                        mQuestions.addAll(objects);
                        QuestionAdapter q = (QuestionAdapter)((HeaderViewListAdapter)agoraList.getAdapter()).getWrappedAdapter();
                        q.notifyDataSetChanged();
                    } else {
                        mQuestions = objects;
                        agoraList.setAdapter(mQuestionAdapter);
                    }
                    if(objects.size()!=0) {
                        endView.setLoading();
                    }else{
                        endView.setEnd();
                    }
                }
            }
        });
    }
    public void addQuestionList(final ArrayList<DataQuestion> objects ){
        setQuestionList(objects, getView(), true);
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
    public ArrayList<DataQuestion> stringToConsumable(String input){
        return resourceToConsumable(new DataResource(input));
    }
    public ArrayList<DataQuestion> resourceToConsumable(DataResource data){
        mData_resource = data;
        if(data == null){
            //No Data Either empty or no more to add
            return null;
        }
        ArrayList<DataQuestion> output = new ArrayList<>();
        try{
            JSONArray objects = data.objects;

            for( int i = 0; i < objects.length(); ++i ){
                output.add(new DataQuestion(objects.getJSONObject(i)));
            }
            //output.add(new DataQuestion("{}"));
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
                return new DataResource(data, request.getHandle());
            }
            catch (UnknownHostException e){
                setQuestionList(new DataResource("{objects: [{text: 'Unknown Host???',id: 1, responses: []}]}"));
            }
            catch (ConnectException e){
                setQuestionList(new DataResource("{objects: [{text: 'Connection Error',id: 1 responses: []}]}"));
            }
            catch (FileNotFoundException e){
                setQuestionList(new DataResource("{objects: [{text: 'The IT guy is turning the computer on and off. SERVER PROBLEM',id: 1, responses: []}]}"));
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
