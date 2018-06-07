package com.example.trcanje;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trcanje.database.DatabaseManager;
import com.example.trcanje.database.TrcanjeDatabase;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackActivity extends FragmentActivity implements OnMapReadyCallback {


    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates;
    private boolean newClicked;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Timer timer;
    private EditText nameEditText;
    private TextView timeTextView;
    private TextView distanceTextView;
    private TextView speedTextView;
    private Button buttonStart;
    private Button buttonPauseResume;
    private Button buttonStop;
    int id;
    private Track track;
    public int requestCode;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateGUI();
        }
    };

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        requestCode = intent.getIntExtra(ConstantsManager.REQUEST_CODE, 0);

        nameEditText = findViewById(R.id.edit_text_track_name);
        timeTextView = findViewById(R.id.TextView_time);
        distanceTextView = findViewById(R.id.TextView_distance);
        speedTextView = findViewById(R.id.TextView_speed);
        buttonStart = (Button) findViewById(R.id.button_start_update);
        buttonPauseResume = (Button) findViewById(R.id.button_pause_resume_update);
        buttonStop = (Button) findViewById(R.id.button_stop_update);

        if (requestCode == ConstantsManager.REQUEST_NEW_CODE) {
            id = intent.getIntExtra(ConstantsManager.TRACK_ID, 0);



            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            nameEditText.setText(String.valueOf(id) + ". track");

            buttonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonPauseResume.setEnabled(true);
                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);
                    track = new Track(id);

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message message = handler.obtainMessage();
//                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }, 1, 100);
                    newClicked = true;
                    startLocationUpdates();
                }
            });
            buttonStart.setEnabled(false);

            buttonPauseResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (buttonPauseResume.getText() == getString(R.string.pause_track)) {
                        stopLocationUpdates();
                        track.setTime(track.getTime() + System.currentTimeMillis() - track.getStartTime());
                        timer.cancel();
                        buttonStop.setEnabled(false);
                        buttonPauseResume.setText(getString(R.string.resume_track));
                    } else {

                            startLocationUpdates();

                        track.setStartTime(System.currentTimeMillis());
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Message message = handler.obtainMessage();

                                handler.sendMessage(message);

                            }
                        }, 1, 100);
                        buttonPauseResume.setText(getString(R.string.pause_track));
                        buttonStop.setEnabled(true);
                    }
                }
            });

            buttonPauseResume.setEnabled(false);


            buttonStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  buttonStart.setEnabled(true);
                    buttonPauseResume.setEnabled(false);
                    buttonStop.setEnabled(false);
                    timer.cancel();
                    track.setName(nameEditText.getText().toString());
                    track.setEndTime(System.currentTimeMillis());
                    //  track.setTime(track.getTime() + track.getEndTime() - track.getStartTime());
                    stopLocationUpdates();
                    Intent intent = new Intent();
                    intent.putExtra(ConstantsManager.TRACK, track);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            buttonStop.setEnabled(false);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);

                return;
            }
            //  trackManager = new TrackManager();
            initiLocation();
        } else {
            if (requestCode == ConstantsManager.REQUEST_VIEW_CODE) {
                buttonStart.setVisibility(View.GONE);
                buttonPauseResume.setText(R.string.delete);
                buttonStop.setText(R.string.back);
                track = intent.getParcelableExtra(ConstantsManager.TRACK);
                nameEditText.setText(track.getName());
                //  distanceTextView.setText(String.valueOf(track.getDistance()));
                //timeTextView.setText(String.valueOf(track.getTime()));
                //speedTextView.setText(String.valueOf(track.getDistance()/track.getTime()*1000));
                timeTextView.setText(track.timePassed(track.getEndTime()));
                distanceTextView.setText(track.distance(track.getEndTime()));
                speedTextView.setText(track.speed(track.getEndTime()));
                buttonPauseResume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete(track.getId());
                        finish();
                    }
                });

                buttonStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        track.setName(nameEditText.getText().toString());
                        update(track);
                        finish();
                    }
                });



            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if permission is granted
        initiLocation();
    }

    @SuppressLint({"MissingPermission", "RestrictedApi"})
    private void initiLocation() {
      /*  mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("INFO", "Uspeh");
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("INFO", "Fail");
            }
        }); */

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
                        resolvable.startResolutionForResult(TrackActivity.this,
                                ConstantsManager.REQUEST_CHECK_SETTINGS);
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
                ArrayList<Location> locations = (ArrayList<Location>) locationResult.getLocations();
              /*  for (Location location : locations) {
                    Log.i("INFO", String.valueOf(locations.size()));
                    // Update UI with location data
                    // ...
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double altitude = location.getAltitude();
                    Log.i("INFO", "Promena lokacije: " + latitude + " , " + longitude + " , " + altitude);
                } */if(locations != null) {
                    track.addLocations(locations);
                }

               drawMap();

               // mMap.addMarker(new MarkerOptions().position(list.get(list.size()-1)).title("Last location"));



            }

            ;
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestCode == ConstantsManager.REQUEST_NEW_CODE && newClicked)  {
                startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (requestCode == ConstantsManager.REQUEST_NEW_CODE) {
            stopLocationUpdates();
        }

    }

    private void stopLocationUpdates() {
        if(mRequestingLocationUpdates) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mRequestingLocationUpdates = false;
        }
        /* if (track != null) {
            List<Location> points = track.getPoints();
            Log.i("INFO", "Track id: " + track.getId() + " Locations: " + points.size());
            for (Location location : points) {
                location.getTime();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();
                Log.i("INFO", "\n" + latitude + " , " + longitude + " , " + altitude);
            }
        } */
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if(!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
        }
    }

    protected void updateGUI() {
        long currentTime = System.currentTimeMillis();
        timeTextView.setText(track.timePassed(currentTime));
        distanceTextView.setText(track.distance(currentTime));
        speedTextView.setText(track.speed(currentTime));
    }

    private void delete(int id) {
        DatabaseManager.delete(id, this);
    }

    private void update(Track track) {
        DatabaseManager.update(track, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(requestCode == ConstantsManager.REQUEST_NEW_CODE) {
            buttonStart.setEnabled(true);
        }
        if (requestCode == ConstantsManager.REQUEST_VIEW_CODE) {
            drawMap();
        }
    }

    public void drawMap(){
        ArrayList<LatLng> list = new ArrayList<>();
        for (Location location : track.getPoints()) {
            list.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        mMap.clear();
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(list));
        polyline1.setTag(track.getName());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(list.get(list.size()-1),ConstantsManager.ZOOM));
    }
}
/*
    Mapa
 */