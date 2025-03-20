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
import com.mediaghor.rainbowtools.Activities.ImageSizeReducer;
import com.mediaghor.rainbowtools.Activities.TextExtractorActivity;
import com.mediaghor.rainbowtools.Models.CardItemsModel;
import com.mediaghor.rainbowtools.PDFOptimization.pdf_to_docx;
import com.mediaghor.rainbowtools.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardItemAdapter extends RecyclerView.Adapter<CardItemAdapter.ViewHolder> {

    private Context context;
    private List<CardItemsModel> cardItemsList;
    private OnItemClickListener listener;


    final int PAGE;

    // Constructor
    public CardItemAdapter(Context context, List<CardItemsModel> cardItemsList,int page) {
        this.context = context;
        this.cardItemsList = cardItemsList;
        this.PAGE = page;
    }

    public interface OnItemClickListener {
        void onItemClick(int cardNo);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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



        // Define a mapping of text to activity classes
        
        if(PAGE == 1){
            Map<String, Class<?>> activityMap = new HashMap<>();
            activityMap.put("Background Remover", BackgroundRemoverActivity.class);
            activityMap.put("Photo Optimizer", EnhanceImagesActivity.class);
            activityMap.put("Text Extractor From Images", TextExtractorActivity.class);
            activityMap.put("Image Size Reducer", ImageSizeReducer.class);

            holder.cardView.setOnClickListener(v -> {
                Class<?> activityClass = activityMap.get(currentItem.getText());
                if (activityClass != null) {
                    Intent intent = new Intent(context, activityClass);
                    context.startActivity(intent);
                } else {
                    Log.d("LINE70", "Intent Null");
                }
            });
        } else if (PAGE == 2) {
            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, pdf_to_docx.class);
                intent.putExtra("position",position+1);
                context.startActivity(intent);
            });
        }


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
