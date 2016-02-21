package com.travelcoins.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CoinActivity extends AppCompatActivity {


    Intent intent;
    String name;
    String img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        String newString;
        if (savedInstanceState == null) {
            intent = getIntent();
            name = intent.getStringExtra("name");
            img = intent.getStringExtra("img");
        }

        TextView txtv = (TextView) findViewById(R.id.name);
        txtv.setText(name);

        ImageView imgview = (ImageView) findViewById(R.id.img_category);

        String iconName;
        switch (img) {
            case "4bf58dd8d48988d12d941735":
                imgview.setImageResource(R.drawable.monument_coin);
                break;
            case "4bf58dd8d48988d181941735":
                imgview.setImageResource(R.drawable.museum_coin);
                break;
            case "4bf58dd8d48988d1fe931735":
                imgview.setImageResource(R.drawable.transport_coin);
                break;
            case "4bf58dd8d48988d1fd931735":
                imgview.setImageResource(R.drawable.transport_coin);
                break;
            case "4d4b7105d754a06372d81259":
                imgview.setImageResource(R.drawable.educational_coin);
                break;
            default:
                imgview.setImageResource(R.drawable.hackupc_coin);
        }

    }

}
