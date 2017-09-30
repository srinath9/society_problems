package com.example.tech.society.models;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by tech on 9/30/17.
 */

class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
