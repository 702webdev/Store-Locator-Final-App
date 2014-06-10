package com.example.storelocatorwithoutpersistence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * Date: 5/4/14
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoreLocatorActivity extends Activity {
    //http://api.remix.bestbuy.com/v1/stores(area(94103,10))?apiKey=xk5y3v8xgpnn79mgpq9xxzhr&format=json
    TableLayout table;
    String zip = "";
    String dist = "";
    EditText zipcode, distance;
    private ProgressDialog mProgress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // re-attach the activity if the task is still available
        TaskHelper.getInstance().attach("task", this);

        zipcode = (EditText) findViewById(R.id.zipcode);
        distance = (EditText) findViewById(R.id.distance);
        Button fetch = (Button) findViewById(R.id.fetchbtn);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zipcode.getText() == null || zipcode.getText().toString().length() == 0 ||
                        distance.getText() == null || distance.getText().toString().length() == 0) {
                    Toast.makeText(StoreLocatorActivity.this,
                            "Kindly check zipcode and distance", Toast.LENGTH_LONG).show();
                } else {
                    if (zipcode.getText().length() != 5) {
                        Toast.makeText(StoreLocatorActivity.this,
                                "Zipcode must be of 5 characters please!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    zip = zipcode.getText().toString();
                    dist = distance.getText().toString();

                    // start the task
                    FetchStoresAsyncTask t = new FetchStoresAsyncTask(StoreLocatorActivity.this);
                    TaskHelper.getInstance().addTask("task", t, (Activity) StoreLocatorActivity.this);
                    t.execute(new String[]{zipcode.getText().toString()
                            , distance.getText().toString()});
                }
            }
        });
        table = (TableLayout) findViewById(R.id.table);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = StoreLocatorActivity.this.getSharedPreferences("Persistence", MODE_PRIVATE);
        String result = sp.getString("Response", "");
        zipcode.setText(sp.getString("zipcode", ""));
        distance.setText(sp.getString("distance", ""));
        try {
            //Parsing the JSON response
            JSONObject response = new JSONObject(result);
            JSONArray storesArray = response.getJSONArray("stores");
            Log.d("StoresArray", storesArray.toString());
            Context context = StoreLocatorActivity.this;
            String rowHeaders[] = new String[]{"SNO", "StoreID", "Name", "LongName", "Address", "City", "Region", "ZIPCODE",
                    "Country", "Timings", "Services", "Phone", "Postal Code", "Distance"};
            TableRow row;

            row = new TableRow(context);
            //Setting Row Header
            for (int i = 0; i < rowHeaders.length; i++) {
                TextView t = new TextView(context);
                t.setText(rowHeaders[i]); //Set to any meaningful text
                t.setBackgroundColor(Color.DKGRAY);
                row.addView(t); //Attach TextView to its parent (row)

                TableRow.LayoutParams params =
                        (TableRow.LayoutParams) t.getLayoutParams();
                params.column = 0; //place at ith columns.
                //Skip above line if being placed side by side
                params.span = 1; //span these many columns,
                //i.e merge these many cells. Skip if not needed
                params.setMargins(2, 2, 2, 2); //To "draw" margins
                //around (outside) the TextView, skip if not needed
                params.width = TableRow.LayoutParams.MATCH_PARENT;
                //Set width as needed (Important: this and the
                //.height below is for layout of "text" inside
                //the TextView, not for layout of TextView' by its
                //parent)
                params.height = TableRow.LayoutParams.WRAP_CONTENT;
                t.setPadding(2, 2, 2, 2);
                //Skip padding (space around text) above if not
                //needed
                t.setLayoutParams(params); // causes layout update.
                //Skip above if no special setting is needed
            }
            table.addView(row,
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT));
            //Now Setting row values:
            for (int j = 0; j < storesArray.length(); j++) {
                row = new TableRow(context);
                final JSONObject cur = storesArray.getJSONObject(j);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            float lat=Float.parseFloat(cur.get("lat").toString());
                            float lng =Float.parseFloat(cur.get("lng").toString());
                            Intent i=new Intent(StoreLocatorActivity.this,MapsActivity.class);
                            i.putExtra("lat",lat);
                            i.putExtra("lng",lng);
                            startActivity(i);
                        }catch (Exception e){
                            Log.e("Error","Error",e);
                        }
                    }
                });
                for (int i = 0; i <= 13; i++) {
                    TextView t = new TextView(context);
                    if (i == 0) {
                        t.setText(String.valueOf(j + 1)); //Set to any meaningful text
                    } else if (i == 1) {
                        t.setText(cur.getString("storeId"));
                    } else if (i == 2) {
                        t.setText(cur.getString("name"));
                    } else if (i == 3) {
                        t.setText(cur.getString("longName"));
                    } else if (i == 4) {
                        t.setText(cur.getString("address"));
                    } else if (i == 5) {
                        t.setText(cur.getString("city"));
                    } else if (i == 6) {
                        t.setText(cur.getString("region"));
                    } else if (i == 7) {
                        t.setText(cur.getString("fullPostalCode"));
                    } else if (i == 8) {
                        t.setText(cur.getString("country"));
                    } else if (i == 9) {
                        t.setText(cur.getString("hoursAmPm").replace(";", "\n"));
                    } else if (i == 10) {
                        JSONArray servicesjson = cur.getJSONArray("services");
                        StringBuilder sb = new StringBuilder();
                        for (int k = 0; k < servicesjson.length(); k++) {
                            sb.append(servicesjson.getJSONObject(k).getString("service") + "\n");
                        }
                        t.setText(sb.toString());
                    } else if (i == 11) {
                        t.setText(cur.getString("phone"));
                    } else if (i == 12) {
                        t.setText(cur.getString("postalCode"));
                    } else if (i == 13) {
                        t.setText(cur.getString("distance"));
                    }
                    t.setBackgroundColor(Color.DKGRAY);
                    row.addView(t); //Attach TextView to its parent (row)

                    setTextViewParams(t, i);
                }
                table.addView(row,
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT,
                                        TableLayout.LayoutParams.WRAP_CONTENT));
            }
            table.setEnabled(true);
            table.setVisibility(View.DRAWING_CACHE_QUALITY_HIGH);
        } catch (Exception e) {
            Log.e("onPostExecuteException", "JSONObject Block!", e);
        }
        Log.d("onStartJSON", result);

    }

    /*
        Sets the layout params for a given textview inside the table row for a given column.
    */
    public void setTextViewParams(TextView t, int colNo) {
        TableRow.LayoutParams params =
                (TableRow.LayoutParams) t.getLayoutParams();
        params.column = colNo; //place at ith columns.
        //Skip above line if being placed side by side
        params.span = 1; //span these many columns,
        //i.e merge these many cells. Skip if not needed
        params.setMargins(2, 2, 2, 2); //To "draw" margins
        //around (outside) the TextView, skip if not needed
        params.width = TableRow.LayoutParams.MATCH_PARENT;
        //Set width as needed (Important: this and the
        //.height below is for layout of "text" inside
        //the TextView, not for layout of TextView' by its
        //parent)
        params.height = TableRow.LayoutParams.MATCH_PARENT;
        t.setPadding(2, 2, 2, 2);
        //Skip padding (space around text) above if not
        //needed
        t.setLayoutParams(params); // causes layout update.
        //Skip above if no special setting is needed
    }

    public void updateUI(String result) {
        table.removeAllViews();
        SharedPreferences sp = StoreLocatorActivity.this.getSharedPreferences("Persistence", MODE_PRIVATE);
        sp.edit().putString("Response", result).commit();
        sp.edit().putString("distance", dist).commit();
        sp.edit().putString("zipcode", zip).commit();

        try {
            //Parsing the JSON response
            JSONObject response = new JSONObject(result);
            JSONArray storesArray = response.getJSONArray("stores");
            Log.d("StoresArray", storesArray.toString());
            Context context = StoreLocatorActivity.this;
            String rowHeaders[] = new String[]{"SNO", "StoreID", "Name", "LongName", "Address", "City", "Region", "ZIPCODE",
                    "Country", "Timings", "Services", "Phone", "Postal Code", "Distance"};
            TableRow row;

            row = new TableRow(context);

            //Setting Row Header
            for (int i = 0; i < rowHeaders.length; i++) {
                TextView t = new TextView(context);
                t.setText(rowHeaders[i]); //Set to any meaningful text
                t.setBackgroundColor(Color.DKGRAY);
                row.addView(t); //Attach TextView to its parent (row)

                TableRow.LayoutParams params =
                        (TableRow.LayoutParams) t.getLayoutParams();
                params.column = 0; //place at ith columns.
                //Skip above line if being placed side by side
                params.span = 1; //span these many columns,
                //i.e merge these many cells. Skip if not needed
                params.setMargins(2, 2, 2, 2); //To "draw" margins
                //around (outside) the TextView, skip if not needed
                params.width = TableRow.LayoutParams.MATCH_PARENT;
                //Set width as needed (Important: this and the
                //.height below is for layout of "text" inside
                //the TextView, not for layout of TextView' by its
                //parent)
                params.height = TableRow.LayoutParams.WRAP_CONTENT;
                t.setPadding(2, 2, 2, 2);
                //Skip padding (space around text) above if not
                //needed
                t.setLayoutParams(params); // causes layout update.
                //Skip above if no special setting is needed
            }
            table.addView(row,
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT));
            //Now Setting row values:
            for (int j = 0; j < storesArray.length(); j++) {
                row = new TableRow(context);

                final JSONObject cur = storesArray.getJSONObject(j);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            float lat=Float.parseFloat(cur.get("lat").toString());
                            float lng =Float.parseFloat(cur.get("lng").toString());
                            Intent i=new Intent(StoreLocatorActivity.this,MapsActivity.class);
                            i.putExtra("lat",lat);
                            i.putExtra("lng",lng);
                            startActivity(i);
                        }catch (Exception e){
                            Log.e("Error","Error",e);
                        }
                    }
                });
                for (int i = 0; i <= 13; i++) {
                    TextView t = new TextView(context);
                    if (i == 0) {
                        t.setText(String.valueOf(j + 1)); //Set to any meaningful text
                    } else if (i == 1) {
                        t.setText(cur.getString("storeId"));
                    } else if (i == 2) {
                        t.setText(cur.getString("name"));
                    } else if (i == 3) {
                        t.setText(cur.getString("longName"));
                    } else if (i == 4) {
                        t.setText(cur.getString("address"));
                    } else if (i == 5) {
                        t.setText(cur.getString("city"));
                    } else if (i == 6) {
                        t.setText(cur.getString("region"));
                    } else if (i == 7) {
                        t.setText(cur.getString("fullPostalCode"));
                    } else if (i == 8) {
                        t.setText(cur.getString("country"));
                    } else if (i == 9) {
                        t.setText(cur.getString("hoursAmPm").replace(";", "\n"));
                    } else if (i == 10) {
                        JSONArray servicesjson = cur.getJSONArray("services");
                        StringBuilder sb = new StringBuilder();
                        for (int k = 0; k < servicesjson.length(); k++) {
                            sb.append(servicesjson.getJSONObject(k).getString("service") + "\n");
                        }
                        t.setText(sb.toString());
                    } else if (i == 11) {
                        t.setText(cur.getString("phone"));
                    } else if (i == 12) {
                        t.setText(cur.getString("postalCode"));
                    } else if (i == 13) {
                        t.setText(cur.getString("distance"));
                    }
                    t.setBackgroundColor(Color.DKGRAY);
                    row.addView(t); //Attach TextView to its parent (row)

                    setTextViewParams(t, i);
                }
                table.addView(row,
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT,
                                        TableLayout.LayoutParams.WRAP_CONTENT));
            }
            table.setEnabled(true);
            table.setVisibility(View.DRAWING_CACHE_QUALITY_HIGH);
        } catch (Exception e) {
            Log.e("onPostExecuteException", "JSONObject Block!", e);
        }
        Log.d("readJSON", result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // detach the activity so we dont leak
        TaskHelper.getInstance().detach("task");


    }

    public void showProgressDialog(String msg) {
        mProgress = new ProgressDialog(StoreLocatorActivity.this);
        mProgress.setMessage(msg);
        mProgress.setCancelable(false);
        mProgress.show();
    }

    public void dismissProgressDialog() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }
}