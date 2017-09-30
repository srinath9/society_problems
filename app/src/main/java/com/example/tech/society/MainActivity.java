package com.example.tech.society;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tech.society.addItem.AddBorrowViewModel;
import com.example.tech.society.models.DataBaseRetrieve;
import com.example.tech.society.models.ProblemModel;
import com.google.gson.JsonElement;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.commons.geojson.Feature;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.tech.society.R.id.mapView;

import com.mapbox.services.commons.models.Position;
import com.mapbox.services.android.ui.geocoder.GeocoderAutoCompleteView;
import com.mapbox.services.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private final String TAG = "asoduuassdiuas";
    private MapView mapView;
    private Marker featureMarker;
    private AddBorrowViewModel addBorrowViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1Ijoia29kYWxpaGFuIiwiYSI6ImNqODMxdW92aTJwOXQyd3M2bXVraWkyYW0ifQ.aaGCz5DsfJB36Jsst4bXog");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        addBorrowViewModel = ViewModelProviders.of(this).get(AddBorrowViewModel.class);

        DataBaseRetrieve data = new DataBaseRetrieve() {
            @Override
            public void onReceive(List<ProblemModel> val) {
                Log.i(TAG, "onReceive: "+val);
            }
        };
        addBorrowViewModel.getData(data);
//        Log.i(TAG, "onCreate: get data "+);

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView et_city = (TextView) findViewById(R.id.et_city);
                TextView et_area = (TextView) findViewById(R.id.et_area);
                TextView et_problem = (TextView) findViewById(R.id.et_problem);

                addBorrowViewModel.addBorrow(new ProblemModel(
                        et_city.getText().toString(),
                        et_area.getText().toString(),
                        et_problem.getText().toString(),
//                        0.0,0.0,
                        featureMarker.getPosition().getLatitude(),
                        featureMarker.getPosition().getLongitude(),
                        new Date()
                ));

                Toast.makeText(getBaseContext(), "Your problem has been reported", Toast.LENGTH_SHORT).show();

//                Log.i(TAG, "onClick: list "+addBorrowViewModel);
            }
        });

        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);

//        list.add("Eleven");
//        list.add("Twelve");
//        list.add("Thirteen");
//        list.add("Fourteen");
//        list.add("Fifteen");
        String[] list = getResources().getStringArray(R.array.list_of_issues);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item , list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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



// Retrieve UserLocation
//        mv.getUserLocation();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

//                if(mapView.getMyLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
//                    mapView.setCenterCoordinate(new LatLngZoom(mapView.getMyLocation().getLatitude(), mapView.getMyLocation().getLongitude(), 20), true);
//                }

                String locationProvider = LocationManager.NETWORK_PROVIDER;
// Or use LocationManager.GPS_PROVIDER


                callMapChanges(mapboxMap);

            }
        });



    }
    private void callMapChanges(final MapboxMap mapboxMap){


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


        LatLng lat = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        updateMap(mapboxMap , lat.getLatitude(), lat.getLongitude());

        mapboxMap.clear();
        featureMarker = mapboxMap.addMarker(new MarkerOptions().
                position(lat));


        mapboxMap.selectMarker(featureMarker);

        mapboxMap.setOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                mapboxMap.clear();
                featureMarker = mapboxMap.addMarker(new MarkerOptions().
                        position(mapboxMap.getCameraPosition().target));


                mapboxMap.selectMarker(featureMarker);

            }
        });

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
