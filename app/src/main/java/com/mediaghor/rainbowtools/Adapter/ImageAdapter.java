package com.mediaghor.rainbowtools.Adapter;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.Helpers.OnItemClickListener;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final Context context;
    private final List<Uri> imageUris;
    private final List<Uri> selectedUris = new ArrayList<>();
    private OnItemClickListener listener;
    private final int maxSelection;


    public ImageAdapter(Context context, List<Uri> imageUris, int MaxSelection) {
        this.context = context;
        this.imageUris = imageUris;
        this.maxSelection = MaxSelection;
        if (context instanceof OnItemClickListener) {
            this.listener = (OnItemClickListener) context;
        } else {
            throw new IllegalArgumentException("Context must implement OnItemClickListener");
        }

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        try {
            Uri uri = imageUris.get(position);

            // Load image using Glide
            Glide.with(context).load(uri).into(holder.imageView);



            holder.checkBox.setChecked(selectedUris.contains(uri));
            if (selectedUris.contains(uri)) {
                holder.frmLayout.setBackgroundResource(R.color.transparent_gray);
                holder.textView.setVisibility(View.VISIBLE);
                holder.textView.setText(String.valueOf(selectedUris.size()));
            } else {
                holder.frmLayout.setBackgroundResource(android.R.color.transparent);
                holder.textView.setText(String.valueOf(selectedUris.size()));
                holder.textView.setVisibility(View.GONE);
            }

            // Set click listener
            holder.frmLayout.setOnClickListener(v -> {

                try {
                    if (!holder.checkBox.isChecked()) {
                        if (maxSelection == selectedUris.size()){
                            Toast.makeText(context, "Can Not Select More Than "+maxSelection, Toast.LENGTH_SHORT).show();
                        }else{
                            selectedUris.add(uri);
                            holder.checkBox.setChecked(true);
                            holder.frmLayout.setBackgroundResource(R.color.transparent_gray);
                            holder.textView.setVisibility(View.VISIBLE);
                            holder.textView.setText(String.valueOf(selectedUris.size()));
                            if (listener != null) {
                                listener.onItemClick(uri,1);
                            }
                        }

                    } else {
                         if (listener != null) {
                            listener.onItemClick(uri,0);
                        }
                        selectedUris.remove(uri);
                        holder.checkBox.setChecked(false);
                        holder.frmLayout.setBackgroundResource(android.R.color.transparent);
                        holder.textView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("onClick", "Error processing click: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            Log.e("onBindViewHolder", "Error binding view holder at position " + position + ": " + e.getMessage(), e);
        }
    }


    @Override
    public int getItemCount() {
        return imageUris.size();
    }


    public List<Uri> getSelectedUris() {
        return selectedUris;
    }


    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        FrameLayout frmLayout;
        TextView textView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.checkBox);
            frmLayout = itemView.findViewById(R.id.frame_layout_of_img_selected_enable);
            textView = itemView.findViewById(R.id.item_image_text_view);;
        }
    }

}

