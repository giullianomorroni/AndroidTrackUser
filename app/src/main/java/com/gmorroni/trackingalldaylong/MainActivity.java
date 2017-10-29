package com.gmorroni.trackingalldaylong;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        location();
    }

    private void location() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        saveLocation(location);
        final LocationRequest mLocationRequest = new LocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void saveLocation(Location location){
        if (location != null) {
            ((TextView)findViewById(R.id.textViewLatitude)).setText(String.valueOf(location.getLatitude()));
            ((TextView)findViewById(R.id.textViewLongitude)).setText(String.valueOf(location.getLongitude()));
            Context context = this;
            SharedPreferences sharedPref = context.getSharedPreferences("lat-long", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            Set<String> values = new HashSet();

            values = sharedPref.getStringSet("values", values);

            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(location.getTime());

            String result =
            String.valueOf(location.getLatitude()) +" - "+
            String.valueOf(location.getLongitude()) +" - Ac:"+
            String.valueOf(location.getAccuracy()) +" Dt:"+
            String.valueOf(instance.get(Calendar.DAY_OF_MONTH))+"/"+
            String.valueOf(instance.get(Calendar.MONTH))+"/"+
            String.valueOf(instance.get(Calendar.YEAR))+" - "+
            String.valueOf(instance.get(Calendar.HOUR_OF_DAY))+":"+
            String.valueOf(instance.get(Calendar.MINUTE));

            values.add(result);
            editor.putStringSet("values", values);
            editor.commit();

            ArrayList<String> items = new ArrayList();
            for (String r : values){
                items.add(r);
            }

            ListView listView = (ListView) findViewById(R.id.listViewLocations);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.fragment_item, items);
            listView.setAdapter(arrayAdapter);
        }
    }

    public void clicked(View v){
        location();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.getLogger("").info(String.valueOf(connectionResult));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.getLogger("").info(String.valueOf(i));
    }

    @Override
    public void onLocationChanged(Location location) {
        saveLocation(location);
    }

}