package com.francesco.voicephotos.db;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.francesco.voicephotos.models.Photo;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper: ";
    private static final String DB_NAME = "voicephotos_db";
    private static final int DB_VERSION = 1;
    static final String TABLE_PHOTOS = "PHOTOS";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate:  creating table and inserting clients");
        createTables(db);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS );
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db){
        Log.d(TAG, "createTables: ");
        //creating tables
        String CREATE_TABLE_PHOTOS = "CREATE TABLE " + TABLE_PHOTOS +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "PHOTO_NAME" + " TEXT ,  "
                + "PHOTO_PATH" + " TEXT , "
                + "PHOTO_DESCRIPTION" + " TEXT , "
                + "PHOTO_ORIENTATION" + " TEXT   );";

        db.execSQL( CREATE_TABLE_PHOTOS );

    }

    public void addPhotoObject ( Photo photo ){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("PHOTO_NAME", photo.getPhoto_name());
        values.put("PHOTO_PATH", photo.getPhoto_path());
        values.put("PHOTO_DESCRIPTION", photo.getPhoto_description());
        values.put("PHOTO_ORIENTATION", photo.getOrientation());

        db.insert(TABLE_PHOTOS, null, values);
        db.close();
    }

    public Integer getPhotoNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        long numRows = (DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM table_name", null));
        int count = (int)numRows;
        return  count;
    }


    public ArrayList<Photo> getPhotos() {

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList <Photo> listPhotos = new ArrayList<Photo>();
        //                                 0           1              2                    3
        String selectQuery = "SELECT  PHOTO_NAME,  PHOTO_PATH, PHOTO_DESCRIPTION, PHOTO_ORIENTATION FROM PHOTOS";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
                listPhotos.add(photo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listPhotos;

    }
















////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




}

