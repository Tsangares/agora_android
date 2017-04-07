package com.startandselect.agora.net.api;

import android.os.AsyncTask;
import android.os.Looper;

import com.startandselect.agora.net.ApiRequest;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by astro on 2/17/17.
 */
public abstract class Fetch extends AsyncTask<ApiRequest, Integer, String> {
    public static String getOrder(Integer order){
        return "date";
    }
    public final static Integer LIMIT = 20;
    public final static Integer OFFSET = 0;
    public final static String TYPE_QUESTION = "question";
    public final static String TYPE_RESPONSE = "response";
    public final static String TYPE_MODULE   = "module";
    public final static String TYPE_COMMENT = "comment";
    public final static String TYPE_COMMENT_VOTE = "comment_vote";
    public final static String TYPE_RESPONSE_VOTE = "response_vote";

    public static String convertInputStreamToString(InputStream ists)
            throws IOException {
        String output = null;
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(
                        ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                    //if(!r1.ready()){
                     //   break;
                    //}
                }
            } finally {
                ists.close();
            }
            output = sb.toString();
        }
        return output;
    }
    protected String doInBackground(ApiRequest... requests){
        try{
            ApiRequest request = requests[0];
            if(Looper.myLooper() == null)Looper.prepare();
            URL url = request.getURL();
            HttpURLConnection connect = (HttpURLConnection)url.openConnection();
            connect.setRequestMethod(request.getMethod());
            if(request.hasData() && !request.getMethod().equals(ApiRequest.GET)) {
                OutputStream os = connect.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(request.getData());
                writer.flush();
                writer.close();
                os.close();
            }
            connect.connect();
            InputStream is = new BufferedInputStream(connect.getInputStream());
            String data = convertInputStreamToString(is);
            return data;
        }
        /*catch (UnknownHostException e){
            throw new UnknownHostException( e.toString());
            //No host
        }
        catch (ConnectException e){
            throw new RuntimeException( e.toString());
            //No connection
        }
        catch (FileNotFoundException e){
            throw new RuntimeException( e.toString());
            //Server is down
        }*/
        catch (Exception e){
            throw new RuntimeException( e.toString());
            //Something else happened
        }
    }
}
