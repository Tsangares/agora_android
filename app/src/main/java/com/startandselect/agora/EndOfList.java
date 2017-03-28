package com.startandselect.agora;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by astro on 2/21/17.
 */
public class EndOfList extends LinearLayout {
    public static final int LOADING = 1;
    public static final int END = 2;

    private ProgressBar loading = null;
    private TextView list_end = null;

    public EndOfList(Context c){
        super(c);
        construct();
    }
    public EndOfList(Context c, int input){
        super(c);
        construct();
        switch (input){
            default:
            case LOADING:
                setLoading();
                break;
            case END:
                setEnd(null);
                break;
        }
    }
    public void construct(){
        setGravity(Gravity.CENTER_HORIZONTAL);
        setPadding(3,3,3,3);
        loading = new ProgressBar(getContext());
        loading.setIndeterminate(true);
        loading.setClickable(false);
        list_end = new TextView(getContext());
        list_end.setClickable(false);
        list_end.setText("No More Questions");
        list_end.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    public void setLoading(){
        //Check to see if view is already there?
        removeAllViews();
        addView(loading);
    }
    public void setEnd(String input){
        if(input == null) input = "No More Questions";
        list_end.setText(input);
        removeAllViews();
        addView(list_end);
    }
}
