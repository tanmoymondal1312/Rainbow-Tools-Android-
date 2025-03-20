package com.mediaghor.rainbowtools.Activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mediaghor.rainbowtools.Adapter.ViewPagerCategoryAdapter;
import com.mediaghor.rainbowtools.R;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private TabLayout tab;
    private ViewPager viewPager;
    View toolbar;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MainActivity);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar
        toolbar = findViewById(R.id.layout_toolbar_included);

        // Initialize TabLayout and ViewPager
        tab = findViewById(R.id.tabLayoutId);
        viewPager = findViewById(R.id.viewPagerId);
        ViewPagerCategoryAdapter adapter = new ViewPagerCategoryAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);

        // Run cache cleanup asynchronously
        new DeleteCacheTask().execute(getCacheDir());
    }


    // AsyncTask for cache deletion (Runs in Background)
    private static class DeleteCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... files) {
            File cacheDir = files[0];
            if (cacheDir != null) {
                deleteCache(cacheDir);
            }
            return null;
        }

        private void deleteCache(File folder) {
            if (folder != null && folder.exists()) {
                for (File file : folder.listFiles()) {
                    if (file.isDirectory()) {
                        deleteCache(file);
                    }
                    file.delete();
                }
            }
        }
    }
}
