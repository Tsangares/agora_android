package com.startandselect.agora;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

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
    private static final String ARGS_RESPONSE_DATA = "response_data";
    //private static final String ARGS_AGORA_SESSION = "agora_session";
    public TextView voted;
    private ArrayList<DataResponse> mResponses;
    private String mResponse_title;
    private String mResponse_creator;
    //private Agora_tab mAgora_session;

    public Detail_Question() {
        // Required empty public constructor
    }
    public static Detail_Question newInstance(ArrayList<DataResponse> response_data, String title, String creator) {
        Detail_Question fragment = new Detail_Question();
        Bundle args = new Bundle();
        args.putString(ARGS_RESPONSE_CREATOR, creator);
        args.putString(ARGS_RESPONSE_TITLE, title);
        args.putString(ARGS_RESPONSE_DATA, new Gson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResponse_data = getArguments().getString(ARGS_RESPONSE_DATA));
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
        ResponseAdapter mResponseAdapter = new ResponseAdapter(getContext(), R.id.response_view, toConsumable(mResponse_data));
        questionDetailList.setAdapter(mResponseAdapter);
        ((TextView)output.findViewById(R.id.detail_question_title)).setText(mResponse_title);
        ((TextView)output.findViewById(R.id.detail_question_creator)).setText(mResponse_creator);
        return output;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ListView questionDetailList = (ListView)getActivity().findViewById(R.id.detail_question_list);
        questionDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vote(getResponseTitle(view));
            }
        });
        if(getVote()!=null)displayDetails();
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
            list.getChildAt(i).findViewById(R.id.response_details).setVisibility(View.VISIBLE);
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

}
