package com.example.trcanje;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.trcanje.database.DatabaseManager;
import com.example.trcanje.database.TrcanjeDatabase;
import com.example.trcanje.tracks.Track;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Button buttonNew;
   // private TrackManager trackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TrackAdapter trackAdapter = new TrackAdapter(getTracks(),this);
        ListView listView = (ListView) findViewById(R.id.list_view_tracks);
        listView.setAdapter(trackAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewTrack((Track)adapterView.getAdapter().getItem(i));
            }
        });
        listView.invalidate();


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
       // trackManager.createNewTrack();
        int id;
        if(getTracks().size() == 0 ) {
            id = 1;
         } else {
            id = getMaxID() + 1;
        }

        intent.putExtra(ConstantsManager.REQUEST_CODE,ConstantsManager.REQUEST_NEW_CODE);
        intent.putExtra(ConstantsManager.TRACK_ID, id);
        startActivityForResult(intent,ConstantsManager.REQUEST_NEW_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ConstantsManager.REQUEST_NEW_CODE){
            if(resultCode == RESULT_OK){
                Track track = data.getParcelableExtra(ConstantsManager.TRACK);
                insertTrack(track);
             //   trackManager.setTrack(track.getId(),track);
                Log.i("INFO2", String.valueOf(track.getId()));

               // TrackAdapter trackAdapter = new TrackAdapter(trackManager.getTracksById(),this);
                 TrackAdapter trackAdapter = new TrackAdapter(getTracks(),this);
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
            TrackAdapter trackAdapter = new TrackAdapter(getTracks(),this);
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

    }

    private int getMaxID() {
        return DatabaseManager.getMaxID(this);
    }

    private void insertTrack(Track track){
        DatabaseManager.insertTrack(track,this);
    }

    private ArrayList<Track> getTracks(){
        return DatabaseManager.getTracks(this);
    }

    void viewTrack(Track t){
        Intent intent = new Intent(this,TrackActivity.class);
        intent.putExtra(ConstantsManager.REQUEST_CODE,ConstantsManager.REQUEST_VIEW_CODE);
        intent.putExtra(ConstantsManager.TRACK,t);
        startActivityForResult(intent,ConstantsManager.REQUEST_VIEW_CODE);
    }
}
