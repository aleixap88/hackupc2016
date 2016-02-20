package com.travelcoins.app;

import android.content.Context;
import android.location.Criteria;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.condesales.EasyFoursquare;
import br.com.condesales.EasyFoursquareAsync;
import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.CheckInListener;
import br.com.condesales.listeners.FoursquareVenuesRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.TipsRequestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.Tip;
import br.com.condesales.models.User;
import br.com.condesales.models.Venue;
import br.com.condesales.tasks.users.UserImageRequest;

public class MainActivity extends AppCompatActivity implements
        AccessTokenRequestListener {

    private EasyFoursquareAsync async;
    private String msg = "HELLO";
    private EasyFoursquare sync;
    private ArrayList<Venue> VenuesList;
    private VenuesCriteria criteria;
    private Context context = getApplicationContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        async = new EasyFoursquareAsync(this);
        async.requestAccess(this);

    }

    @Override
    public void onError(String errorMsg) {
        // Do something with the error message
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessGrant(String accessToken) {
        // with the access token you can perform any request to foursquare.
        // example:
        //for another examples uncomment lines below:
        async.getVenuesNearby(new FoursquareVenuesRequestListener() {
            @Override
            public void onError(String errorMsg) {

            }

            @Override
            public void onVenuesFetched(ArrayList<Venue> venues) {

                VenuesList = venues;
                Toast.makeText(context, "HI", Toast.LENGTH_SHORT).show();
            }
        },criteria);


    }


}
