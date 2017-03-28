package com.startandselect.agora.content;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.startandselect.agora.R;
import com.startandselect.agora.net.DataModule;

import java.util.ArrayList;

public class Detail_Response extends Fragment {
    private static final String ARG_RESPONSE_TEXT = "response_text";
    private static final String ARG_RESPONSE_ID = "response_id";
    private static final String ARG_RESPONSE_CREATOR = "response_creator";
    private static final String ARG_MODULES = "modules";

    private String mResponse_text;
    private String mResponse_creator;
    private int mResponse_id;
    private ArrayList<DataModule> mModules;

    public Detail_Response() {}

    public static Detail_Response newInstance(String response_text, String response_creator,int response_id, ArrayList<DataModule> modules) {
        Detail_Response fragment = new Detail_Response();
        Bundle args = new Bundle();
        args.putString(ARG_RESPONSE_TEXT, response_text);
        args.putString(ARG_RESPONSE_CREATOR, response_creator);
        args.putInt(ARG_RESPONSE_ID, response_id);
        args.putParcelableArrayList(ARG_MODULES, modules);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResponse_text = getArguments().getString(ARG_RESPONSE_TEXT);
            mResponse_id = getArguments().getInt(ARG_RESPONSE_ID);
            mResponse_creator = getArguments().getString(ARG_RESPONSE_CREATOR);
            mModules = getArguments().getParcelableArrayList(ARG_MODULES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View output = inflater.inflate(R.layout.fragment_detail_response, container, false);
        TextView response_text =(TextView) output.findViewById(R.id.detail_response_title);
        response_text.setText(mResponse_text);
        TextView response_creator = (TextView) output.findViewById(R.id.detail_response_creator);
        response_creator.setText(mResponse_creator);
        return output;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setModuleList(mModules);
    }
    public void setModuleList(ArrayList<DataModule> input){
        if(input.size() == 0){
            getActivity().findViewById(R.id.modules_none).setVisibility(View.VISIBLE);
        }else{
            getActivity().findViewById(R.id.modules_none).setVisibility(View.GONE);

        }
    }
    public class ModuleAdapter extends ArrayAdapter<DataModule> {

        public ArrayList<DataModule> objects;

        public ModuleAdapter(Context context, int textViewResourceId, ArrayList<DataModule> _objects) {
            super(context, textViewResourceId, _objects);
            this.objects = _objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;
            if(v == null){
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.response, parent, false);
            }
            DataModule i = objects.get(position);
            if(i != null){
                try {
                    String title = i.title;
                    String text = i.text;
                    TextView title_box = (TextView)v.findViewById(R.id.module_title);
                    TextView text_box = (TextView)v.findViewById(R.id.module_text);
                    if(title_box != null){
                        title_box.setText(title);
                    }
                    if(text_box != null){
                        text_box.setText(text);
                    }
                }catch(Exception e){
                    throw new RuntimeException(e.toString());
                }
            }
            return v;
        }

    }
}
