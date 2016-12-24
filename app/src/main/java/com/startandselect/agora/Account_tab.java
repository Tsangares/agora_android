package com.startandselect.agora;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Account_tab extends Fragment{
    public static int RC_LOGIN_AGORA = 6116;
    public static int RC_REGISTER_AGORA = 6226;
    public static int RC_GOOG = 1661;

    private static final String ARG_UESR_DATA = "user_data";

    public Account_tab() {
    }

    public static Account_tab newInstance() {
        Account_tab fragment = new Account_tab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().getString(ARG_UESR_DATA) != null) {
                refreshAccount();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_tab, container, false);
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.account_sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agoraSignOut();
            }
        });
        getView().findViewById(R.id.account_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivityForResult(new Intent(getActivity(), Register.class), RC_REGISTER_AGORA);
            }
        });
        //Agora Login Button
        View.OnClickListener openLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), Login.class), RC_LOGIN_AGORA);
            }
        };
        getView().findViewById(R.id.account_profile_card).setOnClickListener(openLogin);
        getView().findViewById(R.id.account_sign_in_agora).setOnClickListener(openLogin);

        if(!Profile.getLoggedIn())getActivity().findViewById(R.id.account_loggedin_container).setVisibility(View.GONE);
        else getActivity().findViewById(R.id.account_loggedin_container).setVisibility(View.VISIBLE);
        //Data setup
        refreshAccount();
    }
    public void refreshAccount(){
        refreshAccount((ViewGroup)getView());
    }
    public void refreshAccount(ViewGroup container){
        if(Profile.getLoggedIn()) getActivity().findViewById(R.id.account_loggedin_container).setVisibility(View.VISIBLE);
        refreshProfileName(container);
        refreshMyQuestions(container);
        refreshMyResponses(container);
        refreshMyVotes(container);
        refreshResponseVotes(container);
        refreshQuestionVotes(container);
        refreshTotalResponses(container);
    }

    public void agoraSignin(String data){
        displaySignout();
    }

    public void agoraSignOut(){
        removeSignout();
    }
    public void updateUI(boolean signInSuccess) {
        if (signInSuccess) {
            displaySignout();
        } else {
            displaySignin();
        }
    }
    public void displaySignin(){
        View button1 = getActivity().findViewById(R.id.account_sign_in_agora);
        View button2 = getActivity().findViewById(R.id.account_register);
        button1.animate().scaleY(1).scaleX(1).start();
        button2.animate().scaleY(1).scaleX(1).start();
    }
    public void removeSignin(){
        View button1 = getActivity().findViewById(R.id.account_sign_in_agora);
        View button2 = getActivity().findViewById(R.id.account_register);
        button1.animate().scaleY(0).scaleX(0).start();
        button2.animate().scaleY(0).scaleX(0).start();
    }
    public void displaySignout(){
        try {
            View sign_out = getActivity().findViewById(R.id.account_sign_out_button);
            if(sign_out.getVisibility()==View.VISIBLE)return;
            sign_out.setScaleX(1);
            sign_out.setScaleY(1);
            sign_out.setVisibility(View.VISIBLE);
            //sign_out.animate().scaleY(1).scaleX(1).start();
        }catch (Exception e){
            e.toString();
        }
    }
    public void removeSignout(){
        final View sign_out = getActivity().findViewById(R.id.account_sign_out_button);
        sign_out.setVisibility(View.VISIBLE);
        sign_out.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                sign_out.setVisibility(View.GONE);
                displaySignin();
            }
        }).start();
    }

    public void refreshMyQuestions(ViewGroup container){
        try{
            TextView stats_myQuestions = (TextView)container.findViewById(R.id.account_stats_myQuestions);
            stats_myQuestions.setText(Profile.getMyQuestions().toString());
        }catch (Exception e){
            e.toString();
        }
    }
    public void refreshMyResponses(ViewGroup container){
        try {
            TextView stats_myResponses = (TextView) container.findViewById(R.id.account_stats_myResponses);
            stats_myResponses.setText(Profile.getMyResponses().toString());
        }catch (Exception e){
            e.toString();
        }
    }
    public void refreshMyVotes(ViewGroup container){
        try {
            TextView stats_myVotes = (TextView)container.findViewById(R.id.account_stats_myVotes);
            stats_myVotes.setText(Profile.getMyVotes().toString());
        }catch (Exception e){
            e.toString();
        }
    }

    public void refreshQuestionVotes(ViewGroup container){
        try {
            TextView stats_QuestionVotes = (TextView) container.findViewById(R.id.account_stats_QuestionVotes);
            stats_QuestionVotes.setText(Profile.getQuestionVotes().toString());
        }catch (Exception e){
            e.toString();
        }
    }

    public void refreshResponseVotes(ViewGroup container){
        try{
            TextView stats_ResponseVotes = (TextView)container.findViewById(R.id.account_stats_ResponseVotes);
            stats_ResponseVotes.setText(Profile.getResponseVotes().toString());
        }catch (Exception e){
            e.toString();
        }
    }
    public void refreshTotalResponses(ViewGroup container){
        try{
            TextView stats_totalResponses = (TextView)container.findViewById(R.id.account_stats_totalResponses);
            stats_totalResponses.setText(Profile.getTotalResponses().toString());
        }catch(Exception e){
            e.toString();
        }
    }

    public void refreshProfileName(ViewGroup container){
        try{
            TextView profileName = (TextView)container.findViewById(R.id.account_profile_name);
            profileName.setText(Profile.getUsername());
        }catch(Exception e){
            e.toString();
        }
    }
}
