package com.example.dell.agiitech.Views.Positions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GPSTracer extends Service implements LocationListener {

    Context context;
    private int LOCATION_PERMISSION=1234;
    boolean isGPSEnable = false;
    boolean canGetGPSLocation = false;
    double latitude;
    double longitude;

    static final long MIN_DISTANCE_CHANGE_FOR_UPDATE = 10;

    static final long MIN_TIME_BW_UPDATE = 1000 * 60 * 1;
    Location location;
    LocationManager locationManager;
    boolean isNetWorkEnable = false;


    public GPSTracer(Context context) {
        this.context = context;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            Toast.makeText(context,"Vous avez deja la permission", Toast.LENGTH_LONG).show();
            getLocation();
        }else {
            requestStoragePermission();
        }

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetWorkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnable && !isNetWorkEnable) {

            } else {
                this.canGetGPSLocation = true;
                if (isNetWorkEnable) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return TODO;
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE, this);

                        if (locationManager!=null){
                            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if(location!=null){
                                longitude=location.getLongitude();
                                latitude=location.getLatitude();
                            }
                        }
                    }

                }

                if (isGPSEnable){
                    if (location==null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATE,MIN_DISTANCE_CHANGE_FOR_UPDATE,this);

                        if (locationManager!=null){
                            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if(location!=null){
                                longitude=location.getLongitude();
                                latitude=location.getLatitude();
                            }
                        }
                    }
                }


            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS(){
        if (locationManager!=null){

            locationManager.removeUpdates(GPSTracer.this);
        }
    }

    public double getLatitude(){
        if (location!=null){
            latitude=location.getLatitude();
        }
        return latitude;
    }


    public double getLongitude(){
        if (location!=null){
            longitude=location.getLongitude();
        }
        return longitude;
    }


    public boolean canGetGPSLocation(){
        return this.canGetGPSLocation;
    }



    public void showSettingAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("GPS est parametré");
        builder.setMessage("GPS n\'est pas activé. Voulez-vous l\'activé?");
        builder.setPositiveButton("Parametres", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent in=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(in);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.ACCESS_FINE_LOCATION)){

            new AlertDialog.Builder(this).setTitle("Permission need").setMessage("cause permission")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);

                        }
                    })
                    .setNegativeButton("annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        }else {
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
        }
    }
}
