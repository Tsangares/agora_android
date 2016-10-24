package com.startandselect.agora;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.Toast;

import com.bowyer.app.fabtoolbar.FabToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Sort_tab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sort_tab extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnAccountListener account;
    public Sort_tab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sort_tab.
     */
    // TODO: Rename and change types and number of parameters
    public static Sort_tab newInstance(String param1, String param2) {
        Sort_tab fragment = new Sort_tab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View output = inflater.inflate(R.layout.fragment_sort_tab, container, false);
        final FabToolbar sortToolbar = (FabToolbar) getActivity().findViewById(R.id.sort_toolbar);
        final FloatingActionButton sortFab = (FloatingActionButton) getActivity().findViewById(R.id.sort_fab);
        final SearchView searchBox = (SearchView)getActivity().findViewById(R.id.search_box);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        searchBox.setQueryHint("Search the mainframe");
        searchBox.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnAgoraListener)getContext()).setSearchQuery(searchBox.getQuery().toString());
            }
        });
        searchBox.setIconifiedByDefault(false);
        sortToolbar.setFab(sortFab);
        sortFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortToolbar.expandFab();
                searchBox.requestFocus();
                imm.showSoftInput(searchBox, 0);
            }
        });
        output.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(searchBox.getQuery().length() == 0){
                    sortToolbar.slideInFab();
                }
                imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                return false;
            }
        });
        fetchPopularTagData();
        return output;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void processPopularTagData(final String data){
        //Process the data from the server.
        try {
            final ViewGroup thisContainer = (ViewGroup) getActivity().findViewById(R.id.tab_sort_main);
            if(thisContainer != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        thisContainer.setPadding(10,10,10,10);
                        try {
                            JSONArray arr = new JSONArray(data);
                            for(int i = 0; i < arr.length(); ++i){
                                QuestionCard tag = new QuestionCard(getContext(), null);
                                final String tagText = arr.getJSONObject(i).getString("tag");
                                tag.QuestionTitle.setText(tagText);
                                tag.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((OnAgoraListener) getContext()).setFilterTags(tagText);
                                    }
                                });
                                thisContainer.addView(tag);
                            }
                        }catch(Exception e){
                            throw new RuntimeException(e.toString());
                        }
                    }
                });
            }else{
                    throw new RuntimeException("Problem with getting a container.");
            }
        }catch(Exception e){
            //Container not ready
            throw new RuntimeException(e.toString());
        }
    }
    public void fetchPopularTagData(){
        //Get the data from the server.
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    Toast.makeText(getContext(), "Asking Server", Toast.LENGTH_SHORT).show();
                    URL url = new URL("https://startandselect.com/scripts/GetPopularTags.php");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    /*//post attributes
                    RestParam parameters = new RestParam();
                    parameters.add("value", "test");

                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.toString());
                    writer.flush();
                    writer.close();
                    os.close();*/

                    connect.connect();

                    InputStream is = new BufferedInputStream(connect.getInputStream());
                    processPopularTagData(Common.convertinputStreamToString(is));
                    Toast.makeText(getContext(), "sent data to frag", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.toString();
                }
                return null;
            }
        };
        net.execute();
    }
}
