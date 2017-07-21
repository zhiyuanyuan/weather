package com.zhiyuan.weather.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AqiEntity implements Serializable {

    @SerializedName("city")
    public CityEntity city;
}