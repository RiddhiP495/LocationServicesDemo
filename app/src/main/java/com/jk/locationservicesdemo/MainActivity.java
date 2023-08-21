package com.jk.locationservicesdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.jk.locationservicesdemo.databinding.ActivityMainBinding;
import com.jk.locationservicesdemo.helpers.LocationHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = this.getClass().getCanonicalName();
    ActivityMainBinding binding;
    private LocationHelper locationHelper;
private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        this.binding.btnReverseGeocoding.setOnClickListener(this::onClick);
        this.binding.btnOpenMap.setOnClickListener(this::onClick);

        this.locationHelper = LocationHelper.getInstance();
        this.locationHelper.checkPermission(this);

//        this.lastLocation = this.locationHelper.getLastLocation(this);
//
//        if(this.lastLocation != null){
//            this.binding.tvLocationAddress.setText(this.lastLocation.toString());
//        }else{
//            this.binding.tvLocationAddress.setText("Last Location not obtained");
//        }

        this.locationHelper.getLastLocation(this).observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if(location != null){
                    lastLocation = location;
                    binding.tvLocationAddress.setText(lastLocation.toString());
                }else{
                    binding.tvLocationAddress.setText("Location not obtained");
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v != null){
            switch (v.getId()){
                case R.id.btn_open_map:{
                    this.openMap();
                    break;
                }
                case R.id.btn_reverse_geocoding:{
                    this.doReverseGeocoding();
                    break;
                }
            }
        }
    }

    private void openMap(){
        Log.d(TAG, "onClick: Open map to show location");
    }

    private void doReverseGeocoding(){
        Log.d(TAG, "onClick: Perform reverse geocoding to get coordinates from address.");
        if(this.locationHelper.locationPermissionGranted){
            String givenAddress = this.binding.editAddress.getText().toString();
            LatLng obtainedCoords = this.locationHelper.performReverseGeoCoding(this,givenAddress);

            if(obtainedCoords != null){
                this.binding.tvLocationCoordinates.setText("Lat:" + obtainedCoords.latitude + "\nLng:" + obtainedCoords.longitude);
            }else {
                this.binding.tvLocationCoordinates.setText("No Coordinated obtained");

            }
            }else{
            this.binding.tvLocationCoordinates.setText("Couldn't get coordinates from address");
        }

    }

    private void doGeocoding(){

    }
}