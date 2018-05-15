package com.example.trcanje;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class UpdateActivity extends AppCompatActivity {
    private Button buttonNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        buttonNew = (Button) findViewById(R.id.button_new_track);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTrack();
            }
        });

        TrackAdapter trackAdapter = new TrackAdapter(MainActivity.trackManager.getTracksById(),this);
        ListView listView = (ListView) findViewById(R.id.list_view_tracks);
        listView.setAdapter(trackAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    private void newTrack() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
