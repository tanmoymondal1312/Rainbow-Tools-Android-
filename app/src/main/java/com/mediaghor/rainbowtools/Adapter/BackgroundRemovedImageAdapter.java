package com.mediaghor.rainbowtools.Adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mediaghor.rainbowtools.R;

import java.util.List;

public class BackgroundRemovedImageAdapter extends RecyclerView.Adapter<BackgroundRemovedImageAdapter.ViewHolder> {
    private final List<Uri> imageUrls;

    public BackgroundRemovedImageAdapter(List<Uri> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public BackgroundRemovedImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_for_bg_removed_images, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BackgroundRemovedImageAdapter.ViewHolder holder, int position) {
        Uri imageUrl = imageUrls.get(position);
        Glide.with(holder.imv_listMedia.getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Get image dimensions
                        int imageHeight = resource.getHeight();
                        int imageWidth = resource.getWidth();

                        // Calculate dynamic height in dp
                        int dynamicHeightInPx = imageHeight; // Get image height in pixels
                        int dynamicHeightInDp = (int) (dynamicHeightInPx / holder.imv_listMedia.getResources().getDisplayMetrics().density);

                        // Set the height dynamically
                        ViewGroup.LayoutParams layoutParams = holder.imv_listMedia.getLayoutParams();
                        layoutParams.height = (int) (dynamicHeightInDp * holder.imv_listMedia.getResources().getDisplayMetrics().density);
                        holder.imv_listMedia.setLayoutParams(layoutParams);

                        // Set the image into the ImageView
                        holder.imv_listMedia.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder logic if needed
                    }
                });
    }


    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imv_listMedia;
        AppCompatButton btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_listMedia = itemView.findViewById(R.id.rv_bg_removed_image);
            btn = itemView.findViewById(R.id.btn_download);
        }
    }
}
