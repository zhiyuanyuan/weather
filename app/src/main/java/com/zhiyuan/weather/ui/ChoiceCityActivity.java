package com.zhiyuan.weather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.zhiyuan.weather.R;
import com.zhiyuan.weather.adapter.CityAdapter;
import com.zhiyuan.weather.base.ToolbarActivity;
import com.zhiyuan.weather.bean.ChangeCityEvent;
import com.zhiyuan.weather.bean.City;
import com.zhiyuan.weather.bean.CityORM;
import com.zhiyuan.weather.bean.Irrelevant;
import com.zhiyuan.weather.bean.MultiUpdateEvent;
import com.zhiyuan.weather.bean.Province;
import com.zhiyuan.weather.db.DBManager;
import com.zhiyuan.weather.db.WeatherDB;
import com.zhiyuan.weather.util.C;
import com.zhiyuan.weather.util.OrmLite;
import com.zhiyuan.weather.util.RxBus;
import com.zhiyuan.weather.util.RxUtil;
import com.zhiyuan.weather.util.SharedPreferenceUtil;
import com.zhiyuan.weather.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;

/**
 * Created by admin on 2017/7/25.
 */

public class ChoiceCityActivity extends ToolbarActivity {
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ArrayList<String> dataList = new ArrayList<>();
    private CityAdapter mAdapter;
    private int currentLevel;
    private Province selectedProvince;
    private List<Province> provincesList = new ArrayList<>();
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private List<City> cityList;
    private boolean isChecked = false;
    @Override
    protected int provideContentViewId() {
        return    R.layout.activity_choice_city;
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        Observable.create(emitter -> {
            DBManager.getInstance().openDatabase();
            emitter.onNext(Irrelevant.INSTANCE);
            emitter.onComplete();
        })
                .compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .doOnNext(o -> {
                    initRecyclerView();
                    queryProvinces();
                })
                .subscribe();

        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra(C.MULTI_CHECK, false);
        if (isChecked && SharedPreferenceUtil.getInstance().getBoolean("Tips", true)) {
            showTips();
        }
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, pos) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provincesList.get(pos);
                mRecyclerView.smoothScrollToPosition(0);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                String city = Util.replaceCity(cityList.get(pos).mCityName);
                if (isChecked) {
                    OrmLite.getInstance().save(new CityORM(city));
                    RxBus.getDefault().post(new MultiUpdateEvent());
                } else {
                    SharedPreferenceUtil.getInstance().setCityName(city);
                    RxBus.getDefault().post(new ChangeCityEvent());
                }
                quit();
            }
        });
    }
    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        getToolbar().setTitle("选择城市");
        dataList.clear();
        mAdapter.notifyDataSetChanged();

        Flowable.create((FlowableOnSubscribe<String>) emitter -> {
            cityList = WeatherDB.loadCities(DBManager.getInstance().getDatabase(), selectedProvince.mProSort);
            for (City city : cityList) {
                emitter.onNext(city.mCityName);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .compose(RxUtil.ioF())
                .compose(RxUtil.activityLifecycleF(this))
                .doOnNext(proName -> dataList.add(proName))
                .doOnComplete(() -> {
                    currentLevel = LEVEL_CITY;
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(0);
                })
                .subscribe();
    }
    /**
     * 查询全国所有的省，从数据库查询
     */
    private void queryProvinces() {
        getToolbar().setTitle("选择省份");
        Flowable.create((FlowableOnSubscribe<String>) emitter -> {
            if (provincesList.isEmpty()) {
                provincesList.addAll(WeatherDB.loadProvinces(DBManager.getInstance().getDatabase()));
            }
            dataList.clear();
            for (Province province : provincesList) {
                emitter.onNext(province.mProName);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .compose(RxUtil.ioF())
                .compose(RxUtil.activityLifecycleF(this))
                .doOnNext(proName -> dataList.add(proName))
                .doOnComplete(() -> {
                    mProgressBar.setVisibility(View.GONE);
                    currentLevel = LEVEL_PROVINCE;
                    mAdapter.notifyDataSetChanged();
                })
                .subscribe();
    }

    private void showTips() {
        new AlertDialog.Builder(this)
                .setTitle("多城市管理模式")
                .setMessage("您现在是多城市管理模式,直接点击即可新增城市.如果暂时不需要添加,"
                        + "在右上选项中关闭即可像往常一样操作.\n因为 api 次数限制的影响,多城市列表最多三个城市.(๑′ᴗ‵๑)")
                .setPositiveButton("明白", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("不再提示", (dialog, which) -> SharedPreferenceUtil.getInstance().putBoolean("Tips", false))
                .show();
    }

    private void quit() {
        ChoiceCityActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.multi_check) {
            if (isChecked) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            isChecked = item.isChecked();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_PROVINCE) {
            quit();
        } else {
            queryProvinces();
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ChoiceCityActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getInstance().closeDatabase();
    }

}