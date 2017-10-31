package com.gmorroni.trackingalldaylong;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GoogleApiClient mGoogleApiClient;
    String filename = "locations";
    Intent intent;
    List<String> values = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        if (intent == null) {
            MyLocationCallback callback = new MyLocationCallback(this);
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest(), callback , null);
        }
    }

    private LocationRequest locationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(1800000);
        //mLocationRequest.setInterval(20000);

        mLocationRequest.setFastestInterval(600000);
        //mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return mLocationRequest;
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    public void saveLocation(Location location){
        List<Location> locations = new ArrayList();
        locations.add(location);
        saveLocation(locations);
    }
    public void saveLocation(List<Location> locations){
        if (locations != null) {

            readFromFile(this);

            for(Location location : locations) {
                ((TextView)findViewById(R.id.textViewLatitude)).setText(String.valueOf(location.getLatitude()));
                ((TextView)findViewById(R.id.textViewLongitude)).setText(String.valueOf(location.getLongitude()));

                Calendar instance = Calendar.getInstance();
                instance.setTimeInMillis(location.getTime());

                String result =
                        String.valueOf(location.getLatitude()) +" "+
                                String.valueOf(location.getLongitude()) +" - Ac:"+
                                String.valueOf(location.getAccuracy()) +" Dt:"+
                                String.valueOf(instance.get(Calendar.DAY_OF_MONTH))+"/"+
                                String.valueOf(instance.get(Calendar.MONTH))+"/"+
                                String.valueOf(instance.get(Calendar.YEAR))+" - "+
                                String.valueOf(instance.get(Calendar.HOUR_OF_DAY))+":"+
                                String.valueOf(instance.get(Calendar.MINUTE));

                values.add(result);
            }

            writeToFile();
            ListView listView = (ListView) findViewById(R.id.listViewLocations);
            Collections.reverse(values);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.fragment_item, values);
            listView.setAdapter(arrayAdapter);
        }
    }

    private void readFromFile(Context context) {
        try {
            InputStream inputStream = context.openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    values.add(receiveString);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
    }

    private void writeToFile(){
        FileOutputStream outputStream;
        try {
            if(values.size() > 200) {
                values = values.subList(0, 200);
            }

            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(("").getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (String r : values) {
                stringBuilder.append(r+"\n");
            }

            outputStream.write(stringBuilder.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
