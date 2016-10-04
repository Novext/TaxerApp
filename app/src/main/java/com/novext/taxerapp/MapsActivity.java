package com.novext.taxerapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.google.maps.android.ui.IconGenerator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Response;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private GoogleMap mMap;
    private OkHttpRequest okHttpRequest;
    private Button btnLocationMe;
    private GoogleApiClient mGoogleApiClient;
    private BottomSheetBehavior behavior;
    private SupportMapFragment mapFragment;
    private TextView txtStops,txtName,txtPlate,txtTime,txtCompany;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker[] stops;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<Marker> stopsList;

    private ImageButton imgMenu;
    IconGenerator iconFactory;
    boolean flag = false;

    BroadcastReceiver mNotificationsReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        iconFactory = new IconGenerator(this);
        iconFactory.setRotation(0);
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        okHttpRequest = App.getInstanceOkHttpRequest();
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        txtStops = (TextView) findViewById(R.id.txtStops);
        btnLocationMe = (Button) findViewById(R.id.btnLocationMe);
        txtName = (TextView) findViewById(R.id.txtDriverName);
        txtPlate = (TextView) findViewById(R.id.txtPlate);
        txtCompany = (TextView) findViewById(R.id.txtCompany);
        txtTime = (TextView) findViewById(R.id.txtTime);
        getAllStopsAvailable();

        stopsList = new ArrayList();
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
                locationMe();
            }
        });


        if(checkPlayServices()){
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            }
        }
        imgMenu = (ImageButton) findViewById(R.id.imgMenu);

        setNavigationDrawer();
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawer.openDrawer(Gravity.LEFT);
            }
        });

        mNotificationsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                final double latitude = Double.valueOf(bundle.getString("latitude"));
                final double longitude = Double.valueOf(bundle.getString("longitude"));
                final String _id = bundle.getString("_id");
                final String description = bundle.getString("description");
                final int minutes = Integer.valueOf(bundle.getString("minutes"));
                final int seconds = Integer.valueOf(bundle.getString("seconds"));


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        setMarker(new LatLng(latitude,longitude),_id,description,minutes,seconds);
                    }
                });

            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        LocalBroadcastManager.getInstance(MapsActivity.this)
                .registerReceiver(mNotificationsReceiver, new IntentFilter("notification"));
        super.onStart();
    }

    public void calculateTaxiStops(){

    }


    public void setNavigationDrawer(){
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        Drawer.setDrawerListener(mDrawerToggle);
    }


    public void getInfoStop(final String id){
        new AsyncTask<Void,Void,Response>(){
            @Override
            protected Response doInBackground(Void... params) {
                return okHttpRequest.get("/stops/"+id);
            }

            @Override
            protected void onPostExecute(Response response) {
                if(response!=null){
                    if(response.code()==200){
                        try{
                            JSONObject info = new JSONObject(response.body().string());
                            txtName.setText(info.getJSONObject("taxidriver").getString("name") + " " + info.getJSONObject("taxidriver").getString("lastname"));
                            txtPlate.setText(info.getJSONObject("taxidriver").getString("plate"));
                            txtTime.setText(info.getJSONObject("stop").getString("minutes") + " : 00");
                        }catch (Exception e){
                            Log.e("ERROR",e.toString());
                        }
                    }
                }
            }
        }.execute(null,null,null);
    }


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
        mLastLocation = location;
        if(!flag){
            locationMe();
            flag = true;
        }
    }

    public void locationMe(){
        if(mLastLocation!=null){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), 15);
            mMap.animateCamera(cameraUpdate);
            addMarkerUser(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        }

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
        if(mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        LocalBroadcastManager.getInstance(MapsActivity.this)
                .unregisterReceiver(mNotificationsReceiver);
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals(userMarker)){

                    Projection proj = googleMap.getProjection();
                    int zoom = (int)mMap.getCameraPosition().zoom;

                    getInfoStop(marker.getSnippet());

                    Point point = proj.toScreenLocation(marker.getPosition());
                    point.y = point.y +  mapFragment.getView().getMeasuredHeight()/(8*zoom);
                    LatLng position = proj.fromScreenLocation(point);

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 15);
                    mMap.animateCamera(cameraUpdate);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                return true;
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Error", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

                            JSONArray values = new JSONArray(res.body().string());
                            for (int i = 0; i < values.length(); i++) {
                                LatLng latlng = new LatLng(values.getJSONObject(i).getDouble("latitude"),
                                        values.getJSONObject(i).getDouble("longitude"));
                                setMarker(latlng,values.getJSONObject(i).getString("_id"),values.getJSONObject(i).getString("description"),
                                        values.getJSONObject(i).getInt("minutes"),values.getJSONObject(i).getInt("seconds"));
                            }
                        }catch (Exception e){
                            Log.e("Exception",e.toString());
                        }

                    }
                }

            }
        }.execute(null,null,null);
    }

    public void addStop(LatLng latLng,String description,int minutes,String _id){

    }

    Marker userMarker;
    public void addMarkerUser(LatLng latLng){
        if(userMarker!=null){
            userMarker.remove();
        }

        userMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_user)));


    }

    public void setMarker(LatLng latLng,String _id,String description,int minutes,int seconds){
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(_id)
                .title(description)
                .anchor(iconFactory.getAnchorU(),iconFactory.getAnchorV())
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(minutes + " : " + seconds))));

        stopsList.add(marker);
        timer(marker,minutes,seconds);
    }

    public void timer(Marker marker,int minutes,int seconds){
        Timeout timeout = new Timeout(minutes,seconds,this,marker);
        timeout.start(0,1000);
    }
}
