package com.jk.locationservicesdemo.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

/**
 * LocationServicesDemo Created by jkp.
 */
public class LocationHelper {
    private final String TAG = this.getClass().getCanonicalName();
    public boolean locationPermissionGranted = false;
    public final int REQUEST_CODE_LOCATION = 101;
  //  Location myLocation;
MutableLiveData<Location> myLocation = new MutableLiveData<>();

    private FusedLocationProviderClient fusedLocationProviderClient = null;

    private static final LocationHelper instance = new LocationHelper();

    public static LocationHelper getInstance() {
        return instance;
    }

    public void checkPermission(Context context) {
        this.locationPermissionGranted = (PackageManager.PERMISSION_GRANTED
                == (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)));

        Log.e(TAG, "Location permission granterd" + this.locationPermissionGranted);

        if (!this.locationPermissionGranted) {
            //request location permission
            requestLocationPermission(context);
        }

    }

    public FusedLocationProviderClient getFusedLocationProviderClient(Context context) {
        if (this.fusedLocationProviderClient == null) {
            this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return this.fusedLocationProviderClient;
    }

    //public Location getLastLocation(Context context)
    public MutableLiveData<Location> getLastLocation(Context context) {
        if (this.locationPermissionGranted) {
            Log.e(TAG, "getLastLocation: Permission Granted....Last location");
            try {
                this.getFusedLocationProviderClient(context)
                        .getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                               if(location != null){
                                   myLocation.setValue(location);
                                   Log.e(TAG,"on Success; Last Location Lat:" + myLocation.getValue().getLatitude() +"Long:" + myLocation.getValue().getLongitude());
//                                   myLocation = new Location(location);
//                                   Log.e(TAG,"on Success; Last Location Lat:" + myLocation.getLatitude() +"Long:" + myLocation.getLongitude());
                               }else {
                                   Log.e(TAG,"onSuccess: Unable to access Location");
                               }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,"onFailure: Failed to get last location" + e.getLocalizedMessage());
                            }
                        });
            } catch (Exception ex) {
                Log.e(TAG, "getLastLocation: Exception occurred while getting last location");
                return null;
            }

           return this.myLocation;
        } else {
            Log.e(TAG, "getLastLocation: Location permission not granted");
            requestLocationPermission(context);
            return null;
        }
    }

    public LatLng performReverseGeoCoding(Context context,String address){
    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    try{
        List<Address> locationList = geocoder.getFromLocationName(address,1);
        if(locationList.size() >0){
            LatLng obtaniedCoords = new LatLng((locationList.get(0).getLatitude()),locationList.get(0).getLongitude());
            Log.e(TAG,"performReverseGeocoding: Obtained Coords" + obtaniedCoords.toString());
            return obtaniedCoords;
        }
    }catch (Exception ex){
        Log.e(TAG,"performReverseGeocoding: Couldn't get the" +
                " latlng for the given address" + ex.getLocalizedMessage());
    }
    return null;
}

    public void requestLocationPermission(Context context){
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                                            Manifest.permission.ACCESS_COARSE_LOCATION},
                                                                            REQUEST_CODE_LOCATION);

 }

    public void getLocationUpdates(Context context, LocationCallback locationCallback){
        if(locationPermissionGranted){
            try{
                this.getFusedLocationProviderClient(context).requestLocationUpdates(this.locationRequest,locationCallback, Looper.getMainLooper());
            }catch (Exception ex){
                Log.e(TAG,"getLocationUpdate: Exception occurred while receiving location update " + ex.getLocalizedMessage());
            }
        }else{
            Log.e(TAG,"getLocationUpdate: Locaion permission denied");
        }
    }
}
