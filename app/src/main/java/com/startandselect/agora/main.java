package com.startandselect.agora;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.bowyer.app.fabtoolbar.FabToolbar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONObject;

public class main extends AppCompatActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static boolean anonymous = true;
    public static boolean loggedin = false;

    public Account_tab  account_tab = null;
    public Agora_tab    agora_tab   = null;
    public Sort_tab     sort_tab    = null;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabmain);
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        sort_tab = Sort_tab.newInstance("null", "null");
        account_tab = Account_tab.newInstance(null);
        agora_tab = Agora_tab.newInstance(null);


        TabLayout tabLayout = (TabLayout)findViewById(R.id.toolbar);
        tabLayout.setupWithViewPager(mViewPager);
        //setting icons
        final int[] ICONS = new int[]{
                R.drawable.ic_home_black_24px,
                R.drawable.ic_sort_black_24px,
                R.drawable.ic_account_box_black_24px
        };
        for(int i = 0; i < ICONS.length; ++i){
            tabLayout.getTabAt(i).setIcon(ICONS[i]);
        }
        //FAB
        final FloatingActionButton agoraFab = (FloatingActionButton) findViewById(R.id.agora_fab);
        final FabToolbar sortToolbar = (FabToolbar) findViewById(R.id.sort_toolbar);
        final FloatingActionButton sortFab = (FloatingActionButton) findViewById(R.id.sort_fab);
        if(!anonymous || !loggedin){
            agoraFab.setVisibility(View.GONE);
        }
        final Activity activity = this;
        agoraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, NewQuestion.class), Login.RC_QUESTION);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        agoraFab.show();
                        sortFab.hide();
                        sortToolbar.hide();
                        break;
                    case 1:
                        agoraFab.hide();
                        sortFab.show();
                        sortToolbar.hide();
                        break;
                    case 2:
                        agoraFab.hide();
                        sortFab.hide();
                        sortToolbar.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        sortFab.hide();
        sortToolbar.hide();
        agoraFab.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        /*switch(requestCode){
            case( Account_tab.RC_GOOG ):
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                if( resultCode == RESULT_OK ){
                    //Good
                }
                break;
            case( Account_tab.RC_LOGIN_AGORA ):
                if(resultCode == RESULT_OK) {
                    account_tab.agoraSignin(data.getStringExtra("account"));
                }
                break;
            case(Login.RC_QUESTION):{
                if(resultCode == RESULT_OK){
                    //agora_tab.addQuestion(data.getStringExtra("question"));
                    account_tab.addMyQuestions(1);
                }
            }

        }*/
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabmain, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

    }
    public void     setMyQuestions(String input){
        account_tab.setMyQuestions(input);
    }
    public void     addMyQuestions(Integer input){
        account_tab.addMyQuestions(input);
    }
    public Integer  getMyQuestions(){
        return account_tab.getMyQuestions();
    }
    public void     setMyResponses(String input){
        account_tab.setMyResponses(input);
    }
    public void     addMyResponses(Integer input){
        account_tab.addMyResponses(input);
    }
    public Integer  getMyResponses(){ return account_tab.getMyResponses(); }
    public void     setMyVotes(String input){ account_tab.setMyVotes(input); }
    public void     addMyVotes(Integer input){
        account_tab.addMyVotes(input);
    }
    public Integer  getMyVotes(){
        return account_tab.getMyVotes();
    }
    public void     setQuestionVotes(String input){
        account_tab.setQuestionVotes(input);
    }
    public void     addQuestionVotes(Integer input){
        account_tab.addQuestionVotes(input);
    }
    public Integer  getQuestionVotes(){
        return account_tab.getQuestionVotes();
    }
    public void     setResponseVotes(String input){
        account_tab.setResponseVotes(input);
    }
    public void     addResponseVotes(Integer input){
        account_tab.addResponseVotes(input);
    }
    public Integer  getResponseVotes(){
        return account_tab.getResponseVotes();
    }
    public void     setTotalResponses(String input){
        account_tab.setTotalResponses(input);
    }
    public void     addTotalResponses(Integer input){
        account_tab.addTotalResponses(input);
    }
    public Integer  getTotalResponses(){
        return account_tab.getTotalResponses();
    }
    public void     setProfileName(String newName){
        account_tab.setProfileName(newName);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0){
                return agora_tab;
            }
            if(position == 1){
                return sort_tab;
            }
            if(position == 2){
                return account_tab;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.agora_tab_title);
                case 1:
                    return getString(R.string.sort_tab_title);
                case 2:
                    return getString(R.string.account_tab_title);
            }
            return null;
        }
    }
}
