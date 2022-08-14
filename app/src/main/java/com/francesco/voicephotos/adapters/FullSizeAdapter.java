package com.francesco.voicephotos.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.voicephotos.R;
import com.francesco.voicephotos.activities.FullScreenActivity;
import com.francesco.voicephotos.db.DatabaseHelper;
import com.francesco.voicephotos.models.Photo;

import java.io.File;
import java.util.ArrayList;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> images_paths;
    LayoutInflater inflater;

    DatabaseHelper mDatabaseHelper;

    public FullSizeAdapter(Context context, ArrayList <String> images_paths) {

        this.context =  context;
        this.images_paths= images_paths;
        mDatabaseHelper = new DatabaseHelper(context);


    }

    @Override
    public int getCount() {
        return images_paths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.full_item,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.full_item_image);
        //id delle cose, get da db, implementa campi
        TextView photo_date = (TextView)view.findViewById(R.id.photo_date);
        final TextView photo_description = (TextView)view.findViewById(R.id.photo_description);
        LinearLayout button_save = (LinearLayout) view.findViewById(R.id.button_save);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.setPhotoDescription(images_paths.get(position)  ,photo_description.getText().toString());
                Toast.makeText(context, "Salvato!", Toast.LENGTH_SHORT).show();

            }
        });

        Photo thisPhoto = mDatabaseHelper.getPhotoDetails(images_paths.get(position));

        Glide.with(context).load(images_paths.get(position)).apply(new RequestOptions().centerInside()).into(imageView);
        imageView.setRotation(Integer.parseInt(thisPhoto.getOrientation()));

        String date_photo_database = thisPhoto.getPhoto_name();
        String date_photo_splits[] = date_photo_database.split("_");
        String actual_date = date_photo_splits[0];
        String actual_time = date_photo_splits[1];

        photo_date.setText("Data: " + actual_date.replace("-","/") +" Ora: " + actual_time.replace("-",":"));
        photo_description.setText(thisPhoto.getPhoto_description());

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;


    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       // super.destroyItem(container, position, object);

        ViewPager viewPager = (ViewPager) container;
        View view = (View)object;
        viewPager.removeView(view);


    }


}
