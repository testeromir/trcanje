package com.example.trcanje.tracks;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Track {

    private final int id;
    private final List<Location> points;
    public long startTime;
    public long endTime;

    public void setEndtime(long endtime) {
        this.endTime = endtime;
    }

    public Track(int id) {
        this.id = id;
        this.points = new ArrayList<>();
        startTime = System.currentTimeMillis();
    }

    public Track(int id, List<Location> points,long starttime) {
        this.id = id;
        this.points = points;
        this.startTime = starttime;
    }

    public int getId() {
        return id;
    }

    public List<Location> getPoints() {
        return points;
    }

    public float trackDistance(){
        float distance = 0;
        if(points.size() > 1) {
            for(int i = 1; i < points.size(); i++){
                distance += points.get(i).distanceTo(points.get(i-1));
            }
        }
        return distance;
    }

    public float currentTrackDistance(long currentTime){
        float distance = 0;
        if(points.size() > 1) {
            for (int i = 1; i < points.size(); i++) {
                if(points.get(i).getTime()< currentTime) {
                    distance += points.get(i).distanceTo(points.get(i - 1));
                }
            }
        }
        return distance;
    }
    public float overallSpeed(){
        return trackDistance() / (endTime - startTime) * 1000;
    }

    public float currentSpeed(long currentTime){
        return currentTrackDistance(currentTime) / (currentTime - startTime) * 1000;
    }

    public String timePassed(long currentTime){
        long timepassed = currentTime - startTime;
        long hours = timepassed / 3600000;
        long minutes = (timepassed % 3600000) / 60000;
        long seconds = (timepassed % 60000) / 1000;
        String hoursString = (hours < 10 ? "0"+String.valueOf(hours) : String.valueOf(hours));
        String minutesString = (minutes < 10 ? "0"+String.valueOf(minutes) : String.valueOf(minutes));
        String secondsString = (seconds < 10 ? "0"+String.valueOf(seconds) : String.valueOf(seconds));
        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    public String distance(long currentTime){
        float distance = currentTrackDistance(currentTime);

        String meters = String.valueOf((int)distance % 1000);
        int kmeters = (int)distance / 1000;
        if( kmeters != 0) {
            String kilometers = String.valueOf(kmeters);
            return kilometers + " km " + meters + " m";
        } else {
            return  meters + " m";
        }
    }

    public String speed(long currentTime) {
        float speed = currentSpeed(currentTime);
        String speedstring = String.valueOf((int)speed);
        return speedstring + " m/s";
    }
}
