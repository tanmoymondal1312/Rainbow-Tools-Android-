package com.mediaghor.rainbowtools.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mediaghor.rainbowtools.Fragments.FragmentImagesOptimazation;
import com.mediaghor.rainbowtools.Fragments.FragmentMostUses;
import com.mediaghor.rainbowtools.Fragments.FragmentPDFOptimization;
import com.mediaghor.rainbowtools.Fragments.FragmentPopular;

public class ViewPagerCategoryAdapter extends FragmentPagerAdapter {
    public ViewPagerCategoryAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return new FragmentPopular();
        }else if (position == 1){
            return new FragmentMostUses();
        }else if (position == 2){
            return new FragmentImagesOptimazation();
        }else{
            return new FragmentPDFOptimization();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){
            return "Popular Now";
        } else if (position == 1) {
            return "Most Usage";
        }else if (position == 2) {
            return "Image Optimization";
        }else {
            return "PDF Optimization";
        }
    }
}
