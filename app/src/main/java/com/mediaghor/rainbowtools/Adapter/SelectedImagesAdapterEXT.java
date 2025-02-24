package com.mediaghor.rainbowtools.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;

public class SelectedImagesAdapterEXT extends RecyclerView.Adapter<SelectedImagesAdapterEXT.ViewHolder> {

    private final ArrayList<Uri> selectedUris;
    private final Context context;
    ButtonAnimationManager buttonAnimationManager;
    private boolean isUploading;



    public SelectedImagesAdapterEXT(Context context, ArrayList<Uri> selectedUris) {
        this.context = context;
        this.selectedUris = selectedUris;
        buttonAnimationManager = new ButtonAnimationManager(context);

    }

    @NonNull
    @Override
    public SelectedImagesAdapterEXT.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycler_for_selected_images_of_text_extractor,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Uri uri = selectedUris.get(position);

        // Load the image using Glide for efficiency
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.placeholder_image) // Optional: Set a placeholder image
                .into(holder.imageView);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveItem(position);
            }
        });

        if(isUploading){
            buttonAnimationManager.RemoveCrossButtonEXT("disable", holder.itemView);
        }else {
            buttonAnimationManager.RemoveCrossButtonEXT("enable", holder.itemView);

        }
    }

    @Override
    public int getItemCount() {
        return selectedUris.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_id_recycler_EXT);
            deleteButton = itemView.findViewById(R.id.btn_cancel_from_selected_images);

        }
    }



    public void RemoveItem(int position){
        selectedUris.remove(position);
        if(selectedUris.size() == 0){
            buttonAnimationManager.SelectImageAnimation("loop_animation");
            buttonAnimationManager.GeneratingButtonAnimation("disable");
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, selectedUris.size());
    }

    public ArrayList<Uri> getFinalSelectedUris() {
        return selectedUris;
    }

    /**
     * Setting Uploading State Eg If Uploading Then Made Remove Animation Button Disable else Enable
     * @param state Set The state That if uploading or not
     */
    public void setUploadingState(boolean state) {
        isUploading = state;
        notifyDataSetChanged();
    }

}
