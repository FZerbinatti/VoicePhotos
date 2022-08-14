package com.francesco.voicephotos.activities;

import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    String string_photo_date;
    String string_photo_description;

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

        photo_date = findViewById(R.id.photo_date);
        photo_description = findViewById(R.id.photo_description);

        context=this;

        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.full_screen_image);

        if (savedInstanceState==null){
            Intent intent = getIntent();
            images_paths = intent.getStringArrayListExtra("PHOTOS_PATHS");
            position = intent.getIntExtra("PHOTO_POSITION", 0);

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


                new AlertDialog.Builder(context)
                        .setTitle("Elimina Foto")
                        .setMessage("Sei sicuro di voler eliminare questa foto, o ti è scivolato il dito, babbo?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
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


                                Toast.makeText(FullScreenActivity.this, "Foto Eliminata!", Toast.LENGTH_SHORT).show();

                                images_paths.remove(photo_path);
                                fullsizeadapter.notifyDataSetChanged();
                                viewPager.setAdapter(fullsizeadapter);
                                viewPager.setCurrentItem(position-1, true);



                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_delete)
                        .show();




            }
        });

    }
}

