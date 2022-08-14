package com.francesco.voicephotos.activities;

import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.francesco.voicephotos.R;
import com.francesco.voicephotos.adapters.FullSizeAdapter;
import com.francesco.voicephotos.db.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;

public class FullScreenActivity extends Activity {

    ViewPager viewPager;
    ArrayList<String> images_paths;
    Integer position;
    private static String TAG ="FullScreenActivity: ";
    LinearLayout button_delete, button_save;
    TextView photo_date;
    EditText photo_description;
    DatabaseHelper mDatabaseHelper;
    FullSizeAdapter fullsizeadapter;
    Context context;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FullScreenActivity.this, Gallery.class);
        startActivity(intent);
        Animatoo.animateSlideRight(context);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        context=this;

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.full_screen_image);

        if (savedInstanceState==null){
            Intent intent = getIntent();
            images_paths = intent.getStringArrayListExtra("PHOTOS_PATHS");
            Log.d(TAG, "onCreate: -"+intent.getIntExtra("PHOTO_POSITION", 0));

            position = intent.getIntExtra("PHOTO_POSITION", 0);
            Log.d(TAG, "onCreate: int position: "+position);
        }

        fullsizeadapter = new FullSizeAdapter(this, images_paths);
        viewPager.setAdapter(fullsizeadapter);

        viewPager.setCurrentItem(position, true);

        deletePhoto();



    }

    private void deletePhoto() {
        button_delete = findViewById(R.id.button_delete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //remove from SQL
                String photo_path = images_paths.get(position);
                mDatabaseHelper.removePhotoFromSqlite(photo_path);

                //remove actual file
                File fdelete = new File(photo_path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + photo_path);
                    } else {
                        System.out.println("file not Deleted :" + photo_path);
                    }
                }


                Toast.makeText(FullScreenActivity.this, "Photo Removed", Toast.LENGTH_SHORT).show();

                images_paths.remove(photo_path);
                fullsizeadapter.notifyDataSetChanged();
                viewPager.setAdapter(fullsizeadapter);
                viewPager.setCurrentItem(position-1, true);


                /*Intent intent = new Intent(FullScreenActivity.this, FullScreenActivity.class);
                startActivity(intent);*/
            }
        });

    }
}

