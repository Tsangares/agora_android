package com.startandselect.agora.content;

import android.app.Service;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bowyer.app.fabtoolbar.FabToolbar;
import com.startandselect.agora.R;

import org.json.JSONArray;

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
        final FabToolbar sortToolbar = (FabToolbar) output.findViewById(R.id.sort_toolbar);
        final FloatingActionButton sortFab = (FloatingActionButton) output.findViewById(R.id.sort_fab);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        fetchPopularTagData();
        return output;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                    //processPopularTagData(Common.convertInputStreamToString(is));
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
