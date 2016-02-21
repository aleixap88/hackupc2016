package com.travelcoins.app;

import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.GPSTracker;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.FoursquareVenuesRequestListener;
import br.com.condesales.models.Venue;

public class NavDrawMap extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    public static final String COINS_TOTAL = "coins_total";
    public static final String COINS_MUSEUM = "coin_museum";
    public static final String COINS_UNI = "coins_uni";
    public static final String COINS_METRO = "coins_metro";
    public static final String COINS_BUS =  "coins_bus";
    public static final String COINS_MONUMENT = "coins_monument";

    private Context mActivity;
    private GoogleMap mMap;
    private EasyFoursquareAsync async;
    private ArrayList<Venue> VenuesList;
    private ArrayList<Marker> Markers;
    private VenuesCriteria criteria;
    private Context context;
    private Location mLocation;

    private LatLng mLatLng;
    private Venue v;

    private LocationManager locationManager;
    private LocationListener mLocationListener;

    private TinyDB tinydb;

    private ArrayList<String> saved;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_draw_map);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        tinydb = new TinyDB(getApplicationContext());

        tinydb.putInt(COINS_TOTAL, 0);
        tinydb.putInt(COINS_MUSEUM, 0);
        tinydb.putInt(COINS_UNI, 0);
        tinydb.putInt(COINS_METRO, 0);
        tinydb.putInt(COINS_BUS, 0);
        tinydb.putInt(COINS_MONUMENT, 0);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.count_all).setTitle(Integer.toString(tinydb.getInt(COINS_TOTAL)));
        navigationView.getMenu().findItem(R.id.count_educational).setTitle(Integer.toString(tinydb.getInt(COINS_UNI)));
        navigationView.getMenu().findItem(R.id.count_customs).setTitle(Integer.toString(tinydb.getInt(COINS_METRO)));
        navigationView.getMenu().findItem(R.id.count_museums).setTitle(Integer.toString(tinydb.getInt(COINS_MUSEUM)));
        navigationView.getMenu().findItem(R.id.count_monuments).setTitle(Integer.toString(tinydb.getInt(COINS_MONUMENT)));
        navigationView.getMenu().findItem(R.id.count_transports).setTitle(Integer.toString(tinydb.getInt(COINS_BUS)));



        // ---------------------------------------




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mActivity = getApplicationContext();

        //saved = tinydb.getListString("saved_coins");



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                10, (android.location.LocationListener)this);

        GPSTracker gps = new GPSTracker(mActivity);
        if(gps.canGetLocation()){
            mLocation = gps.getLocation();
            mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_draw_map, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            try
            { Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "TravelCoins");
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            }
            catch(Exception e)
            { //e.toString();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

                VenuesList = venues;
                Markers = new ArrayList<Marker>(venues.size());
                ArrayList<String> saved = tinydb.getListString("saved_coins");

                for (int i = 0; i < venues.size(); ++i) {

                    v = venues.get(i);
                    LatLng latlong = new LatLng(v.getLocation().getLat(), v.getLocation().getLng());

                    if (!isInside(mLocation, latlong)) {

                        if (saved.contains(v.getId())) {

                        } else {
                            Markers.add(mMap.addMarker(new MarkerOptions()
                                    .position(latlong)
                                    .title(v.getName() + " " + v.getCategories().get(0).getName())
                                    .icon(BitmapDescriptorFactory.fromBitmap(resize_MapIcon(v.getCategories().get(0).getId())))));

                        }
                    }
                    else {
                        //COMPROBAR SI YA ESTABA COGIDA O NO O SI ES DE UN RETO, SI NO LO ESTABA LANZAR ACTIVITY
                        Log.v("WOLOLO", "YOU ARE INSIDE");
                        if (saved != null) {
                            for (int j = 0; j < saved.size(); ++j) {
                                if (saved.get(j).equals(v.getId())) {
                                    //Esta esta alli no hacer NAHHH!!
                                    Log.v("WOLOLO", "NOTHING HAPPEN");
                                } else {
                                    //No esta, guardar i lanzar activity
                                    saved = tinydb.getListString("save_cons");
                                    saved.add(v.getId());
                                    tinydb.putListString("saved_coins", saved);

                                    Intent intent = new Intent(context, CoinActivity.class);
                                    intent.putExtra("name", v.getName());
                                    intent.putExtra("img", v.getCategories().get(0).getName());
                                    startActivity(intent);

                                    plusone(v.getCategories().get(0).getId());



                                    Log.v("WOLOLO", "YOU DONT HAVE IT");
                                }
                            }
                        }
                        else {

                            saved = tinydb.getListString("save_cons");
                            saved.add(v.getId());
                            tinydb.putListString("saved_coins", saved);

                            Intent intent = new Intent(context, CoinActivity.class);
                            intent.putExtra("name", v.getName());
                            intent.putExtra("img", v.getCategories().get(0).getName());
                            startActivity(intent);

                        }

                    }
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        }, criteria);


    }



    public Bitmap resize_MapIcon(String category) {
        String iconName;
        //Log.d("CATEGORYYYYYYYYYYYYYYYYYY : ", category);
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
            case "4bf58dd8d48988d129951735":
                iconName = "transport_coin";
                break;
            case "52f2ab2ebcbc57f1066b8b50":
                iconName = "transport_coin";
                break;
            case "52f2ab2ebcbc57f1066b8b4f":
                iconName = "transport_coin";
                break;
            case "4d4b7105d754a06372d81259":
                iconName = "educational_coin";
                break;
            default:
                iconName = "default_coin";
        }
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 70, 70, false);
        return resizedBitmap;
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();

        mLocation = location;
        //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        ArrayList<String> saved = tinydb.getListString("saved_coins");

        if (VenuesList != null) {

            for (int i = 0; i < VenuesList.size(); ++i) {
                Venue v = VenuesList.get(i);
                mLatLng = new LatLng(v.getLocation().getLat(), v.getLocation().getLng());
                if(isInside(location, mLatLng)) {
                    //LANZAR ACTIVITY DE GANAR MONEDA, COMPROBAR SI ERA DE RETO O NO Y TRATARLA

                    for (int k = 0; k < Markers.size(); ++k) {
                        if (Markers.get(k).getPosition().latitude == mLatLng.latitude) {
                            if (Markers.get(k).getPosition().longitude == mLatLng.longitude) {
                                Markers.get(k).remove();
                                saved.add(v.getId());
                                tinydb.putListString("saved_coins", saved);

                                Intent intent = new Intent(this, CoinActivity.class);
                                intent.putExtra("name", v.getName());
                                intent.putExtra("img", v.getCategories().get(0).getName());
                                startActivity(intent);

                                plusone(v.getCategories().get(0).getId());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public boolean isInside(Location location, LatLng latlng) {

        float[] distance = new float[2];

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(25)
                .strokeColor(android.graphics.Color.TRANSPARENT));

        Location.distanceBetween(latlng.latitude, latlng.longitude,
                circle.getCenter().latitude, circle.getCenter().longitude, distance);

        if (distance[0] > circle.getRadius()) {
            //circle.remove();
            return false;
        } else {
            //circle.remove();
            return true;
        }

    }

    public void plusone(String id) {

        int cont;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        switch (id) {
            case "4bf58dd8d48988d12d941735":
                cont = tinydb.getInt(COINS_MONUMENT);
                ++cont;
                tinydb.putInt(COINS_MONUMENT, cont);
                break;
            case "4bf58dd8d48988d181941735":
                cont = tinydb.getInt(COINS_MUSEUM);
                ++cont;
                tinydb.putInt(COINS_MUSEUM, cont);

                navigationView.getMenu().findItem(R.id.count_monuments).setTitle(Integer.toString(tinydb.getInt(COINS_MONUMENT)));


                break;
            case "4bf58dd8d48988d1fe931735":
                cont = tinydb.getInt(COINS_BUS);
                ++cont;
                tinydb.putInt(COINS_BUS, cont);

                navigationView.getMenu().findItem(R.id.count_transports).setTitle(Integer.toString(tinydb.getInt(COINS_BUS)));

                break;
            case "4bf58dd8d48988d1fd931735":
                cont = tinydb.getInt(COINS_METRO);
                ++cont;
                tinydb.putInt(COINS_METRO, cont);

                navigationView.getMenu().findItem(R.id.count_customs).setTitle(Integer.toString(tinydb.getInt(COINS_METRO)));

                break;
            case "4d4b7105d754a06372d81259":
                cont = tinydb.getInt(COINS_MONUMENT);
                ++cont;
                tinydb.putInt(COINS_UNI, cont);

                navigationView.getMenu().findItem(R.id.count_monuments).setTitle(Integer.toString(tinydb.getInt(COINS_MONUMENT)));

                break;
            default:
                cont = tinydb.getInt(COINS_METRO);
                ++cont;
                tinydb.putInt(COINS_METRO, cont);

                navigationView.getMenu().findItem(R.id.count_customs).setTitle(Integer.toString(tinydb.getInt(COINS_METRO)));

                break;

        }

        cont = tinydb.getInt(COINS_TOTAL);
        ++cont;
        tinydb.putInt(COINS_TOTAL, cont);

        navigationView.getMenu().findItem(R.id.count_all).setTitle(Integer.toString(tinydb.getInt(COINS_TOTAL)));


    }

}
