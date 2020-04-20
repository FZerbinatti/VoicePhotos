package com.francesco.voicephotos.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.francesco.voicephotos.Interfaces.RecyclerViewClickListener;
import com.francesco.voicephotos.R;
import com.francesco.voicephotos.adapters.RecyclerViewAdapter;
import com.francesco.voicephotos.db.DatabaseHelper;
import com.francesco.voicephotos.models.Photo;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    private static String TAG ="Gallery: ";
    DatabaseHelper mDatabaseHelper;
    RecyclerView recyclerView;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 8;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    List <Photo> photoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.gallery_recyclerview);
        hideSoftKeyboard();
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        photoList = mDatabaseHelper.getPhotos();
        Log.d(TAG, "onCreate: Photos list size:" + photoList.size());

        final GridLayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 8);
        recyclerView.setLayoutManager(mLayoutManager);

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                ArrayList<String> images_paths = null;
                // open the Full screen image display
                Intent intent = new Intent(Gallery.this, FullScreenActivity.class);
                // passa solamente una lista dei path delle immagini
                for (int i=0; i<photoList.size(); i++){
                    images_paths.add(photoList.get(i).getPhoto_path());
                }

                intent.putExtra("PHOTOS_PATHS", images_paths) ;
                intent.putExtra("PHOTO_POSITION", position);
                startActivity(intent);


            }
        };

        RecyclerViewAdapter adapter = new RecyclerViewAdapter( this, photoList , listener                                                                                                                                                               );
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                Log.d(TAG, "onScrolled: totalItemCount" +totalItemCount);
                Log.d(TAG, "onScrolled: visibleItemCount"+visibleItemCount);
                Log.d(TAG, "onScrolled: firstVisibleItem"+firstVisibleItem);

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.i("Yaeye!", "end called");

                    // Do something

                    loading = true;
                }
            }
        });


    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }
}
