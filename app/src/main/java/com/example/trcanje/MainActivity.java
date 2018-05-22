package com.example.trcanje;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.trcanje.tracks.Track;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_NEW_CODE = 0;
    public static final int REQUEST_VIEW_CODE = 1;
    private Button buttonNew;
    private TrackManager trackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        trackManager = new TrackManager();


        buttonNew = (Button) findViewById(R.id.button_new_track);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTrack();
            }
        });

    }

    private void newTrack() {
        Intent intent = new Intent(this,TrackActivity.class);
        trackManager.createNewTrack();
        intent.putExtra(getString(R.string.request_code),REQUEST_NEW_CODE);
        intent.putExtra(getString(R.string.track_id),trackManager.getCounter()-1);
        startActivityForResult(intent,REQUEST_NEW_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_NEW_CODE){
            if(resultCode == RESULT_OK){
                Track track = data.getParcelableExtra(getString(R.string.track));
                trackManager.setTrack(track.getId(),track);
                Log.i("INFO2", String.valueOf(track.getId()));

                TrackAdapter trackAdapter = new TrackAdapter(trackManager.getTracksById(),this);
                ListView listView = (ListView) findViewById(R.id.list_view_tracks);
                listView.setAdapter(trackAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        viewTrack((Track)adapterView.getAdapter().getItem(i));
                    }
                });
                listView.invalidate();
            }
        } else {

        }

    }

    void viewTrack(Track t){
        Intent intent = new Intent(this,TrackActivity.class);
        intent.putExtra(getString(R.string.request_code),REQUEST_VIEW_CODE);
        intent.putExtra(getString(R.string.track),t);
        startActivityForResult(intent,REQUEST_VIEW_CODE);
    }
}
