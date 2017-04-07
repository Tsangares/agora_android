package com.startandselect.agora.content;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;

import com.startandselect.agora.Master;
import com.startandselect.agora.R;
import com.startandselect.agora.net.DataGeneric;
import com.startandselect.agora.net.DataQuestion;
import com.startandselect.agora.net.api.GetDetail;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by astro on 3/27/17.
 */

public class QuestionList extends AgoraList {
    public AgoraAdapter adapter;
    public static AgoraList newInstance(Context context) {
        Bundle args = new Bundle();
        QuestionList fragment = new QuestionList();
        fragment.type = GetDetail.TYPE_QUESTION;
        fragment.adapter = new AgoraAdapter(context, new ArrayList<DataGeneric>());
        fragment.setListAdapter(fragment.adapter);
        //fragment.setArguments(args);
        return fragment;
    }
    public void onListItemClick(ListView list, View view, int pos, long id){
        DataQuestion q = (DataQuestion)adapter.getItem(pos);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        //Make a reponselist think then commit this transaction.
        /*Fragment frag = ResponseList.newInstance(q.text, creator, mQuestions.get(i).responses);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_content, frag);
        transaction.addToBackStack(Master.FRAG_AGORA);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();*/
    }
    public void onEndExecute(JSONArray objects){
        adapter.addAll(DataQuestion.getQuestionArray(objects));
        adapter.notifyDataSetChanged();
    }
}
