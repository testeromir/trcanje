package com.example.trcanje;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.trcanje.tracks.Track;

@Database(entities = Track.class, version = 1)
public abstract class TrcanjeDatabase extends RoomDatabase{

    private static TrcanjeDatabase INSTANCE;

    public abstract TrackDao trackDao();

    public static TrcanjeDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TrcanjeDatabase.class, "track-database.db").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
