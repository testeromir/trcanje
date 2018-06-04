package com.example.trcanje.tracks;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.trcanje.database.Converters;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "track_table")
public class Track implements Parcelable {

    @PrimaryKey
    private final int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "points")
    @TypeConverters(Converters.class)
    private List<Location> points;

    public void setPoints(List<Location> points) {
        this.points = points;
    }

    @ColumnInfo(name = "start_time")
    private long startTime;

    @ColumnInfo(name = "end_time")
    private long endTime;

    @ColumnInfo(name = "distance")
    private float distance;

    @ColumnInfo(name = "time")
    private long time;

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
        distance = in.readFloat();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedList(points);
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
        parcel.writeFloat(distance);
        parcel.writeLong(time);
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
        distance = 0;
        time = 0;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Track(int id, List<Location> points, long starttime) {
        this.id = id;
        this.points = points;
        this.startTime = starttime;
        distance = 0;
        time = 0;
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
        this.distance = distance;
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
        this.setDistance(distance);
        return distance;
    }
    public float overallSpeed(){
        return distance / time * 1000;
    }

    public float currentSpeed(long currentTime){
        return trackDistance() / (currentTime - startTime + time) * 1000;
    }

    public String timePassed(long currentTime){
        long timepassed = currentTime - startTime + time;
        long hours = timepassed / 3600000;
        long minutes = (timepassed % 3600000) / 60000;
        long seconds = (timepassed % 60000) / 1000;
        String hoursString = (hours < 10 ? "0"+String.valueOf(hours) : String.valueOf(hours));
        String minutesString = (minutes < 10 ? "0"+String.valueOf(minutes) : String.valueOf(minutes));
        String secondsString = (seconds < 10 ? "0"+String.valueOf(seconds) : String.valueOf(seconds));
        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    public String distance(long currentTime){
        float distance = trackDistance();

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


}
