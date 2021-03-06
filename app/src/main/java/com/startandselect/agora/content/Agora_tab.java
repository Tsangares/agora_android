package com.startandselect.agora.content;

import android.app.Activity;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.startandselect.agora.Master;
import com.startandselect.agora.R;
import com.startandselect.agora.account.Account_tab;
import com.startandselect.agora.net.ApiRequest;
import com.startandselect.agora.net.DataQuestion;
import com.startandselect.agora.net.DataResource;

import org.json.JSONArray;

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

import com.startandselect.agora.EndOfList;
import com.startandselect.agora.net.api.Fetch;
import com.startandselect.agora.net.api.GetList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAgoraListener} interface
 * to handle interaction events.
 * Use the {@link Agora_tab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Agora_tab extends Fragment  {

    private static final String ARG_DATA = "question_data"; //WTF is this???

    public QuestionAdapter mQuestionAdapter;
    public String mData;
    public DataResource mData_resource = null;
    public static ArrayList<DataQuestion> mQuestions;
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
                displayError("Looking for all the questions in the chaotic space of the internet...");
                new DownloadQuestions().download(0,20,null);
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
        if(mData == null){
            setQuestionList(new ArrayList<DataQuestion>());
        }else{
            setQuestionList(mData);
        }
    }
    public void setQuestionListNext(){
        if(mData == null)return;
        ListView agoraList = (ListView) getView().findViewById(R.id.agora_list_view);
        if(mData_resource != null && mData_resource.meta != null && !mData_resource.meta.next.equals("null")){
            ApiRequest request = new ApiRequest(mData_resource.meta.next, ApiRequest.BASE, ApiRequest.APPEND);
            new DownloadQuestions().execute(request);
        }else{
            endView.setEnd(null);
        }
    }
    public void displayError(final String error){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ListView agoraList = (ListView) getView().findViewById(R.id.agora_list_view);
                    mQuestionAdapter = new QuestionAdapter(getContext(), R.id.question_view, new ArrayList<DataQuestion>());
                    agoraList.setAdapter(mQuestionAdapter);
                    endView.setEnd(error);
                }catch (Exception e){
                    e.toString();
                }
            }
        });

    }
    public void setQuestionList(String data){
        setQuestionList(new DataResource(data));
    }
    public void setQuestionList(String data, View root){
        setQuestionList(new DataResource(data), root);
    }
    public void setQuestionList(DataResource data){
        if(data == null){
            Toast.makeText(getContext(), "problem with data", Toast.LENGTH_SHORT);
            return;
        }
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
        try {

            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (root == null) return;
                    ListView agoraList = (ListView) root.findViewById(R.id.agora_list_view);
                    if (objects != null && agoraList != null) {
                        mQuestionAdapter = new QuestionAdapter(getContext(), R.id.question_view, objects);
                        if (add) {
                            mQuestions.addAll(objects);
                            QuestionAdapter q = (QuestionAdapter) ((HeaderViewListAdapter) agoraList.getAdapter()).getWrappedAdapter();
                            q.notifyDataSetChanged();
                        } else {
                            mQuestions = objects;
                            agoraList.setAdapter(mQuestionAdapter);
                        }
                        if (objects.size() != 0) {
                            endView.setLoading();
                        } else {
                            endView.setEnd(null);
                        }
                    }
                }
            });
        }catch(Exception e){
            Toast.makeText(getContext(), "Problem updating list", Toast.LENGTH_SHORT).show();
            e.toString();
        }
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
    public class DownloadQuestions extends GetList{
        public AsyncTask download(Integer offset,Integer limit, Integer order){
            return init(GetList.TYPE_QUESTION, offset, limit, order);
        }
        //This is of object type because then it can handle both a DataResource returned from the function or an ArrayList<DataQuestion>
        //ArrayList<DataQuestion> is returned by DataBuilder as an attempt to make creating DataQuestions easier.
        //DataResource is returned on a successful data transfer.
        protected void onPostExecute(DataResource data) {
            if(data != null) {
                setQuestionList(data);
            }
        }
    }
}
