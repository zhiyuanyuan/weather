package com.zhiyuan.weather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zhiyuan.weather.R;
import com.zhiyuan.weather.base.ToolbarActivity;

/**
 * Created by admin on 2017/7/24.
 */

public class SettingActivity extends ToolbarActivity{
    @Override
    protected int provideContentViewId() {
        return    R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolbar().setTitle("设置");
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingFragment()).commit();
    }


    @Override
    protected void beforeSetContent() {
        super.beforeSetContent();
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }
}
