package com.travelcoins.app;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;

import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.FoursquareVenuesRequestListener;

import br.com.condesales.models.Venue;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EasyFoursquareAsync async;
    private ArrayList<Venue> VenuesList;
    private VenuesCriteria criteria;
    private Context context;
    private Location mLocation;
    private Venue v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        context = getApplicationContext();
        async = new EasyFoursquareAsync(this);
        //async.requestAccess(this);

        async.getVenuesNearby(new FoursquareVenuesRequestListener() {
            @Override
            public void onVenuesFetched(ArrayList<Venue> venues) {
                Venue v = venues.get(1);
                String s = v.getId();
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < venues.size(); ++i) {
                    v = venues.get(i);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(v.getLocation().getLat(),v.getLocation().getLng()))
                            .title(v.getName()));
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        },criteria);


        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
}
