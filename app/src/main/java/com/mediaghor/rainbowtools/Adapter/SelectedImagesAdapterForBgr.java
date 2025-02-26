package com.mediaghor.rainbowtools.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;

public class SelectedImagesAdapterForBgr extends RecyclerView.Adapter<SelectedImagesAdapterForBgr.ViewHolder> {

    private final ArrayList<Uri> selectedUris;
    private final Context context;
    ButtonAnimationManager buttonAnimationManager;
    private boolean isUploading;


    public SelectedImagesAdapterForBgr(Context context, ArrayList<Uri> selectedUris) {
        this.context = context;
        this.selectedUris = selectedUris;
        buttonAnimationManager = new ButtonAnimationManager(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_for_selected_images_to_remove_bg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = selectedUris.get(position);

        // Load the image using Glide for efficiency
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.placeholder_image) // Optional: Set a placeholder image
                .into(holder.imageView);

        // Set click listener for the delete button
        if(isUploading){
            holder.deleteButton.setVisibility(View.GONE);
        }
        holder.deleteButton.setOnClickListener(v -> {
            selectedUris.remove(position);
            if(selectedUris.size() == 0){
                buttonAnimationManager.ManageSunBackground("disable");
                buttonAnimationManager.SelectImageAnimation("loop_animation");
                buttonAnimationManager.GeneratingButtonAnimation("disable");
            }
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, selectedUris.size());
        });

    }

    @Override
    public int getItemCount() {
        return selectedUris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.selected_image);
            deleteButton = itemView.findViewById(R.id.btn_cancel_from_selected_images);
        }
    }
    public ArrayList<Uri> getFinalSelectedUris() {
        return selectedUris;
    }
    public void setUploadingStates(boolean uploading) {
        isUploading = uploading;
        notifyDataSetChanged();
    }

}
