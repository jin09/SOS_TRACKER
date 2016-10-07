package com.app.gautam.securesamaj;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static String TAG = "main activity";
    TextView mLatitudeText;
    TextView mLongitudeText;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Button btn3,btn4;
    private Toast mtoast;
    public String address = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate........");
        mLatitudeText = (TextView) findViewById(R.id.fieldLatitude);
        mLongitudeText = (TextView) findViewById(R.id.fieldLongitude);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        //connect to the google play API
        buildGoogleApiClient();


        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnectingToInternet()){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("You don't have an internet connection.\n" +
                            "Please connect to the Internet before moving further.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //work to be done here
                            dialog.dismiss();
                            mtoast.makeText(getApplicationContext(),"CONNECT TO AN INTERNET SOURCE !!",Toast.LENGTH_LONG).show();
                            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else {
                    if (mLastLocation == null) {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            //to do
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("We were unable to find your location. But we can still inform your contacts.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //work to be done here
                                    dialog.dismiss();
                                    DateFormat dateFormatter = new SimpleDateFormat("hh : mm" + "*" + "\n" + "*" + "dd/MM/yyyy");
                                    dateFormatter.setLenient(false);
                                    Date today = new Date();
                                    String s = dateFormatter.format(today);
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    Log.d(TAG, String.valueOf(address));
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "I am in *DANGER*....\n\n" +
                                            "\uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98\n" +
                                            "/ ___|    /  _ \\   / ___| \n" +
                                            "\\___ \\   | |   | |   \\___ \\ \n" +
                                            " ___) |   | |   | |    ___) |\n" +
                                            "|____/   \\___/  |____/ \n" +
                                            "\uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98" +
                                            "\n\n" +
                                            "Time : \n" + "*" + s + "*" + "\n\n" +
                                            "_" + "This is an auto generated message.Download the android app now" + "_" + "\n" +
                                            "http://ska-developers.appspot.com/SOS-android-app-beta/");
                                    sendIntent.setType("text/plain");
                                    sendIntent.setPackage("com.whatsapp");
                                    startActivity(sendIntent);
                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        } else {
                            turnOnLocationSetting(MainActivity.this, mGoogleApiClient);
                        }
                    }
                    else{
                    String resultFromTask = null;
                    getAddressFromJSON task = new getAddressFromJSON();
                    try {
                        resultFromTask = task.execute("").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    //Log.d("whtsapp click",resultFromTask);
                    DateFormat dateFormatter = new SimpleDateFormat("hh : mm" + "*" + "\n" + "*" + "dd/MM/yyyy");
                    dateFormatter.setLenient(false);
                    Date today = new Date();
                    String s = dateFormatter.format(today);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    Log.d(TAG, String.valueOf(address));
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "I am in *DANGER*....\n\n" +
                            "\uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98\n" +
                            "/ ___|    /  _ \\   / ___| \n" +
                            "\\___ \\   | |   | |   \\___ \\ \n" +
                            " ___) |   | |   | |    ___) |\n" +
                            "|____/   \\___/  |____/ \n" +
                            "\uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98 \uD83C\uDD98" +
                            "\n\n" +
                            "Time : \n" + "*" + s + "*" + "\n\n" +
                            "My exact location is : \n http://maps.google.com/maps?q=loc:"
                            + String.valueOf(mLastLocation.getLatitude()) + "," +
                            String.valueOf(mLastLocation.getLongitude()) +
                            "\n\nand my estimated address is : \n\n" + "*" + resultFromTask + "*" + "\n\n" +
                            "_" + "This is an auto generated message.Download the android app now" + "_" + "\n" +
                            "http://ska-developers.appspot.com/SOS-android-app-beta/");
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                }
            }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnectingToInternet()){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("You don't have an internet connection.\n" +
                            "Please connect to the Internet before moving further.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //work to be done here
                            dialog.dismiss();
                            mtoast.makeText(getApplicationContext(),"CONNECT TO AN INTERNET SOURCE !!",Toast.LENGTH_LONG).show();
                            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else {
                    if (mLastLocation == null) {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            //to do
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("We were unable to find your location. But we can still inform your contacts.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //work to be done here
                                    dialog.dismiss();
                                    DateFormat dateFormatter = new SimpleDateFormat("hh : mm" + "*" + "\n" + "*" + "dd/MM/yyyy");
                                    dateFormatter.setLenient(false);
                                    Date today = new Date();
                                    String s = dateFormatter.format(today);
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    Log.d(TAG, String.valueOf(address));
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "I am now *SAFE*....\n\n" +

                                            "✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅\n" +
                                            "   _____     ___        ______    ______\n" +
                                            "  / ___/      /   |      / ____/   / ____/\n" +
                                            "  \\__ \\      / /| |     / /_       / __/   \n" +
                                            " ___/ /   / ___ |    / __/     / /___   \n" +
                                            "/____/   /_/  |_|   /_/       /_____/   \n" +
                                            "✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅" +
                                            "\n\n" +
                                            "Time : \n" + "*" + s + "*" + "\n\n" +
                                            "_" + "This is an auto generated message.Download the android app now" + "_" + "\n" +
                                            "http://ska-developers.appspot.com/SOS-android-app-beta/");
                                    sendIntent.setType("text/plain");
                                    sendIntent.setPackage("com.whatsapp");
                                    startActivity(sendIntent);
                                }
                            });
                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }else{
                            turnOnLocationSetting(MainActivity.this,mGoogleApiClient);
                        }
                    }
                    else{
                    String resultFromTask = null;
                    getAddressFromJSON task = new getAddressFromJSON();
                    try {
                        resultFromTask = task.execute("").get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    //Log.d("whtsapp click",resultFromTask);
                    DateFormat dateFormatter = new SimpleDateFormat("hh : mm" + "*" + "\n" + "*" + "dd/MM/yyyy");
                    dateFormatter.setLenient(false);
                    Date today = new Date();
                    String s = dateFormatter.format(today);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    Log.d(TAG, String.valueOf(address));
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "I am now *SAFE*....\n\n" +

                            "✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅\n" +
                            "   _____     ___        ______    ______\n" +
                            "  / ___/      /   |      / ____/   / ____/\n" +
                            "  \\__ \\      / /| |     / /_       / __/   \n" +
                            " ___/ /   / ___ |    / __/     / /___   \n" +
                            "/____/   /_/  |_|   /_/       /_____/   \n" +
                            "✅ ✅ ✅ ✅ ✅ ✅ ✅ ✅" +
                            "\n\n" +
                            "Time : \n" + "*" + s + "*" + "\n\n" +
                            "My exact location is : \n http://maps.google.com/maps?q=loc:"
                            + String.valueOf(mLastLocation.getLatitude()) + "," +
                            String.valueOf(mLastLocation.getLongitude()) +
                            "\n\nand my estimated address is : \n\n" + "*" + resultFromTask + "*" + "\n\n" +
                            "_" + "This is an auto generated message.Download the android app now" + "_" + "\n" +
                            "http://ska-developers.appspot.com/SOS-android-app-beta/");
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                }
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()........");
        //Check the internet connection
        if (!isConnectingToInternet()) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("You don't have an internet connection.\n" +
                    "Please connect to the Internet before moving further.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //work to be done here
                    dialog.dismiss();
                    mtoast.makeText(getApplicationContext(),"CONNECT TO AN INTERNET SOURCE !!",Toast.LENGTH_LONG).show();
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        else{
            mtoast.makeText(getApplicationContext(),"CONNECTED",Toast.LENGTH_SHORT).show();
        }
        //connecting to google play services API
        client.connect();
        mGoogleApiClient.connect();
    }

    public void getAddressFromGeoLocation(String addr){
        address = addr;
        Log.d("onpostexecute",address.toString());
    }

    class getAddressFromJSON extends AsyncTask<String,String,String>{
        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            MainActivity.this.getAddressFromGeoLocation(s);
            // To dismiss the dialog
            progress.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            try {
                URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(mLastLocation.getLatitude()) +"," +
                        String.valueOf(mLastLocation.getLongitude())+"&sensor=true");
                Log.d("link",url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.d("link","url is hit");
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.d("link","I got nothing from the url that I hit..");
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
                forecastJsonStr = buffer.toString();
                Log.d("link",forecastJsonStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.d("Error closing stream", e.getMessage());
                    }
                }
            }
            try {
                return getAddressFromJSON(forecastJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return forecastJsonStr;
        }

        private String getAddressFromJSON(String forecastJsonStr) throws JSONException {
            Log.d("link","trying to parse the JSON...");
            JSONObject rootJson = new JSONObject(forecastJsonStr);
            JSONArray resultsArray = rootJson.getJSONArray("results");
            JSONObject firstObject = resultsArray.getJSONObject(0);
            return firstObject.getString("formatted_address");
        }
    }
    public boolean isConnectingToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        mGoogleApiClient.connect();
        Log.d(TAG, "onstart.........");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.gautam.securesamaj/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.app.gautam.securesamaj/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Log.d(TAG, "onstop..............");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected........");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET
                }, 10);
            } else {
                getLocation();
            }
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    finish();
                    System.exit(0);
                }
                return;
        }
    }

    public void turnOnLocationSetting(final Activity activity, GoogleApiClient mGoogleApiClient) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                        break;
                }
            }
        });

    }

    private void getLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));

            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Log.d(TAG, "latt=" + String.valueOf(mLastLocation.getLatitude()) + "  " + "longi=" + String.valueOf(mLastLocation.getLongitude()));
        } else {
            mLatitudeText.setText("0");
            mLongitudeText.setText("0");
            Log.d(TAG, "location was NULL");
            turnOnLocationSetting(this, mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection to google play suspended!!! ..");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "connection to google play failed ..");
    }
}