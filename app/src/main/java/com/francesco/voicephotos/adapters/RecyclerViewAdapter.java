package com.francesco.voicephotos.adapters;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.util.Log;
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

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter <RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Photo> photos;
    RecyclerViewClickListener clickListener;
    private int orientation;

    public RecyclerViewAdapter(Context context, List<Photo> photos, RecyclerViewClickListener clickListener) {
        this.context = context;
        this.photos = photos;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.cardview_item_book, viewGroup,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Log.d(TAG, "onBindViewHolder: Test: photo size: "+ photos.size());
        Log.d(TAG, "onBindViewHolder: photos number: "+i + "details: "+ viewHolder.photo_preview_text.getText() + " - " +(photos.get(i).getPhoto_description()));
        viewHolder.photo_preview_text.setText(photos.get(i).getPhoto_description());

        File imgFile = new File (photos.get(i).getPhoto_path());



       // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        orientation=Integer.parseInt(photos.get(i).getOrientation());


        //viewHolder.photo_preview.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView photo_preview;
        private TextView photo_preview_text;
        private ProgressBar progressBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photo_preview = (ImageView) itemView.findViewById(R.id.book_img_id);
            photo_preview_text = (TextView) itemView.findViewById(R.id.book_title_id);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            clickListener.onClick(itemView, getAdapterPosition());

        }
    }
}
