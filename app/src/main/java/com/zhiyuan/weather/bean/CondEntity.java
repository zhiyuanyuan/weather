package com.zhiyuan.weather.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CondEntity implements Serializable {
    @SerializedName("code")
    public String code;
    @SerializedName("txt")
    public String txt;
    @SerializedName("txt_d")
    public String txtDay;
}