package com.zhiyuan.weather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zhiyuan.weather.R;
import com.zhiyuan.weather.adapter.HomePagerAdapter;
import com.zhiyuan.weather.util.RxDrawer;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppbarLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.coord)
    CoordinatorLayout mCoord;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private MainFragment mMainFragment;
    private MultiCityFragment mMultiCityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initDrawer();
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        if (mNavView != null) {
            mNavView.setNavigationItemSelectedListener(this);
            mNavView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle =
                    new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                            R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }
    private void initView() {
        setSupportActionBar(mToolbar);
        mFab.setOnClickListener(v-> showShareDialog());
        HomePagerAdapter mAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mMainFragment = new MainFragment();
        mMultiCityFragment = new MultiCityFragment();
        mAdapter.addTab(mMainFragment, "主页面");
        mAdapter.addTab(mMultiCityFragment, "多城市");
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager, false);
    }


    private void showShareDialog() {
        // wait to do
    }
    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        RxDrawer.close(mDrawerLayout)
                .doOnNext(o -> {
                    switch (item.getItemId()) {
                        case R.id.nav_set:
                            SettingActivity.launch(MainActivity.this);
                            break;
                        case R.id.nav_about:
                            AboutActivity.launch(MainActivity.this);
                            break;
                        case R.id.nav_city:
                            ChoiceCityActivity.launch(MainActivity.this);
                            break;
                        case R.id.nav_multi_cities:
                            mViewPager.setCurrentItem(1);
                            break;
                    }
                })
                .subscribe();
        return false;
    }
}
