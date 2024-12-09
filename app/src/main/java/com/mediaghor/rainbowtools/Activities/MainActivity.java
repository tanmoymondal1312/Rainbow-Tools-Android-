package com.mediaghor.rainbowtools.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mediaghor.rainbowtools.Adapter.ViewPagerCategoryAdapter;
import com.mediaghor.rainbowtools.R;

public class MainActivity extends AppCompatActivity {
    TabLayout tab;
    ViewPager viewPager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set The Tab Layout And Viewpager For Categories
        tab = findViewById(R.id.tabLayoutId);
        viewPager = findViewById(R.id.viewPagerId);
        ViewPagerCategoryAdapter adapter = new ViewPagerCategoryAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);
    }
}