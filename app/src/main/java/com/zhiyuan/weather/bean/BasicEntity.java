package com.zhiyuan.weather.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BasicEntity implements Serializable {
    @SerializedName("city")
    public String city;
}