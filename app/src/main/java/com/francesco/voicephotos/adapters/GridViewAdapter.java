package com.francesco.voicephotos.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.francesco.voicephotos.Interfaces.RecyclerViewClickListener;
import com.francesco.voicephotos.R;
import com.francesco.voicephotos.models.Photo;

import java.io.File;
import java.util.List;

public class GridViewAdapter extends RecyclerView.Adapter <GridViewAdapter.ViewHolder> {

    private Context context;
    private List<Photo> photos;
    private int orientation;

    public GridViewAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.adapter_save_photo, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        File imgFile = new File (photos.get(i).getPhoto_path());
        orientation=Integer.parseInt(photos.get(i).getOrientation());
        final ProgressBar progressBar = viewHolder.progressBar;


        Glide.with(context).load(imgFile).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).thumbnail(0.1f).into(viewHolder.photo_preview);

        viewHolder.photo_preview.setRotation(orientation);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private ImageView photo_preview;
        private ProgressBar progressBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo_preview = (ImageView) itemView.findViewById(R.id.adapter_gridview_img_id);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_savePhoto_gridview);


        }



    }
}
