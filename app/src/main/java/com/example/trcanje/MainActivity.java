package com.example.trcanje;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trcanje.tracks.Track;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    Timer timer;
    private TextView timeTextView;
    private TextView distanceTextView;
    private TextView speedTextView;
    private Button  buttonStart;
    private Button buttonStop;
    int id;
    private Track track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        intent.getIntExtra(getString(R.string.track_id),id);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {"android.permission.ACCESS_FINE_LOCATION"}, 1);

            return;
        }
      //  trackManager = new TrackManager();
        timeTextView = findViewById(R.id.TextView_time);
        distanceTextView = findViewById(R.id.TextView_distance);
        speedTextView = findViewById(R.id.TextView_speed);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

         buttonStart = (Button) findViewById(R.id.button_start_update);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                mRequestingLocationUpdates = true;
               track = new Track(id);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        timeTextView.setText(track.timePassed(System.currentTimeMillis()));
                        distanceTextView.setText(track.distance(System.currentTimeMillis()));
                        speedTextView.setText(track.speed(System.currentTimeMillis()));

                    }
                }, 1, 100);
                startLocationUpdates();
            }
        });

         buttonStop = (Button) findViewById(R.id.button_stop_update);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                timer.cancel();
                track.endTime = System.currentTimeMillis();
                stopLocationUpdates();
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.track),track);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        initiLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if permission is granted
        initiLocation();
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void initiLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("INFO", "Uspeh");
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("INFO", "Fail");
            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i("INFO", "Uspeh");
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("INFO", "Fail");
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                List<Location> locations = locationResult.getLocations();
                for (Location location : locations) {
                    Log.i("INFO", String.valueOf(locations.size()));
                    // Update UI with location data
                    // ...
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double altitude = location.getAltitude();
                    Log.i("INFO","Promena lokacije: " + latitude + " , " + longitude + " , "+ altitude);
                }
                track.addLocations(locations);
            };
        };

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        if (track != null) {
            List<Location> points = track.getPoints();
            Log.i("INFO", "Track id: " + track.getId() + " Locations: " + points.size());
            for (Location location : points) {
                location.getTime();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();
                Log.i("INFO", "\n" + latitude + " , " + longitude + " , " + altitude);
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }
}

/*
    Prethodna aktivnost koja ce da predstavlja listu staza, gde cemo imati opciju da klikom pogledamo prethodne staze ili da dodamo novu stazu.
    Morace ovo lepo da se osmisli. Start new track prilikom pritiska na dugme da se povuce odavde u tu prethodnu klasu.
    Posle toga ce ici mapa i slicno.

 */