package com.francesco.voicephotos.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.voicephotos.db.DatabaseHelper;
import com.francesco.voicephotos.R;
import com.francesco.voicephotos.models.Photo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SavePhoto extends AppCompatActivity {
    static private String TAG = "SavePhoto class: ";

    private static final int REQUEST_CODE_SPEACH_INPUT = 1000;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    ImageView preview_immagine;
    Button button_cancel, button_save;
    ImageButton button_microphone;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String photo_path, photo_name, photo_orientation;
    EditText editText_description;
    TextView textview_location;
    DatabaseHelper mDatabaseHelper;
    private FusedLocationProviderClient client;
    private LocationListener listener;
    private Integer orientation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo);

        hideSoftKeyboard();

        //dichiarazione
        preview_immagine = (ImageView) findViewById(R.id.preview_immagine);
        button_save = (Button) findViewById(R.id.salva_immagine);
        button_cancel = (Button) findViewById(R.id.cancel_photo);
        button_microphone = (ImageButton) findViewById(R.id.button_microphone);
        editText_description = (EditText) findViewById(R.id.editText_description);
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());


        orientation=0;


        final Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        Log.d(TAG, "onCreate: extras : " + extras);


        int photo_mod = Integer.parseInt(extras.getString("PHOTO_MOD"));
        if (photo_mod ==0){
            photo_path = extras.getString("PHOTO_PATH");
            photo_name = extras.getString("PHOTO_NAME");
            photo_orientation = extras.getString("PHOTO_ORIENTATION");
            orientation = Integer.parseInt(photo_orientation);
            File imgFile = new File(photo_path);
            Log.d(TAG, "onCreate: PHOTO PATH: " + photo_path);
            Glide.with(getApplicationContext()).load(imgFile.getAbsolutePath()).apply(new RequestOptions().centerInside()).into(preview_immagine);
            preview_immagine.setRotation(orientation);
        }else{

        }










        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // quano si clicca salva deve prendere i campi e aggiungerli in sqlite , path photo, descrzione, geotag

                Photo photo = new Photo(photo_name, photo_path, editText_description.getText().toString(), orientation.toString());
                mDatabaseHelper.addPhotoObject(photo);

                Toast.makeText(getApplicationContext(), "Photo Saved", Toast.LENGTH_SHORT).show();
                finish();

                Intent intent = new Intent(SavePhoto.this, MainActivity.class);
                startActivity(intent);


            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fdelete = new File(photo_path);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + photo_path);
                        Intent intent = new Intent(SavePhoto.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        System.out.println("file not Deleted :" + photo_path);
                    }
                }
            }
        });


        button_microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Voice2Text();

            }





        });


    }

    public void Voice2Text (){

        Intent intent_voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent_voice.putExtra(RecognizerIntent.EXTRA_PROMPT, "Location of the photo");


        try {
            startActivityForResult(intent_voice, REQUEST_CODE_SPEACH_INPUT);
        }catch (Exception e){
            Log.d(TAG, "onClick: "+ e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEACH_INPUT:{
                if (resultCode == RESULT_OK && data != null ) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String description = editText_description.getText().toString();

                    if (description.equals("") || description.equals(" ") || description.isEmpty() ){
                        editText_description.setText(result.get(0));
                    }else{
                        textview_location.setText(result.get(0));
                    }

                }
            }
        }
    }


    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


}


