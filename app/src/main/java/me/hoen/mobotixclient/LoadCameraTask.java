package me.hoen.mobotixclient;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;

/**
 * Created by vhoen on 23/07/15.
 */
public class LoadCameraTask extends AsyncTask<String, Void, MjpegInputStream> {
    protected OnTaskCompleted loadStreamCallback;
    protected OnTaskCompleted authenticationErrorCallback;
    protected String username;
    protected String password;

    public LoadCameraTask(OnTaskCompleted callback, OnTaskCompleted authenticationErrorCallback, String username, String password) {
        this.loadStreamCallback = callback;
        this.authenticationErrorCallback = authenticationErrorCallback;
        this.username = username;
        this.password = password;
    }

    protected MjpegInputStream doInBackground(String... url) {
        HttpResponse res = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            httpclient.setCredentialsProvider(cp);
        }

        Log.d(MainActivity.TAG, "1. Sending http request");
        try {
            res = httpclient.execute(new HttpGet(URI.create(url[0])));
            Log.d(MainActivity.TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
            if (res.getStatusLine().getStatusCode() == 401) {
                if(authenticationErrorCallback != null){
                    authenticationErrorCallback.onTaskCompleted(null);
                }
                return null;
            }
            return new MjpegInputStream(res.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG, "Request failed-ClientProtocolException", e);
            //Error connecting to camera
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(MainActivity.TAG, "Request failed-IOException", e);
            //Error connecting to camera
        }

        return null;
    }

    protected void onPostExecute(MjpegInputStream result) {
        if (this.loadStreamCallback != null) {
            loadStreamCallback.onTaskCompleted(result);
        }
    }
}
