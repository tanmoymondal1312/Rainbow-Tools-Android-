package com.mediaghor.rainbowtools.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mediaghor.rainbowtools.Activities.BackgroundRemoverActivity;
import com.mediaghor.rainbowtools.Activities.EnhanceImagesActivity;
import com.mediaghor.rainbowtools.Activities.TextExtractorActivity;
import com.mediaghor.rainbowtools.Models.CardItemsModel;
import com.mediaghor.rainbowtools.R;

import java.util.List;

public class CardItemAdapter extends RecyclerView.Adapter<CardItemAdapter.ViewHolder> {

    private Context context;
    private List<CardItemsModel> cardItemsList;

    // Constructor
    public CardItemAdapter(Context context, List<CardItemsModel> cardItemsList) {
        this.context = context;
        this.cardItemsList = cardItemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_use_now_card, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItemsModel currentItem = cardItemsList.get(position);
        // Set text
        holder.textView.setText(currentItem.getText());
        String imageUri = currentItem.getImageUri();

        Glide.with(context)
                .load(Uri.parse(imageUri))
                .placeholder(R.drawable.emtyimgbgrmv) // Optional placeholder
                .error(R.drawable.placeholder_image)            // Optional error image
                .into(holder.imageView);



        holder.cardView.setOnClickListener(v -> {
            Intent intent = null;
            if (currentItem.getText().equals("Background Remover")) {
                intent = new Intent(context, BackgroundRemoverActivity.class);
            }else if(currentItem.getText().equals("Photo Optimizer")){
                intent = new Intent(context, EnhanceImagesActivity.class);
            } else if (currentItem.getText().equals("Text Extractor From Images")) {
                intent = new Intent(context, TextExtractorActivity.class);
            }else if (currentItem.getText().equals("Image Size Reducer")) {
                intent = new Intent(context, TextExtractorActivity.class);

            }
            // Start the activity if an intent is created
            if (intent != null) {
                context.startActivity(intent);
            }else {
                Log.d("LINE70","intent Null");

            }
        });
    }




    @Override
    public int getItemCount() {
        return cardItemsList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
       ImageView imageView;
       TextView textView;
       CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views

            imageView = itemView.findViewById(R.id.tool_icon_img_id);
            textView = itemView.findViewById(R.id.tool_name_id);
            cardView = itemView.findViewById(R.id.parent_card);


        }
    }
}
