package com.mediaghor.rainbowtools.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mediaghor.rainbowtools.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class PhotoPickerAdapter extends RecyclerView.Adapter<PhotoPickerAdapter.ViewHolder> {
    List<Uri> uris;

    public PhotoPickerAdapter(List<Uri> uris) {
        this.uris = uris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_for_selected_images_to_remove_bg, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = uris.get(position);

        // Load the image to get its dimensions
        try {
            InputStream inputStream = holder.itemView.getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Set the image URI
            holder.imv_listMedia.setImageURI(uri);

            holder.crossBtn.setOnClickListener(v -> {
                // Remove the image from the list (remove from data)
                uris.remove(position);

                // Notify the adapter that an item was removed
                notifyItemRemoved(position);

                // Optionally, you can notify the item range change for better performance
                notifyItemRangeChanged(position, uris.size());
            });

            // Calculate the aspect ratio and set the dynamic height
            if (bitmap != null) {
                int originalWidth = bitmap.getWidth();
                int originalHeight = bitmap.getHeight();
                float aspectRatio = (float) originalHeight / originalWidth;

                // Convert 170dp to pixels
                float density = holder.imv_listMedia.getContext().getResources().getDisplayMetrics().density;
                int fixedWidthInDp = 170;  // 170dp
                int fixedWidthInPx = (int) (fixedWidthInDp * density);  // Convert dp to px

                holder.imv_listMedia.getViewTreeObserver().addOnPreDrawListener(() -> {
                    // Set the width to the fixed width
                    ViewGroup.LayoutParams params = holder.imv_listMedia.getLayoutParams();
                    params.width = fixedWidthInPx;  // Set the fixed width in pixels

                    // Calculate the height based on the aspect ratio
                    int newHeight = (int) (fixedWidthInPx * aspectRatio);
                    params.height = newHeight;  // Set the calculated height based on aspect ratio

                    holder.imv_listMedia.setLayoutParams(params);

                    // Log the new width and height for debugging
                    Log.d("width", String.valueOf(fixedWidthInPx));
                    Log.d("height", String.valueOf(newHeight));

                    return true;
                });
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imv_listMedia;
        ImageButton crossBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_listMedia = itemView.findViewById(R.id.selected_image);
            crossBtn = itemView.findViewById(R.id.btn_cancel_from_selected_images);
        }
    }
}
