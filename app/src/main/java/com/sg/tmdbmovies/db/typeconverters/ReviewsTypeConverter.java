package com.sg.tmdbmovies.db.typeconverters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sg.tmdbmovies.model.Review;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ReviewsTypeConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static ArrayList<Review> gettingListFromString(String data) {
        if (data == null) {
            return new ArrayList<Review>();
        }

        Type listType = new TypeToken<ArrayList<Review>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String gettingStringFromList(ArrayList<Review> someObjects) {
        return gson.toJson(someObjects);
    }

}
