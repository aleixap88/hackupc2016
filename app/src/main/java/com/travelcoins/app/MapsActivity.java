package com.travelcoins.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.GPSTracker;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.FoursquareVenuesRequestListener;
import br.com.condesales.models.Venue;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Context mActivity;
    private GoogleMap mMap;
    private EasyFoursquareAsync async;
    private ArrayList<Venue> VenuesList;
    private VenuesCriteria criteria;
    private Context context;
    private Location mLocation;

    private LatLng mLatLng;
    private Venue v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mActivity = getApplicationContext();

        GPSTracker gps = new GPSTracker(mActivity);
        if(gps.canGetLocation()){
            mLocation = gps.getLocation();
            mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        }
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

        //Map configuration
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15.5f));
        mMap.setMyLocationEnabled(true);


        async.getVenuesNearby(new FoursquareVenuesRequestListener() {
            @Override
            public void onVenuesFetched(ArrayList<Venue> venues) {
                Venue v = venues.get(1);
                String s = v.getId();
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < venues.size(); ++i) {
                    v = venues.get(i);

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(v.getLocation().getLat(), v.getLocation().getLng()))
                            .title(v.getName()+" "+v.getCategories().get(0).getName())
                            .icon(BitmapDescriptorFactory.fromBitmap(resize_MapIcon(v.getCategories().get(0).getId()))));

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



    public Bitmap resize_MapIcon(String category) {
        String iconName;
        switch (category) {
            case "4bf58dd8d48988d12d941735":
                iconName = "monument_coin";
                break;
            case "4bf58dd8d48988d181941735":
                iconName = "museum_coin";
                break;
            case "4bf58dd8d48988d1fe931735":
                iconName = "transport_coin";
                break;
            case "4bf58dd8d48988d1fd931735":
                iconName = "transport_coin";
                break;
            case "4d4b7105d754a06372d81259":
                iconName = "educational_coin";
                break;
            default:
                iconName = "hackupc_coin";
        }
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 70, 70, false);
        return resizedBitmap;
    }
}
