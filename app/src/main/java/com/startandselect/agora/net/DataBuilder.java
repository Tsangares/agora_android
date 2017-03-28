package com.startandselect.agora.net;

/**
 * Created by astro on 2/17/17.
 */
public class DataBuilder{
    private DataBuilder(){

    }
    public static DataQuestion buildQuestion(String question){
        DataQuestion temp = new DataQuestion();
        temp.text = question;
        return temp;
    }
    public static DataResource buildResource(){
        return null;
    }
}
