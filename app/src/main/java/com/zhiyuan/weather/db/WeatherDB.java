package com.zhiyuan.weather.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhiyuan.weather.bean.City;
import com.zhiyuan.weather.bean.Province;
import com.zhiyuan.weather.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo on 2015/9/30 0030.
 * 封装数据库操作
 */
public class WeatherDB {

    public WeatherDB() {

    }

    public static List<Province> loadProvinces(SQLiteDatabase db) {

        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query("T_Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.mProSort = cursor.getInt(cursor.getColumnIndex("ProSort"));
                province.mProName = cursor.getString(cursor.getColumnIndex("ProName"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        Util.closeQuietly(cursor);
        return list;
    }

    public static List<City> loadCities(SQLiteDatabase db, int ProID) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("T_City", null, "ProID = ?", new String[] { String.valueOf(ProID) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.mCityName = cursor.getString(cursor.getColumnIndex("CityName"));
                city.mProID = ProID;
                city.mCitySort = cursor.getInt(cursor.getColumnIndex("CitySort"));
                list.add(city);
            } while (cursor.moveToNext());
        }
        Util.closeQuietly(cursor);
        return list;
    }
}
