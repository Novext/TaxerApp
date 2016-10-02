package com.novext.taxerapp;

/**
 * Created by angel on 9/30/16.
 */

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by angel on 04/08/16.
 */
public class OkHttpRequest {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    RequestBody body;
    Request request;
    Response response;
    String url;
    OkHttpClient client = new OkHttpClient();

    public OkHttpRequest(String _url){
        url = _url;
    }

    public Response get(String path){
        String uri = url + path;
        try{
            request = new Request.Builder()
                    .url(uri)
                    .get()
                    .build();
            response = client.newCall(request).execute();
        }catch (IOException e){

        }
        return null;
    }

    public Response post(String values,String path){

        String uri = url + path;

        try {
            body = RequestBody.create(JSON, values);
            request = new Request.Builder()
                    .url(uri)
                    .post(body)
                    .build();
            response = client.newCall(request).execute();
            return response;
        }catch (IOException e){
            Log.e("[Connection Error]",e.toString());
        }
        return null;
    }

    public Response put(String values,String path){
        String uri = url + path;

        try {
            body = RequestBody.create(JSON, values);
            request = new Request.Builder()
                    .url(uri)
                    .put(body)
                    .build();
            response = client.newCall(request).execute();
            return response;
        }catch (IOException e){
            Log.e("[Connection Error]",e.toString());
        }
        return null;
    }


}