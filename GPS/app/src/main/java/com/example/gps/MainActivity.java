package com.example.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity
{
    Switch sw_j_gpsWifi;
    TextView tv_j_longitude;
    TextView tv_j_latitude;
    TextView tv_j_altitude;
    TextView tv_j_address;
    Button btn_j_getLocation;

    //Googles API location services
    FusedLocationProviderClient fusedLocationClient;

    //Location Request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    //Used to see if the user has given us access to their location
    private static final int PERMISSION_ALLOW_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sw_j_gpsWifi = findViewById(R.id.sw_v_gpsWiFi);
        tv_j_longitude = findViewById(R.id.tv_v_longitude);
        tv_j_latitude = findViewById(R.id.tv_v_latitude);
        tv_j_altitude = findViewById(R.id.tv_v_altitude);
        tv_j_address = findViewById(R.id.tv_v_address);
        btn_j_getLocation = findViewById(R.id.btn_v_getLocation);

        locationRequest = new LocationRequest();
        //how often do we want the location check to occur.
        locationRequest.setInterval(30000);
        //what is the fastest that we want this to check for our location
        locationRequest.setFastestInterval(500);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        updateLocationEventListener();
        gpsWifiEventListener();
        getUserPermissions();
    }

    //trigger a method to make sure the user has granted us to use their location
    //called from else statement in getUserPermissions()
    //Right click > generate override methods > OnRequestPermissionsResult()

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(PERMISSION_ALLOW_LOCATION == requestCode)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getUserPermissions();
            }
            else
            {
                //popup message saying they need to accept the permissions
                Toast.makeText(this, "Permissions need to be granted to use this application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateLocationEventListener()
    {
        btn_j_getLocation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getUserPermissions();
            }
        });
    }

    public void gpsWifiEventListener()
    {
        sw_j_gpsWifi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(sw_j_gpsWifi.isChecked())
                {
                    //Request a location with a tradeoff that favors highly accurate locations
                    //at the possible expense of additional power usage
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                }
                else
                {
                    //Requests a location with a balance between location accuracy and power usage
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
            }
        });
    }

    public void getUserPermissions()
    {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>()
            {
                @Override
                public void onSuccess(Location location)
                {
                    updateGPSInfo(location);
                }
            });
        }
        else
        {
            //Permission has not been granted.
            //ask for permission
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ALLOW_LOCATION);
        }
    }

    public void updateGPSInfo(Location loc)
    {
        tv_j_longitude.setText(loc.getLongitude() + "");
        tv_j_latitude.setText(loc.getLatitude() + "");
        tv_j_altitude.setText(loc.getAltitude() + "");
        tv_j_address.setText(loc.getProvider() + "");
    }
}