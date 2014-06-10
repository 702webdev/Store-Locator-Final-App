package com.example.storelocatorwithoutpersistence;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 5/10/14
 * Time: 12:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        float lat=getIntent().getExtras().getFloat("lat");
        float lng=getIntent().getExtras().getFloat("lng");
        LatLng loc=new LatLng(lat,lng);
        GoogleMap googleMap=null;
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,15.0f));
            Marker TP = googleMap.addMarker(new MarkerOptions().
                    position(loc).title("Store"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        Button b=(Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}