package com.united.lovender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;

public class LocationFromMap extends AppCompatActivity {

    private static final int RESULT_OK = -1;
    private GoogleMap googleMap;
    MapView mMapView;
    private TextView search_tv;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Button continue_b;
    private ImageButton current_location_ib;
    private String latitude = null, longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.no_animation);
        setContentView(R.layout.activity_location_from_map);

        mMapView = findViewById(R.id.fm_mapview);
        continue_b = findViewById(R.id.fm_continue_b);
        current_location_ib = findViewById(R.id.fm_current_location_ib);
        search_tv = findViewById(R.id.fm_search_tv);

//        initiating google map
        initiateMap(savedInstanceState);


        //    register button click responses
        clickResponse();
    }

    //    register button click responses
    private void clickResponse() {
        //         current location image button on click
        current_location_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_tv.setHint("Fetching your gps location...");
                GetUserLocationPermissionThenSetLocation(); //RRC
            }
        });

        search_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)){
                    continue_b.setEnabled(true);
                    continue_b.setBackgroundResource(R.color.lightGreen);
                }
                else continue_b.setEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        continue_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String address = search_tv.getText().toString().trim();
                new MaterialDialog.Builder(LocationFromMap.this)
                        .title("Selected Location")
                        .content(""+address)
                        .contentColorRes(R.color.black)
                        .titleColor(getResources().getColor(R.color.black))
                        .positiveText("Confirm")
                        .positiveColorRes(R.color.green)
                        .negativeText("Cancel")
                        .negativeColorRes(R.color.googleRed)
                        .backgroundColor(getResources().getColor(R.color.white))
                        .icon(getResources().getDrawable(R.drawable.ic_location))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)){
                                    ShowToastyMessage showToastyMessage = new ShowToastyMessage(LocationFromMap.this);
                                    showToastyMessage.warningMessage("unable to get latitude and longitude, try again");
                                    return;
                                }
                                MySharedPrefs mySharedPrefs = new MySharedPrefs(LocationFromMap.this);
                                mySharedPrefs.setSelectedMapLocation(address, latitude, longitude);
                                ((ResultReceiver)getIntent().getParcelableExtra("finisher")).send(1, new Bundle());
                                finish();
                            }
                        })
                        .show();
            }
        });

    }





        //  initializing map
    private void initiateMap(Bundle savedInstanceState){
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        MapsInitializer.initialize(LocationFromMap.this);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;

//                set selected location on map on click
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng point) {
                        search_tv.setHint("Fetching...");
                        setSelectedLocation(point);  //  RRC
                    }
                });

                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(31.3260, 75.5762)).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
               // googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                current_location_ib.performClick();
            }
        });
    }
    //  initializing map

    //    set selected location on map and edit text
    private void setSelectedLocation(LatLng point) {

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String gps_address;
            List<Address> addresses;
            addresses = geocoder.getFromLocation(point.latitude,point.longitude, 1);
            gps_address = addresses.get(0).getAddressLine(0);
            search_tv.setText(gps_address);

//                            setting location at google map
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(point.latitude, point.longitude))
                    .anchor(0.5f, 0.1f)
                    .title(gps_address)
                    .snippet(""));
            latitude = String.valueOf(point.latitude);
            longitude = String.valueOf(point.longitude);
            //.icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
    //    set selected location on map and edit text

    //    permission to get user location, if permitted then get user location from gps
    private void GetUserLocationPermissionThenSetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{

            try {
                googleMap.setMyLocationEnabled(true);
                String latitude = String.valueOf(getLatitude());
                String longitude = String.valueOf(getLongitude());

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                String gps_address;
                double lat = Double.parseDouble(latitude);
                double log = Double.parseDouble(longitude);
                List<Address> addresses;
                addresses = geocoder.getFromLocation(getLatitude(),getLongitude(), 1);
                gps_address = addresses.get(0).getAddressLine(0);
                search_tv.setText(gps_address);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title(gps_address));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, log)));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, log), 18.0f));

                this.latitude = latitude;
                this.longitude = longitude;

            } catch (IOException | IndexOutOfBoundsException | NullPointerException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    permission to get user location

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                search_tv.setText(place.getAddress());
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));


                //   Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                // TODO: Handle the error.
                //  Log.i(TAG, status.getStatusMessage());

            }
        }
    }


    //    getting user location
    public double getLatitude(){

        // construct a new instance of SimpleLocation
        SimpleLocation location = new SimpleLocation(this);
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }
        return location.getLatitude();


    }
    public double getLongitude(){
        // construct a new instance of SimpleLocation
        SimpleLocation  location = new SimpleLocation(this);
        // if we can't access the location yet
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }
        return location.getLongitude();
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.exit);
    }

}
