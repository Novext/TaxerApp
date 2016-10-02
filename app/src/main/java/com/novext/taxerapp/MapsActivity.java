package com.novext.taxerapp;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.Response;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap mMap;
    private OkHttpRequest okHttpRequest;
    private Button btnLocationMe;
    private GoogleApiClient mGoogleApiClient;
    private BottomSheetBehavior behavior;
    SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        okHttpRequest = App.getInstanceOkHttpRequest();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
//        final Toolbar toolbar = (Toolbar) findViewById(R.id.gmail_toolbar);
//        setSupportActionBar(toolbar);

        btnLocationMe = (Button) findViewById(R.id.btnLocationMe);

        View bottomSheet = coordinatorLayout.findViewById(R.id.info_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btnLocationMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    LocationRequest mLocationRequest;

    Location mLastLocation;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

        }
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.isMyLocationEnabled();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Projection proj = googleMap.getProjection();
                int zoom = (int)mMap.getCameraPosition().zoom;

                Point point = proj.toScreenLocation(marker.getPosition());
                point.y = point.y +  mapFragment.getView().getMeasuredHeight()/(8*zoom);
                LatLng position = proj.fromScreenLocation(point);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 5);
                mMap.animateCamera(cameraUpdate);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

        Marker melbourne = mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400"));
        // Add a marker in Sydney and move the camera
        //        getAllStopsAvailable();

    }

    public void getAllStopsAvailable(){

        new AsyncTask<Void,Void, Response>(){
            @Override
            protected Response doInBackground(Void... params) {
                return okHttpRequest.get("/stops");
            }

            @Override
            protected void onPostExecute(Response res) {
                if(res!=null){
                    if(res.code()==200){
                        try{

                            JSONArray values = new JSONArray(res);
                            for (int i = 0; i < values.length(); i++) {
                                LatLng latlng = new LatLng(values.getJSONObject(i).getDouble("latitude"),
                                        values.getJSONObject(i).getDouble("longitude"));
                                setMarker(latlng,values.getJSONObject(i).getString("description"));
                            }
                        }catch (JSONException e){

                        }

                    }
                }

            }
        }.execute(null,null,null);
    }

    public void setMarker(LatLng latLng,String description){
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(description));
    }




}
