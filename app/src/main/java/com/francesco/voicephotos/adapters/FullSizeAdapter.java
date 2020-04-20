package com.francesco.voicephotos.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.voicephotos.R;

import java.util.ArrayList;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> images_paths;
    LayoutInflater inflater;

    public FullSizeAdapter(Context context, ArrayList <String> images_paths) {

        this.context =  context;
        this.images_paths= images_paths;


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
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.activity_full_screen,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.full_item_image);
        Glide.with(context).load(images_paths.get(position)).apply(new RequestOptions().centerInside()).into(imageView);

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
