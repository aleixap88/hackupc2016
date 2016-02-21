package com.travelcoins.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String IDS = "ids";

    private CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        BdHandler bdHandler = new BdHandler();
        bdHandler.execute();

        // -------------------------------------------

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());


        callbackManager = CallbackManager.Factory.create();


        setContentView(R.layout.activity_main);

        //Para arreglar el tema del hash :
        //Log.d("KeyHash:", printHashKey(getApplicationContext()));

        //info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();


                if (!usuario_registrado()) {
                    Log.d("REGISTRO: ", "user registrado!!");
                    Post_handler post_handler = new Post_handler();
                    post_handler.execute();
                }

                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);

                //info.setText("User ID:  " +
                //        loginResult.getAccessToken().getUserId() + "\n" +
                //        "Auth Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                //info.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                //info.setText("Login attempt failed.");
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            Intent i = new Intent(this, NavDrawMap.class);
            startActivity(i);
        }
    }




    private boolean usuario_registrado() {
        TinyDB tinydb = new TinyDB(getApplicationContext());
        ArrayList<String> lista = tinydb.getListString(IDS);
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == AccessToken.getCurrentAccessToken().getUserId()) {
                return true;
            }
        }
        return false;
    }

    public void openmap(View view) {
        Intent i = new Intent(this, NavDrawMap.class);
        startActivity(i);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static String printHashKey(Context ctx) {
        // Add code to print out the key hash
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "SHA-1 generation: the key count not be generated: NameNotFoundException thrown";
        } catch (NoSuchAlgorithmException e) {
            return "SHA-1 generation: the key count not be generated: NoSuchAlgorithmException thrown";
        }

        return "SHA-1 generation: epic failed";
    }

    public class Post_handler extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("https://travelcoins.herokuapp.com/api/users");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setRequestProperty("id_facebook", AccessToken.getCurrentAccessToken().getUserId());
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes("https://travelcoins.herokuapp.com/api/users");
                wr.flush();
                wr.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    public class BdHandler  extends AsyncTask< List<String>, Void, List<String> > {

        private final String LOG_TAG = BdHandler.class.getSimpleName();

        public List<String> getDataFromJson(String importJsonStr)
                throws JSONException {

            // Estas serán las constantes que queremos coger del json que nos da nuestra API:
            final String API_ID_FB = "id_facebook";
            final String API_MONEDAS_RETADAS = "monedas_retadas";

            JSONArray jsonArray = new JSONArray(importJsonStr);

            List<String> result = new ArrayList<String>();
            int i;
            for (i = 0; i < jsonArray.length(); ++i) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String identificador = jsonObject.getString(API_ID_FB);

                result.add(identificador);
            }

            return result;

        }


        @Override
        protected List<String> doInBackground(List<String>... params) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String importJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String BASE_URL = "https://travelcoins.herokuapp.com/api/users";

                URL url = new URL(BASE_URL);
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                importJsonStr = buffer.toString();
            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                Log.v(LOG_TAG, "El JSON es: " + importJsonStr);
                return getDataFromJson(importJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            ArrayList<String> lista = new ArrayList<String>();
            if (result != null) {
                int i = 0;
                while(i < result.size()) {
                    lista.add(result.get(i));
                    ++i;
                }
                // Llegados a este punto ya tenemos todos los datos actualizados.
            }
            if (lista != null) {
                TinyDB tinydb = new TinyDB(getApplicationContext());
                tinydb.putListString(IDS, lista);
            }
        }

    }

}
