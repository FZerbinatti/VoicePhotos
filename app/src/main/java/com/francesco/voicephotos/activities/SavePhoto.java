package com.francesco.voicephotos.activities;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.voicephotos.adapters.GridViewAdapter;
import com.francesco.voicephotos.adapters.RecyclerViewAdapter;
import com.francesco.voicephotos.db.DatabaseHelper;
import com.francesco.voicephotos.R;
import com.francesco.voicephotos.models.Photo;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

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
    String photo_path, photo_name, photo_orientation, photo_path_1 ,photo_path_2, photo_path_3, photo_path_4;
    EditText editText_description;
    TextView textview_location;
    DatabaseHelper mDatabaseHelper;
    private FusedLocationProviderClient client;
    private LocationListener listener;
    private Integer orientation;
    LinearLayout four_photo;
    int photo_numeber;
    RecyclerView recyclerViewSavePhotos;
    ArrayList<Photo> list_of_photos;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo);

        hideSoftKeyboard();

        //dichiarazione
        button_save = (Button) findViewById(R.id.salva_immagine);
        button_cancel = (Button) findViewById(R.id.cancel_photo);
        button_microphone = (ImageButton) findViewById(R.id.button_microphone);
        editText_description = (EditText) findViewById(R.id.editText_description);
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        four_photo = findViewById(R.id.ll_four_photo);
        preview_immagine = (ImageView) findViewById(R.id.preview_immagine);


        orientation = 0;
        Intent intent = getIntent();
        final ArrayList<Photo> list_of_photos = (ArrayList<Photo>)intent.getSerializableExtra("LIST_OF_PHOTOS");
        Log.d(TAG, "onCreate: size arrived: "+list_of_photos.size());
        Bundle extras = getIntent().getExtras();
        int list_size =list_of_photos.size();
        Log.d(TAG, "onCreate: RECIEVED LIST_SIZE: " + list_size );



        if (list_size == 1){
            preview_immagine.setVisibility(View.VISIBLE);
            four_photo.setVisibility(View.GONE);

            photo_path = list_of_photos.get(0).getPhoto_path();
            photo_name = list_of_photos.get(0).getPhoto_name();
            photo_orientation = list_of_photos.get(0).getOrientation();
            orientation = Integer.parseInt(photo_orientation);
            File imgFile = new File(photo_path);
            Log.d(TAG, "onCreate: PHOTO PATH: " + photo_path);
            Glide.with(getApplicationContext()).load(imgFile.getAbsolutePath()).apply(new RequestOptions().centerInside()).into(preview_immagine);
            preview_immagine.setRotation(orientation);

            //salvataggio foto singola
            button_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "onClick: salvataggio foto singola");
                    // quano si clicca salva deve prendere i campi e aggiungerli in sqlite , path photo, descrzione, geotag

                    Photo photo = new Photo(photo_name, photo_path, editText_description.getText().toString(), orientation.toString());
                    mDatabaseHelper.addPhotoObject(photo);

                    Toast.makeText(getApplicationContext(), "Photo Saved", Toast.LENGTH_SHORT).show();
                    finish();

                    Intent intent = new Intent(SavePhoto.this, MainActivity.class);
                    startActivity(intent);


                }
            });
        }else{

            preview_immagine.setVisibility(View.GONE);
            four_photo.setVisibility(View.VISIBLE);
            recyclerViewSavePhotos = findViewById(R.id.savephotos_recyclerview);



            final GridLayoutManager mLayoutManager;
            mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
            recyclerViewSavePhotos.setLayoutManager(mLayoutManager);

            GridViewAdapter adapter = new GridViewAdapter( this, list_of_photos );
            recyclerViewSavePhotos.setLayoutManager(new GridLayoutManager(this,2));
            recyclerViewSavePhotos.setAdapter(adapter);

            //salvataggio foto multiple
            button_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // quano si clicca salva deve prendere i campi e aggiungerli in sqlite , path photo, descrzione, geotag


                    for (int j=0; j<list_of_photos.size(); j++){

                        photo_path = list_of_photos.get(j).getPhoto_path();
                        photo_name = list_of_photos.get(j).getPhoto_name();
                        photo_orientation = list_of_photos.get(j).getOrientation();
                        orientation = Integer.parseInt(photo_orientation);


                        Photo photo = new Photo(photo_name, photo_path, editText_description.getText().toString(), orientation.toString());
                        mDatabaseHelper.addPhotoObject(photo);
                    }



                    Toast.makeText(getApplicationContext(), "Photo Saved", Toast.LENGTH_SHORT).show();
                    finish();

                    Intent intent = new Intent(SavePhoto.this, MainActivity.class);
                    startActivity(intent);


                }
            });

        }




        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1 ) chiedi se effettivamente vuoi cancellare le/la foto fatte

/*                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                // 2) ciclo for do cancellazione da lista foto
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
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFinishing()){
                            new AlertDialog.Builder(SavePhoto.this)
                                    .setMessage("Are you sure you want to cancel this set?")
                                    .setCancelable(true).setNegativeButton("Anulla", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked

                                }
                            })
                                    .setPositiveButton("Cancella Sessione", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.out.println("file Deleted :" + photo_path);
                                            Intent intent = new Intent(SavePhoto.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).show();
                        }
                    }
                });


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


