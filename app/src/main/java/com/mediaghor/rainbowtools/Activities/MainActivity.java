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

import java.io.File;

public class MainActivity extends AppCompatActivity {
    TabLayout tab;
    ViewPager viewPager;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MainActivity);

        Toolbar toolbar;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.tool_bar_global);
        setSupportActionBar(toolbar);

        //Set The Tab Layout And Viewpager For Categories
        tab = findViewById(R.id.tabLayoutId);
        viewPager = findViewById(R.id.viewPagerId);
        ViewPagerCategoryAdapter adapter = new ViewPagerCategoryAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);



        try {
            File cacheDir = getCacheDir();
            long cacheSize = getFolderSize(cacheDir);

            if (cacheSize > 2000 * 1024) { // 200MB
                deleteCache(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Function to get the total cache size
    private long getFolderSize(File folder) {
        long size = 0;
        try {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    size += getFolderSize(file);
                } else {
                    size += file.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    // Function to clear cache
    private void deleteCache(File folder) {
        try {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    deleteCache(file);
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}