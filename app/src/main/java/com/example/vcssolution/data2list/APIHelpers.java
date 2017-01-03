package com.example.vcssolution.data2list;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by VCSSolution on 1/3/2017.
 */

public class APIHelpers {
    private Context context;
    private String API_ENDPOINT_PROD = "http://122.165.112.126:3037/";
    private String SYNC_API_ENDPO_SYNC_PROD = "http://122.165.112.126:90/";
    public void data(String name,String phone,String subject,Context context, Handler handler){
        try
        {
            this.context = context;
            JSONObject requestJson = new JSONObject();
            requestJson.put("Name", name);
            requestJson.put("Phone", phone);
            requestJson.put("Subject", subject);

            URL url = new URL(API_ENDPOINT_PROD + "api/test");
            HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
            urlc.setRequestMethod("POST");
            urlc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlc.setDoOutput(true);
            urlc.setAllowUserInteraction(true);
            PrintStream ps = new PrintStream(urlc.getOutputStream());
            ps.print(requestJson.toString());
            ps.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String responseString = reader.readLine();

            JSONObject aJsonObj = new JSONObject(responseString);
            boolean isSaved = Boolean.valueOf( (String)aJsonObj.get("isSaved"));

            if(isSaved)
            {
                Message message = new Message();
                Bundle mBundle = new Bundle();
                mBundle.putString("responseStatus", (String)aJsonObj.get("message"));
                mBundle.putString("msgTitle", "Message");
                message.setData(mBundle);
                message.what = 1;
                handler.sendMessage(message);
            }
        }
        catch (IOException e) {
            Message message = new Message();
            Bundle mBundle = new Bundle();
            mBundle.putString("responseStatus", "The service could not be reached. Please try again later.");
            mBundle.putString("msgTitle", "Cannot Connect");
            message.setData(mBundle);
            message.what = 0;
            handler.sendMessage(message);
        }
        catch (JSONException e) {

            Message message = new Message();
            Bundle mBundle = new Bundle();
            mBundle.putString("responseStatus", "The service could not be reached. Please try again later.");
            mBundle.putString("msgTitle", "Cannot Connect");
            message.setData(mBundle);
            message.what = 0;
            handler.sendMessage(message);
        }
    }
}
