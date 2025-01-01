package com.mediaghor.rainbowtools.Adapter;

import static com.mediaghor.rainbowtools.R.color.transparent;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<Uri> imageUris;
    private final List<Uri> selectedUris = new ArrayList<>();

    public ImageAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(position);

        // Load image into ImageView using Glide
        Glide.with(context).load(uri).into(holder.imageView);
        holder.frmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                    holder.frmLayout.setBackgroundResource(R.color.transparent_gray);
                    holder.textView.setVisibility(View.VISIBLE);
                    selectedUris.add(uri);
                    holder.textView.setText(String.valueOf(getSelectedUris().size()));
                }
                else {
                    holder.checkBox.setChecked(false);
                    holder.frmLayout.setBackgroundResource(android.R.color.transparent);
                    holder.textView.setVisibility(View.GONE);
                    selectedUris.remove(uri);

                }
            }
        });

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
            textView = itemView.findViewById(R.id.item_image_text_view);
        }
    }
}

