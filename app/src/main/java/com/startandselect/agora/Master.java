package com.startandselect.agora;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public class Master extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String FRAG_AGORA = "frag_agora";
    public static final String FRAG_TAGS = "frag_tags";
    public static final String FRAG_ACCT = "frag_acct";
    public static final String FRAG_ADD = "frag_add";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FragmentManager fg = getSupportFragmentManager();
        Fragment frag;
        if(savedInstanceState != null){
            frag = getSupportFragmentManager().getFragment(savedInstanceState, FRAG_AGORA);
        }else{
            frag = Agora_tab.newInstance(null);
        }
        FragmentTransaction transaction = fg.beginTransaction();
        transaction.add(R.id.main_content, frag, FRAG_AGORA);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAG_AGORA, getSupportFragmentManager().findFragmentByTag(FRAG_AGORA));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            if (requestCode == Account_tab.RC_AGORA) {
                String account_data = data.getStringExtra("account");
                FragmentManager fg = getSupportFragmentManager();
                Fragment current = fg.findFragmentById(R.id.main_content);
                Account_tab account_tab = null;
                if(current instanceof Account_tab) {
                    account_tab = (Account_tab) current;
                    account_tab.setAccount(new DataUser(account_data));
                }else{
                    account_tab = Account_tab.newInstance(account_data);
                    FragmentTransaction transaction = fg.beginTransaction();
                    transaction.replace(R.id.main_content, account_tab);
                    transaction.addToBackStack(Master.FRAG_AGORA);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.commit();
                }
            }else if(requestCode == Account_tab.RC_GOOG){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                //Switch to account_tab fragment and put result in the bundle

            }
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fg = getSupportFragmentManager();
        Fragment frag = null;
        FragmentTransaction transaction = fg.beginTransaction();
        if (id == R.id.nav_home) {
            frag = Agora_tab.newInstance(null);
            transaction.replace(R.id.main_content, frag, FRAG_AGORA);
        } else if (id == R.id.nav_tags) {
            frag = Sort_tab.newInstance("","");
            transaction.replace(R.id.main_content, frag, FRAG_TAGS);
        } else if (id == R.id.nav_account) {
            frag = Account_tab.newInstance(null);
            transaction.replace(R.id.main_content, frag, FRAG_ACCT);
        } else if (id == R.id.nav_add) {
            frag = NewQuestion.newInstance();
            transaction.replace(R.id.main_content, frag, FRAG_ADD);
        }
        if(frag != null) {
            transaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
