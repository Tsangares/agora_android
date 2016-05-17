package com.startandselect.agora;

import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class Agora_tab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnAgoraListener mListener;

    private String predata = null;

    public Agora_tab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Agora_tab.
     */
    // TODO: Rename and change types and number of parameters
    public static Agora_tab newInstance(String param1, String param2) {
        Agora_tab fragment = new Agora_tab();
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
        final View output = inflater.inflate(R.layout.fragment_agora_tab, container, false);
        final ScrollView scrollView = (ScrollView)output.findViewById(R.id.tab_agora_scroll);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(output.getWindowToken(), 0);
                return false;
            }
        });

        getAllQuestions();
        return output;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void SetQuestions(final String data){
        predata = data;

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
    public void processData(final String data){
        try {
            final ViewGroup thisContainer = (ViewGroup) getActivity().findViewById(R.id.tab_agora_linearlayout);
            if(thisContainer != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        thisContainer.setPadding(10,10,10,10);
                        try {
                            thisContainer.removeAllViews();
                            JSONArray arr = new JSONArray(data);
                            for(int i = 0; i < arr.length(); ++i){
                                thisContainer.addView(new QuestionCard(getContext(), arr.getJSONObject(i)));
                            }
                        }catch(Exception e){
                            throw new RuntimeException(e.toString());
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
    public void getAllQuestions(){
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    URL url = new URL("http://startandselect.com/scripts/DownloadAllQuestions.php");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    /*//post attributes
                    ContentValues parameters = new ContentValues();
                    parameters.put("value", "test");

                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.toString());
                    writer.flush();
                    writer.close();
                    os.close();*/

                    connect.connect();

                    InputStream is = new BufferedInputStream(connect.getInputStream());
                    processData(Common.convertinputStreamToString(is));
                }catch (Exception e){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to hookup with server...", Toast.LENGTH_SHORT).show();
                            final Button failed = new Button(getContext());
                            failed.setText(R.string.failed_agora_getAll);
                            failed.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    failed.setText(R.string.retry_agora_getAll);
                                    getAllQuestions();
                                }
                            });
                            final ViewGroup thisContainer = (ViewGroup) getActivity().findViewById(R.id.tab_agora_linearlayout);
                            thisContainer.removeAllViews();
                            thisContainer.addView(failed);
                        }
                    });
                }
                return null;
            }
        };
        net.execute();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAgoraListener {
        // TODO: Update argument type and name

        void onFragmentInteraction(Uri uri);
    }
}