package com.gmorroni.trackingalldaylong;

import android.location.Location;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

/**
 * Created by gmorroni on 30/10/17.
 */

public class MyLocationCallback extends LocationCallback {

    MainActivity mainActivity;

    public MyLocationCallback(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        this.mainActivity.saveLocation(locationResult.getLocations());
        //this.mainActivity.saveLocation(locationResult.getLastLocation());
    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
        if (locationAvailability.isLocationAvailable()) {

        }
    }
}
