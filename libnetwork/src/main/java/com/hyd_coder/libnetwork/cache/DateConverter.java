package com.hyd_coder.libnetwork.cache;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/9 17:07
 * description : 时间转换器
 */
public class DateConverter {

    @TypeConverter
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static Date longToDate(long time) {
        return new Date(time);
    }
}
