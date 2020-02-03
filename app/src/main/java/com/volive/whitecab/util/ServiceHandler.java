//service handler revison on 18-6-2016
// By Cipher


package com.volive.whitecab.util;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;


public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    //photo upload
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    String s = null;
    public String url = null;
    public String param = null;
    public String filepath = null;
    public String requestString = null;

    OkHttpClient client = new OkHttpClient();

    String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public ServiceHandler() {

    }

    public String callToServer(String url, int method, JSONObject jsonobject) throws IOException {
        String json = jsonobject.toString();
        Log.e("", "json:" + json);
        String arraystring = null;
        Response response = null;
        if (method == POST) {
            RequestBody body = RequestBody.create(JSON, String.valueOf(jsonobject));
            FormEncodingBuilder formbody = new FormEncodingBuilder();
            JSONArray array = jsonobject.names();

            for (int i = 0; i < array.length(); i++) {
                try {
                    formbody.add(array.getString(i), jsonobject.getString(array.getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //Log.e("","url is : " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .post(formbody.build())
                    .build();

            Log.e("", "url is : " + request.urlString() + " .. " + request.url());

            response = client.newCall(request).execute();
        } else if (method == GET) {
            Log.e("", "url is : " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            response = client.newCall(request).execute();
        }
        return response.body().string();

    }

    public Response uploadImage(String tempURL, String tempFilePath, String param1) throws Exception {
        this.url = tempURL;
        this.filepath = tempFilePath;
        this.param = param1;

        Response serviceresponse = run();
        return serviceresponse;
    }

    public Response run() throws Exception {
        Log.e("img", "image" + filepath);

        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + param + "\";filename=\"10566design.jpg\""),
                        RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
                //Headers.of("Content-Disposition", "form-data; name=\"status_image\""),
                //RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Log.e("", " res : " + response.priorResponse());
        Log.e("", " res : " + response.networkResponse().toString());
        return response;
    }

    public Response uploadImagenew(String tempURL, String tempFilePath, String param1) throws Exception {
        this.url = tempURL;
        this.filepath = tempFilePath;
        this.param = param1;

        Response serviceresponse = run1();
        return serviceresponse;
    }

    public Response run1() throws Exception {
        Log.e("img", "image" + filepath);

        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\""+param+"\";filename=\"10566design.jpg\""),
//                        RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
                        Headers.of("Content-Disposition", "form-data; name=\"user_image\""),
                        RequestBody.create(MEDIA_TYPE_PNG, new File(filepath).getName()))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Log.e("requestBody", new File(filepath).getName());

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Log.e("", " res : " + response.priorResponse());
        Log.e("", " res : " + response.networkResponse().toString());
        return response;
    }


    private static final String TAG = "ServiceHandler";

    public JSONObject ObjectuploadImage(String strUrl, String userId, String sourceImageFile) {

        try {
            File sourceFile = new File(sourceImageFile);

            Log.d(TAG, "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE = sourceImageFile.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");


            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("uid", userId)
                    .addFormDataPart("API-KEY", "98745632")
                    .addFormDataPart("user_image", "profile.png", RequestBody.create(MEDIA_TYPE_PNG, sourceFile

                    ))

                    .build();

            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            Log.e("response", response.body().string());
            return new JSONObject(response.body().string());

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }

}
