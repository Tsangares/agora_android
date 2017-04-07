package com.startandselect.agora;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.startandselect.agora.net.RestRequest;
import com.startandselect.agora.net.api.GetList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class NetTest {
    public class Asynk_Example extends GetList {
        protected void onPostExecute(String data){
            if(data != null){
                success();
            }else{
                //failed
                fail("nope");
            }
        }
    }
    public void success(){
        //To
    }
    @Test
    public void test(){
        //Asynk_Example a = new Asynk_Example().init(GetList.TYPE_QUESTION);
    }

    @Test
    public void is_returning_data() {
        //Make a test for all types of asynks.
        /*Asynk_Example cnt = new Asynk_Example();
        cnt.init
        RestRequest.Think k = new RestRequest.Think();
        return k.init(20,0);
        RestRequest k = new RestRequest();
        try{
            k.process().get();
        }catch (Exception e){
            fail(e.toString());
        }*/

    }

}