package com.united.lovender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import im.delight.android.location.SimpleLocation;

public class UserCurrentLocation{ // getting user current location

    private Context context;

    UserCurrentLocation(Context context){
        this.context = context;
    }

//getting current location
    public HashMap<String, String> getUserCurrentLocation(){
        try {
            HashMap<String, String> hashMap = new HashMap<>();
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String gps_address1, gps_address2, country_name = null;
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(getLatitude(),getLongitude(), 1);
            gps_address1 = addresses.get(0).getAddressLine(0);
          //  gps_address2 = addresses.get(0).getAddressLine(1);
            country_name = addresses.get(0).getCountryName();
           // if (!TextUtils.isEmpty(gps_address2)){
           //     hashMap.put("address", gps_address1+" "+gps_address2+", "+country_name);
           // }
          //  else {
                hashMap.put("address", gps_address1+", "+country_name);
          //  }
            hashMap.put("latitude", String.valueOf(getLatitude()));
            hashMap.put("longitude", String.valueOf(getLongitude()));
            return hashMap;
        } catch (IOException | IndexOutOfBoundsException | NullPointerException e) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("address", "");
            hashMap.put("latitude", "");
            hashMap.put("longitude", "");
            return hashMap;
        }
    }

    //getting current country
    public String getUserCurrentCountry(){
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            String country_name = null;
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(getLatitude(),getLongitude(), 1);
            country_name = addresses.get(0).getCountryName();
            return country_name;
        } catch (IOException | IndexOutOfBoundsException | NullPointerException e) {
            return "";
        }
    }


    //    getting user location
    public double getLatitude(){

        // construct a new instance of SimpleLocation
        SimpleLocation location = new SimpleLocation(context, false, true);
        // if we can't access the location yet
        try{
            if (!location.hasLocationEnabled()) {
                // ask the user to enable location access
                SimpleLocation.openSettings(context);
            }
        }
        catch (Exception ignored){ }

        return location.getLatitude();


    }
    public double getLongitude() {
        // construct a new instance of SimpleLocation
        SimpleLocation location = new SimpleLocation(context);
         //if we can't access the location yet
        try{
            if (!location.hasLocationEnabled()) {
                // ask the user to enable location access
                SimpleLocation.openSettings(context);
            }
        }
        catch (Exception ignored){ }
        return location.getLongitude();
    }

}
