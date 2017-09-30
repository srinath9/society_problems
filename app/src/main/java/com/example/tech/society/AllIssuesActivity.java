package com.example.tech.society;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.tech.society.addItem.AddBorrowViewModel;
import com.example.tech.society.models.DataBaseRetrieve;
import com.example.tech.society.models.ProblemModel;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.ui.geocoder.GeocoderAutoCompleteView;
import com.mapbox.services.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.commons.models.Position;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AllIssuesActivity extends AppCompatActivity implements LocationListener {
    private MapView mapView;
    private Marker featureMarker;
    String TAG;
    LatLng lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = getClass().getSimpleName();

        Mapbox.getInstance(this, "pk.eyJ1Ijoia29kYWxpaGFuIiwiYSI6ImNqODMxdW92aTJwOXQyd3M2bXVraWkyYW0ifQ.aaGCz5DsfJB36Jsst4bXog");
        setContentView(R.layout.activity_all_issues);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);





        findViewById(R.id.btn_new_problem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext() , MainActivity.class));
            }
        });


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                DataBaseRetrieve data = new DataBaseRetrieve() {
                    @Override
                    public void onReceive(List<ProblemModel> val ) {
                        Log.i(TAG, "onReceive: "+val);
                        if (val.size() == 0)
                            enterRandomData( lat.getLatitude(), lat.getLongitude());

                        markerExistingIssueLocations(val, mapboxMap);
                    }
                };



                String locationProvider = LocationManager.NETWORK_PROVIDER;
                AddBorrowViewModel addBorrowViewModel = ViewModelProviders.of(AllIssuesActivity.this).get(AddBorrowViewModel.class);
                addBorrowViewModel.getData(data);

                callMapChanges(mapboxMap);





            }
        });
    }

    private void enterRandomData(double lat, double lng) {


        double randomNumLat = ThreadLocalRandom.current().nextDouble(lat * 10000, lat * 10000 + 50);
        double randomNumLng = ThreadLocalRandom.current().nextDouble(lng * 10000, lng * 10000 + 50);
        AddBorrowViewModel addBorrowViewModel = ViewModelProviders.of(AllIssuesActivity.this).get(AddBorrowViewModel.class);

        for(int i=0; i< 4 ; i++) {
            randomNumLat = ThreadLocalRandom.current().nextDouble(lat * 10000-50, lat * 10000 + 50);
            randomNumLng = ThreadLocalRandom.current().nextDouble(lng * 10000-50, lng * 10000 + 50);

            addBorrowViewModel.addBorrow(new ProblemModel(
                    "CityName",
                    "AreaName",
                    "IssueName",
//                        0.0,0.0,
                    randomNumLat / 10000,
                    randomNumLng / 10000,
                    new Date()
            ));
        }


    }

    private void markerExistingIssueLocations(List<ProblemModel> val ,MapboxMap mapboxMap ){
        for ( int i=0; i< val.size() ; i++){
            featureMarker = mapboxMap.addMarker(new MarkerOptions().
                    position(val.get(i).getMapLatlng()));
            mapboxMap.selectMarker(featureMarker);


        }
    }

    private void callMapChanges(final MapboxMap mapboxMap) {

        Log.i(TAG, "callMapChanges: Mapbox.getAccessToken() "+Mapbox.getAccessToken());

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);


        lat = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        updateMap(mapboxMap , lat.getLatitude(), lat.getLongitude());

        mapboxMap.clear();
        featureMarker = mapboxMap.addMarker(new MarkerOptions().
                position(lat));


        mapboxMap.selectMarker(featureMarker);



//        mapboxMap.getSelectedMarkers().get(0).getPosition();
        GeocoderAutoCompleteView autocomplete = (GeocoderAutoCompleteView) findViewById(R.id.query);
        autocomplete.setAccessToken(Mapbox.getAccessToken());
        autocomplete.setType(GeocodingCriteria.TYPE_POI);
        autocomplete.setOnFeatureListener(new GeocoderAutoCompleteView.OnFeatureListener() {
            @Override
            public void onFeatureClick(CarmenFeature feature) {
                hideOnScreenKeyboard();
                Position position = feature.asPosition();
                updateMap(mapboxMap , position.getLatitude(), position.getLongitude());
            }
        });

//
    }
    private void hideOnScreenKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }
    private void updateMap(final MapboxMap mapboxMap ,double latitude, double longitude) {
        // Build marker
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("asdsadasd"));

        // Animate camera to geocoder result location
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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

}
