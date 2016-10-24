package com.startandselect.agora;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Account_tab extends Fragment
        implements  GoogleSignInApi{
    public static int RC_AGORA = 6116;
    public static int RC_GOOG = 1661;

    private static final String ARG_UESR_DATA = "user_data";

    public GoogleSignInOptions gso;
    public GoogleApiClient mGoogleApiClient;

    public DataUser user = null;

    public String name = "Doe";
    public Integer user_id = null;
    public String key = "";

    private Integer myQuestions = 0;
    private Integer myResponses = 0;
    private Integer myVotes = 0;
    private Integer QuestionVotes = 0;
    private Integer ResponseVotes = 0;
    private Integer TotalResponses = 0;

    public Account_tab() {
    }

    public static Account_tab newInstance(String user_data) {
        Account_tab fragment = new Account_tab();
        Bundle args = new Bundle();
        if(user_data == null){
            args.putString(ARG_UESR_DATA, user_data);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setAccount(new DataUser(getArguments().getString(ARG_UESR_DATA)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup output = (ViewGroup)inflater.inflate(R.layout.fragment_account_tab, container, false);
        View accountCard = output.findViewById(R.id.account_profile_card);
        View loginButton = output.findViewById(R.id.account_sign_in_agora);

        //Google Api
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId()
                .build();
        if(mGoogleApiClient != null){
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        //hmm
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        output.findViewById(R.id.account_sign_in_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                getActivity().startActivityForResult(signInIntent, RC_GOOG);
                removeSignin();
            }
        });

        //Agora Login Button
        View.OnClickListener openLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSignin();
                getActivity().startActivityForResult(new Intent(getActivity(), Login.class), RC_AGORA);
            }
        };
        output.findViewById(R.id.account_sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agoraSignOut();
            }
        });
        accountCard.setOnClickListener(openLogin);
        loginButton.setOnClickListener(openLogin);
        return output;
    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //This is shit:
        refreshAccount(); //Initializes all of the fields?
        if(user != null){
            //Sign in user from the data.
            setAccount(user);
            removeSignin();
            displaySignout();
        }
    }

    public void setAccount(DataUser data){
        user = data;
        setProfileName(data.username);
        setMyQuestions(data.total_questions);
        setMyResponses(data.total_responses);
        setMyVotes(data.total_votes);
        setQuestionVotes(-1);
        setResponseVotes(-1);
        setTotalResponses(-1);
        refreshAccount();
    }

    public void agoraSignin(String data){
        displaySignout();
        try{
            setAccount(new DataUser(data));
        }catch(Exception e){
            throw new RuntimeException(e.toString());
        }
    }

    public void agoraSignOut(){
        name = "Doe";
        setEmpty();
        removeSignout();
    }

    //Google api
    public void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            name = "Jane";

            try {
                String temp = acct.getEmail();
                if(temp != null)name = temp;
            }catch (Exception e){
                //Get something else
                e.toString();
            }
            try {
                String temp = acct.getDisplayName();
                if(temp != null)name = temp;
            }catch (Exception e){
                //get another
                e.toString();
            }
            try{
                String temp = acct.getGivenName();
                if(temp != null)name = temp;
            }catch (Exception e){
                //get another
                e.toString();
            }
            try{
                user_id = Integer.parseInt(acct.getId());
            }catch(Exception e){
                //no user_id!?
                e.toString();
            }
            TextView handcock = (TextView)getActivity().findViewById(R.id.account_profile_name);
            handcock.setText(name);

            updateUI(true);
        } else {
            updateUI(false);
        }

    }
    public void updateUI(boolean signInSuccess){
        if(signInSuccess){
            displaySignout();
        }else{
            displaySignin();
        }
    }
    public GoogleSignInResult getSignInResultFromIntent (Intent data){
        return null;
    }
    public PendingResult<Status> signOut(GoogleApiClient out){
        out.disconnect();
        return null;
}
    public PendingResult<Status> revokeAccess (GoogleApiClient client){
        return null;
    }
    public OptionalPendingResult<GoogleSignInResult> silentSignIn (GoogleApiClient client){
        return null;
    }

    public Intent getSignInIntent (GoogleApiClient client){
        return null;
    }

    public void displaySignin(){
        View button1 = getActivity().findViewById(R.id.account_sign_in_agora);
        View button2 = getActivity().findViewById(R.id.account_sign_in_google);
        button1.animate().scaleY(1).scaleX(1).start();
        button2.animate().scaleY(1).scaleX(1).start();
    }
    public void removeSignin(){
        View button1 = getActivity().findViewById(R.id.account_sign_in_agora);
        View button2 = getActivity().findViewById(R.id.account_sign_in_google);
        button1.animate().scaleY(0).scaleX(0).start();
        button2.animate().scaleY(0).scaleX(0).start();
    }
    public void displaySignout(){
        try {
            View sign_out = getActivity().findViewById(R.id.account_sign_out_button);
            //sign_out.setScaleX(0);
            //sign_out.setScaleY(0);
            sign_out.setVisibility(View.VISIBLE);
            sign_out.animate().scaleY(1).scaleX(1).start();
        }catch (Exception e){
            try {
                View sign_out = getView().findViewById(R.id.account_sign_out_button);
                //sign_out.setScaleX(0);
                //sign_out.setScaleY(0);
                sign_out.setVisibility(View.VISIBLE);
                sign_out.animate().scaleY(1).scaleX(1).start();
            }catch (Exception k){
                k.toString();
            }
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
    public void setMyQuestions(String input){
        setMyQuestions(Integer.parseInt(input));
    }
    public void setMyQuestions(Integer input){
        myQuestions = input;
    }
    public void addMyQuestions(Integer input){
        myQuestions+=input;
    }
    public Integer getMyQuestions(){
        return myQuestions;
    }
    public void setMyResponses(String input){
        setMyResponses(Integer.parseInt(input));
    }
    public void setMyResponses(Integer input){
        myResponses = input;
    }
    public void addMyResponses(Integer input){
        myResponses += input;
    }
    public Integer getMyResponses(){
        return myResponses;
    }
    public void setMyVotes(String input){
        setMyVotes(Integer.parseInt(input));
    }
    public void setMyVotes(Integer input){
        myVotes = input;
    }
    public void addMyVotes(Integer input){
        myVotes += input;
    }
    public Integer getMyVotes(){
        return myVotes;
    }
    public void setQuestionVotes(String input){
        setQuestionVotes(Integer.parseInt(input));
    }
    public void setQuestionVotes(Integer input){
        QuestionVotes = input;
    }
    public void addQuestionVotes(Integer input){
        QuestionVotes += input;
    }
    public Integer getQuestionVotes(){
        return QuestionVotes;
    }
    public void setResponseVotes(String input){
        setResponseVotes(Integer.parseInt(input));
    }
    public void setResponseVotes(Integer input){
        ResponseVotes = input;
    }
    public void addResponseVotes(Integer input){
        ResponseVotes += input;
    }
    public Integer getResponseVotes(){
        return ResponseVotes;
    }
    public void setTotalResponses(String input){
        setTotalResponses(Integer.parseInt(input));
    }
    public void setTotalResponses(Integer input){
        TotalResponses = input;
    }
    public void addTotalResponses(Integer input){
        TotalResponses += input;
    }
    public Integer getTotalResponses(){
        return TotalResponses;
    }
    public void setProfileName(String newName){
        name = newName;
    }
    public void setEmpty(){
        setProfileName("Doe");
        setMyQuestions(0);
        setMyResponses(0);
        setMyVotes(0);
        setQuestionVotes(0);
        setResponseVotes(0);
        setTotalResponses(0);
        user_id = null;
        user = null;
        key = null;
    }

    public void refreshMyQuestions(ViewGroup container){
        try{
            TextView stats_myQuestions = (TextView)container.findViewById(R.id.account_stats_myQuestions);
            stats_myQuestions.setText(myQuestions.toString());
        }catch (Exception e){
            e.toString();
        }
    }
    public void refreshMyResponses(ViewGroup container){
        try {
            TextView stats_myResponses = (TextView) container.findViewById(R.id.account_stats_myResponses);
            stats_myResponses.setText(myResponses.toString());
        }catch (Exception e){
            e.toString();
        }
    }
    public void refreshMyVotes(ViewGroup container){
        try {
            TextView stats_myVotes = (TextView)container.findViewById(R.id.account_stats_myVotes);
            stats_myVotes.setText(myVotes.toString());
        }catch (Exception e){
            e.toString();
        }
    }

    public void refreshQuestionVotes(ViewGroup container){
        try {
            TextView stats_QuestionVotes = (TextView) container.findViewById(R.id.account_stats_QuestionVotes);
            stats_QuestionVotes.setText(QuestionVotes.toString());
        }catch (Exception e){
            e.toString();
        }
    }

    public void refreshResponseVotes(ViewGroup container){
        try{
            TextView stats_ResponseVotes = (TextView)container.findViewById(R.id.account_stats_ResponseVotes);
            stats_ResponseVotes.setText(ResponseVotes.toString());
        }catch (Exception e){
            e.toString();
        }
    }
    public void refreshTotalResponses(ViewGroup container){
        try{
            TextView stats_totalResponses = (TextView)container.findViewById(R.id.account_stats_totalResponses);
            stats_totalResponses.setText(TotalResponses.toString());
        }catch(Exception e){
            e.toString();
        }
    }

    public void refreshProfileName(ViewGroup container){
        try{
            TextView profileName = (TextView)container.findViewById(R.id.account_profile_name);
            profileName.setText(name);
        }catch(Exception e){
            e.toString();
        }
    }
    public void refreshAccount(){
        refreshAccount((ViewGroup)getView());
    }
    public void refreshAccount(ViewGroup container){
        refreshProfileName(container);
        refreshMyQuestions(container);
        refreshMyResponses(container);
        refreshMyVotes(container);
        refreshResponseVotes(container);
        refreshQuestionVotes(container);
        refreshTotalResponses(container);
    }
}
