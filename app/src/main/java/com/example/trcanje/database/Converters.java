package com.example.trcanje.database;

import android.arch.persistence.room.TypeConverter;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Converters {
  static  Gson gson = new Gson();
    @TypeConverter
    public static List<Location> stringToLocationList(String data) {
            if(data == null){
                return  Collections.emptyList();
            }

            Type listType = new TypeToken<List<Location>>() {}.getType();

            return gson.fromJson(data,listType);
    }

    @TypeConverter
    public static String locationListToString(List<Location> locations){
        return gson.toJson(locations);
    }


}
