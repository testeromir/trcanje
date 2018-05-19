package com.example.trcanje.tracks;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Track implements Parcelable {

    private final int id;
    private String name;
    private final List<Location> points;
    private long startTime;
    private long endTime;

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setName(String name) {

        this.name = name;
    }

    protected Track(Parcel in) {

        id = in.readInt();
        name = in.readString();
        points = in.createTypedArrayList(Location.CREATOR);
        startTime = in.readLong();
        endTime = in.readLong();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

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

    public void addLocations(List<Location> locations){
        getPoints().addAll(locations);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedList(points);
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
    }
}
