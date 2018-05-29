package com.example.trcanje;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.trcanje.tracks.Track;

import java.util.List;

@Dao
public interface TrackDao {
    @Insert
    public void insertTrackDB(Track... trackEntries);

    @Update
    public void updateTrackDB(Track... trackEntries);

    @Query("SELECT * FROM track_table")
    public List<Track> getTracksDB();

    @Query("SELECT max(id) FROM track_table")
    public int getMaxIDDB();

    @Query("DELETE FROM track_table WHERE id = :currID")
    public void deleteTrackDB(int currID);
}
