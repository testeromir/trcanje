package com.example.trcanje.database;

import android.content.Context;

import com.example.trcanje.tracks.Track;

import java.util.ArrayList;

public class DatabaseManager {

    public static void delete(int id, Context context) {
        TrcanjeDatabase.getInstance(context).trackDao().deleteTrackDB(id);
    }

    public static void update(Track track, Context context) {
        TrcanjeDatabase.getInstance(context).trackDao().updateTrackDB(track);
    }

    public static int getMaxID(Context context) {
        return TrcanjeDatabase.getInstance(context).trackDao().getMaxIDDB();
    }

    public static void insertTrack(Track track, Context context){
        TrcanjeDatabase.getInstance(context).trackDao().insertTrackDB(track);
    }

    public static ArrayList<Track> getTracks(Context context){
        return new ArrayList<Track> (TrcanjeDatabase.getInstance(context).trackDao().getTracksDB());
    }
}
