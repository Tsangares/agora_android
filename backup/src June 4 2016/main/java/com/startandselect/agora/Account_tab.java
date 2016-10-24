package com.startandselect.agora;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.IntegerRes;
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
        implements  GoogleApiClient.OnConnectionFailedListener,
                    View.OnClickListener,
                    GoogleSignInApi,
                    OnAccountListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public final static int RC_GOOG = 6969;
    public final static int RC_AGORA = 666;
    private String mParam1;
    private String mParam2;

    public GoogleSignInOptions gso;
    public GoogleApiClient mGoogleApiClient;
    public String name = "Doe";
    public Integer user_id = null;


    public Integer[] recentQuestions = {};
    private Integer myQuestions = 0;
    private Integer myResponses = 0;
    private Integer myVotes = 0;
    private Integer QuestionVotes = 0;
    private Integer ResponseVotes = 0;
    private Integer TotalResponses = 0;

    public Account_tab() {
        // Required empty public constructor
    }

    public static Account_tab newInstance(String param1, String param2) {
        Account_tab fragment = new Account_tab();
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
        ViewGroup output = (ViewGroup)inflater.inflate(R.layout.fragment_account_tab, container, false);
        View accountCard = (View)output.findViewById(R.id.account_profile_card);
        View loginButton = (View)output.findViewById(R.id.account_sign_in_agora);
        //ViewGroup linearContainer = (ViewGroup)output.findViewById(R.user_id.account_container);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId()
                .build();
        if(mGoogleApiClient != null){
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        output.findViewById(R.id.account_sign_in_google).setOnClickListener(this);
        View.OnClickListener openLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);*/
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
        refreshAccount(output);
        updateAccount(output);
        if(user_id != null){
            removeSignin();
            displaySignout();
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
    //Google api
    public void onClick(View v){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        getActivity().startActivityForResult(signInIntent, RC_GOOG);
        removeSignin();
    }

    public void agoraSignin(String data){
        displaySignout();
        try{
            JSONObject acct = new JSONObject(data);
            updateAccount(acct, (ViewGroup) getView());
        }catch(Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public void agoraSignOut(){
        name = "Doe";
        setEmpty();
        removeSignout();
    }

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Fucked
    }
    public void updateAccountFromid(int input){

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
    public void refreshAccount(ViewGroup container){
        refreshProfileName(container);
        refreshMyQuestions(container);
        refreshMyResponses(container);
        refreshMyVotes(container);
        refreshResponseVotes(container);
        refreshQuestionVotes(container);
        refreshTotalResponses(container);
    }
    public void updateAccount(final ViewGroup container){
        AsyncTask<Void, Void, Void> net = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    if(Looper.myLooper() == null)Looper.prepare();
                    URL url = new URL("https://startandselect.com/scripts/CheckUser.php");
                    HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                    connect.setRequestMethod("POST");

                    //post attributes
                    RestParam parameters = new RestParam();
                    if(user_id != null) {
                        parameters.add("user_id", user_id.toString());
                    }

                    OutputStream os = connect.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(parameters.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    connect.connect();

                    InputStream is = new BufferedInputStream(connect.getInputStream());
                    String data = Common.convertinputStreamToString(is);
                    updateAccount(new JSONObject(data), container);
                }catch (Exception e){
                    e.toString();
                }
                return null;
            }
        };
    }
    public void updateAccount(JSONObject data, ViewGroup container){
        try {
            setProfileName(data.getString("username"));
            setMyQuestions(data.getString("MyQuestions"));
            setMyResponses(data.getString("MyResponses"));
            setMyVotes(data.getString("MyVotes"));
            setQuestionVotes(data.getString("QuestionVotes"));
            setResponseVotes(data.getString("ResponseVotes"));
            setTotalResponses(data.getString("TotalResponses"));
            user_id = data.getInt("id");
            if(container != null)refreshAccount(container);
        }catch (Exception e){
            throw new RuntimeException(e.toString());
        }
    }
    public Integer getUser_id() {
        return user_id;
    }
}
