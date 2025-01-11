package com.mediaghor.rainbowtools.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.R;

import java.util.List;

public class SelectedImageAdapter extends RecyclerView.Adapter<SelectedImageAdapter.SelectedImageViewHolder> {

    private static final String TAG = "SelectedImageAdapter";

    private final Context context;
    private final List<Uri> selectedUris;

    public SelectedImageAdapter(Context context, List<Uri> selectedUris) {
        this.context = context;
        this.selectedUris = selectedUris;
    }

    @NonNull
    @Override
    public SelectedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image_2, parent, false);
            return new SelectedImageViewHolder(view);
        } catch (Exception e) {
            Log.e("selected_img_tag", "Error in onCreateViewHolder: " + e.getMessage(), e);
            throw e; // Re-throwing so the crash can still be caught in logs.
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImageViewHolder holder, int position) {
        try {
            Log.d("selected_img_tag", selectedUris.toString());

            Uri uri = selectedUris.get(position);

            // Load image into ImageView using Glide
            Glide.with(context)
                    .load(uri)
                    .error(R.drawable.placeholder_image) // Optional: Placeholder for failed loads
                    .into(holder.imageView);
        } catch (Exception e) {
            Log.e("selected_img_tag", "Error in onBindViewHolder at position " + position + ": " + e.getMessage(), e);

            // Optional: Set a placeholder or clear the ImageView in case of failure
            holder.imageView.setImageResource(R.drawable.emtyimgbgrmv);
        }
    }

    @Override
    public int getItemCount() {
        try {
            return selectedUris.size();
        } catch (Exception e) {
            Log.e("selected_img_tag", "Error in getItemCount: " + e.getMessage(), e);
            return 0; // Return 0 to avoid crashes
        }
    }

    static class SelectedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SelectedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                imageView = itemView.findViewById(R.id.imageView_2);
            } catch (Exception e) {
                Log.e("selected_img_tag", "Error in ViewHolder constructor: " + e.getMessage(), e);
            }
        }
    }
}
