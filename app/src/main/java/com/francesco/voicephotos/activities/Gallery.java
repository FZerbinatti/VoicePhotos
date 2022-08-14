package com.francesco.voicephotos.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

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

    SearchView searchview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.gallery_recyclerview);
        searchview = findViewById(R.id.searchview);
        hideSoftKeyboard();
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        photoList = mDatabaseHelper.getPhotos();
        Log.d(TAG, "onCreate: Photos list size:" + photoList.size());

        final GridLayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d(TAG, "onQueryTextSubmit: "+query);
               photoList=mDatabaseHelper.searchPhoto(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d(TAG, "onQueryTextChange: "+newText);
                photoList=mDatabaseHelper.searchPhoto(newText);

                // -------------------------------- RICOMPILA TUTTA LA LISTA CON I NUOVI DATI --------------------

                final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        ArrayList<String> images_paths = new ArrayList<>();
                        // open the Full screen image display
                        Intent intent = new Intent(Gallery.this, FullScreenActivity.class);
                        // passa solamente una lista dei path delle immagini
                        for (int i=0; i<photoList.size(); i++){
                            images_paths.add(photoList.get(i).getPhoto_path());
                            Log.d(TAG, "onClick: path: "+photoList.get(i).getPhoto_path());
                        }

                        intent.putExtra("PHOTOS_PATHS", images_paths) ;
                        Log.d(TAG, "onClick: position passed: "+position);
                        intent.putExtra("PHOTO_POSITION", position);
                        startActivity(intent);

                    }
                };

                RecyclerViewAdapter adapter = new RecyclerViewAdapter( getApplicationContext(), photoList , listener                                                                                                                                                               );
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
                recyclerView.setAdapter(adapter);

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                        visibleItemCount = recyclerView.getChildCount();
                        totalItemCount = mLayoutManager.getItemCount();


                        Log.d(TAG, "onScrolled: firstVisibleItem"+firstVisibleItem);
                        Log.d(TAG, "onScrolled: visibleItemCount"+visibleItemCount);
                        Log.d(TAG, "onScrolled: totalItemCount" +totalItemCount);

                        if (loading) {
                            if (totalItemCount > previousTotal) {
                                loading = false;
                                previousTotal = totalItemCount;
                            }
                        }
                        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                            loading = true;
                        }
                    }
                });


                return false;
            }
        });

        final RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                ArrayList<String> images_paths = new ArrayList<>();
                // open the Full screen image display
                Intent intent = new Intent(Gallery.this, FullScreenActivity.class);
                // passa solamente una lista dei path delle immagini
                for (int i=0; i<photoList.size(); i++){
                    images_paths.add(photoList.get(i).getPhoto_path());
                    Log.d(TAG, "onClick: path: "+photoList.get(i).getPhoto_path());
                }

                intent.putExtra("PHOTOS_PATHS", images_paths) ;
                Log.d(TAG, "onClick: position passed: "+position);
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

                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();


                Log.d(TAG, "onScrolled: firstVisibleItem"+firstVisibleItem);
                Log.d(TAG, "onScrolled: visibleItemCount"+visibleItemCount);
                Log.d(TAG, "onScrolled: totalItemCount" +totalItemCount);

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                    loading = true;
                }
            }
        });




    }

    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: text== "+newText);
        if (TextUtils.isEmpty(newText)) {
            //mListView.clearTextFilter();
        } else {
            //mListView.setFilterText(newText.toString());
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }
}
