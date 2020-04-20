package com.francesco.voicephotos.activities;

import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.francesco.voicephotos.R;
import com.francesco.voicephotos.adapters.FullSizeAdapter;

import java.util.ArrayList;

public class FullScreenActivity extends Activity {

    ViewPager viewPager;
    ArrayList<String> images_paths;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        viewPager = (ViewPager) findViewById(R.id.full_screen_image);

        if (savedInstanceState==null){
            Intent intent = getIntent();
            images_paths = intent.getStringArrayListExtra("PHOTOS_PATHS");
            position = Integer.parseInt(intent.getStringExtra("PHOTO_POSITION"));
        }

        FullSizeAdapter fullsizeadapter = new FullSizeAdapter(this, images_paths);
        viewPager.setAdapter(fullsizeadapter);
        viewPager.setCurrentItem(position, true);



    }
}
