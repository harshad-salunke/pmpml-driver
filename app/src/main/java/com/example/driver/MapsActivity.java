package com.example.driver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import com.example.driver.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Map<String, Marker> mNamedMarkers = new HashMap<String, Marker>();
    Geocoder geocoder;
    int ACCESSS_LOCATION_REQUEST_CODE = 1000;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker userMarker;
    Circle usercirclelocation;
    String Name;
    FirebaseUser firebaseUser;
    String UserUid;
    ChildEventListener markerUpdateListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();
            Users2 users2=dataSnapshot.getValue(Users2.class);
            SetBusLocation(users2,key);
//            Log.d("harshadsalunke", "Adding location for '" + key + "'");
//
//            Double lng = dataSnapshot.child("longitude").getValue(Double.class);
//            Double lat = dataSnapshot.child("latitude").getValue(Double.class);
//            LatLng location = new LatLng(lat, lng);
//            Users2 users2 = dataSnapshot.getValue(Users2.class);
//            Marker marker = mNamedMarkers.get(key);
//
//            if (marker == null) {
//                MarkerOptions options = getMarkerOptions(users2);
//                marker = mMap.addMarker(options.position(location));
//                mNamedMarkers.put(key, marker);
//            } else {
//                marker.setPosition(location);
//            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            String key = dataSnapshot.getKey();
            Users2 users2 = dataSnapshot.getValue(Users2.class);
            SetBusLocation(users2,key);
//            Log.d("BATTERY_SERVICE", "Location for '" + key + "' was updated.");
//            Double lng = dataSnapshot.child("longitude").getValue(Double.class);
//            Double lat = dataSnapshot.child("latitude").getValue(Double.class);
//            LatLng location = new LatLng(lat, lng);
//
//            Marker marker = mNamedMarkers.get(key);
//
//            if (marker == null) {
//                MarkerOptions options = getMarkerOptions(users2);
//                marker = mMap.addMarker(options.position(location));
//                mNamedMarkers.put(key, marker);
//            } else {
//                marker.setPosition(location);
//            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            Log.d("harhad", "Location for '" + key + "' was removed.");

            Marker marker = mNamedMarkers.get(key);
            if (marker != null)
                marker.remove();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            // Unused
            Log.d("harshad", "Priority for '" + dataSnapshot.getKey() + " was changed.");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("harshad", "markerUpdateListener:onCancelled", databaseError.toException());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        Name=intent.getStringExtra("name");
        if(Name!=null){
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("Name", Name).apply();
        }
        else {
          Name=PreferenceManager.getDefaultSharedPreferences(MapsActivity.this).getString("Name", "defaultStringIfNothingFound");

        }
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        UserUid=firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
//        databaseReference.addChildEventListener(markerUpdateListener);
//        User user=new User(-34,135,"sample");
//        User user1=new User(-60,150,"sample 2");
//        databaseReference.push().setValue(user);
//        databaseReference.push().setValue(user1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == ACCESSS_LOCATION_REQUEST_CODE) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startedLocationUpdate();
            } else {
                //Permission not granted
            }
        }
    }



    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("locations are",locationResult.getLastLocation()+"");
            Location   location = locationResult.getLastLocation();


//            setUserLocation(location);
            Users2 users2=new Users2(location.getLatitude(),location.getLongitude(),location.getBearing(),Name);
             databaseReference.child(UserUid).setValue(users2);

        }
    };

    public void SetBusLocation(Users2 users2,String key){
        Location location = new Location("");
        location.setLatitude(users2.getLatitude());
        location.setLongitude(users2.getLongitude());
        location.setBearing(users2.getBaring());

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        Marker marker = mNamedMarkers.get(key);
        if(marker==null){
            MarkerOptions markerOptions=getMarkerOptions(users2);
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float)0.5,(float)0.5);
            marker=mMap.addMarker(markerOptions);
            mNamedMarkers.put(key, marker);

        }else{
            marker.setPosition(latLng);
            marker.setRotation(location.getBearing());

        }

    }



   public void setUserLocation(Location location){
       LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        if(userMarker==null){
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
            //for rotation of car
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float)0.5,(float)0.5);
            userMarker=mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        }else{
            userMarker.setPosition(latLng);
            userMarker.setRotation(location.getBearing());

        }
//        if(usercirclelocation==null){
//            CircleOptions circleOptions=new CircleOptions();
//            circleOptions.center(latLng);
//            circleOptions.strokeColor(4);
//            circleOptions.strokeColor(Color.argb(255,255,0,0));
//            circleOptions.fillColor(Color.argb(32,155,0,0));
//            circleOptions.radius(location.getAccuracy());
//            usercirclelocation=mMap.addCircle(circleOptions);
//        }
//        else {
//            usercirclelocation.setCenter(latLng);
//            usercirclelocation.setRadius(location.getAccuracy());
//        }
    }

    private void startedLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "location permited", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private   void stopLocationUpdate(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

             @Override
             protected void onStart() {
                 if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                     startedLocationUpdate();
                 }
                 else {
                     ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESSS_LOCATION_REQUEST_CODE);
                 }
                 super.onStart();
             }

             @Override
             protected void onStop() {
//        stopLocationUpdate();
                 super.onStop();
             }

             private MarkerOptions getMarkerOptions(Users2 users2) {
        // TODO: Read data from database for the given marker (e.g. Name, Driver, Vehicle type)
        return new MarkerOptions().title(users2.title).snippet("car");
    }


}