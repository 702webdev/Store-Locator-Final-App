package com.example.storelocatorwithoutpersistence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class FetchStoresAsyncTask extends AsyncTask<String, String, String> {
    private StoreLocatorActivity storeLocatorActivity;
    String urlPrefix = "http://api.remix.bestbuy.com/v1/stores(area(";
    String urlSuffix = "))?apiKey=xk5y3v8xgpnn79mgpq9xxzhr&format=json";
    String apiKey = "xk5y3v8xgpnn79mgpq9xxzhr";

    ProgressDialog mProgress;
    Context context;
    FetchStoresAsyncTask(Context context){
        this.context=context;
    }
    @Override
    protected void onPreExecute() {
        if (storeLocatorActivity != null)
        {
            // reference UI updates using activity
            storeLocatorActivity.showProgressDialog("Fetching store locator info!");
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String zipcode = strings[0];
        String distance = strings[1];

        String readJSON = getJSON(urlPrefix + zipcode + "," + distance + urlSuffix);

        Log.d("readJSON", readJSON);
        return readJSON;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onPostExecute(String result) {
        if (storeLocatorActivity != null)
        {
            // reference UI updates using activity
            storeLocatorActivity.dismissProgressDialog();
            storeLocatorActivity.updateUI(result);
        }

    }

    /**
     * Attaches an activity to the task
     * @param a The activity to attach
     */
    public void attach(Activity a)
    {
        this.storeLocatorActivity = (StoreLocatorActivity)a;
    }

    /**
     * Removes the activity from the task
     */
    public void detach()
    {
        this.storeLocatorActivity = null;
    }
    public String getJSON(String address) {
        //Creating the http Client object necessary to fetch the input response data.
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(address);
        try {
            //Getting the input response via the client object
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            //Checking the http status. If its 200, then proceed to fetch the json response.
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(StoreLocatorActivity.class.toString(), "Failed to get JSON object");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}