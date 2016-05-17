package com.startandselect.agora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Apollonian on 5/12/2016.
 */
public class Common {
    public static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

}
class RestParam{
    public String output = "";
    public int count = 0;
    public void add(String key, String value){
        if(count++ != 0)output += "&";
        output += key + "=" + value;
    }
    public final String get(){
        return output;
    }
}