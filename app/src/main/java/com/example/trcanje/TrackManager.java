package com.example.trcanje;

import android.location.Location;

import com.example.trcanje.tracks.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackManager {

    private static int counter = 0;

    private final Map<Integer, Track> tracksById = new HashMap<>();

    public Map<Integer, Track> getTracksById() {
        return tracksById;
    }

    public Track createNewTrack() {

        counter++;

        Track track = new Track(counter);

        tracksById.put(counter, track);

        return track;
    }

    public Track createNewTrack(List<Location> points, long starttime) {
        counter++;

        Track track = new Track(counter, points, starttime);

        tracksById.put(counter, track);

        return track;
    }

    public Track getLastTrack(){
         return tracksById.get(counter);
    }

    public boolean addLocation(int trackId, Location location) {
        if (!tracksById.containsKey(trackId)) {
            throw new IllegalArgumentException("Track with ID " + trackId + " does not exist");
        }

        return tracksById.get(trackId).getPoints().add(location);
    }

    public void addLocations(int trackId, List<Location> locations) {
        if (!tracksById.containsKey(trackId)) {
            throw new IllegalArgumentException("Track with ID " + trackId + " does not exist");
        }

        tracksById.get(trackId).getPoints().addAll(locations);
    }
}
