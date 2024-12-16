package com.mediaghor.rainbowtools.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mediaghor.rainbowtools.Activities.BgremoverActivity;
import com.mediaghor.rainbowtools.Models.CardItemsModel;
import com.mediaghor.rainbowtools.R;

import java.util.List;
import java.util.Objects;

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
        holder.parentCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
        // Set text
        holder.textView.setText(currentItem.getText());
        Log.d("VideoPlayback", "Text set for position: " + position);

        // Set video URI
        String videoUri = currentItem.getVideoUri();
        Log.d("VideoPlayback", "Received URI for video at position " + position + ": " + videoUri);

        if (videoUri != null && !videoUri.isEmpty()) {
            try {
                // Parse URI
                Uri uri = Uri.parse(videoUri);
                Log.d("VideoPlayback", "Parsed URI for video at position " + position + ": " + uri.toString());

                // Set VideoView URI
                holder.videoView.setVideoURI(uri);
                Log.d("VideoPlayback", "Video URI set for VideoView at position " + position);

                holder.videoView.requestFocus();
                Log.d("VideoPlayback", "Requested focus for VideoView at position " + position);

                // Set listeners
                holder.videoView.setOnPreparedListener(mediaPlayer -> {
                    int videoWidth = mediaPlayer.getVideoWidth();
                    int videoHeight = mediaPlayer.getVideoHeight();

                    // Calculate aspect ratio
                    float aspectRatio = (float) videoWidth / (float) videoHeight;

                    // Set the fixed width (e.g., 200dp)
                    int fixedWidthPx = (int) (135 * context.getResources().getDisplayMetrics().density); // Convert 1350dp to pixels

                    // Calculate the new height based on the fixed width and the aspect ratio
                    int calculatedHeightPx = (int) (fixedWidthPx / aspectRatio);

                    // Set the new layout parameters
                    ViewGroup.LayoutParams layoutParams = holder.videoView.getLayoutParams();
                    layoutParams.width = fixedWidthPx; // Set fixed width
                    layoutParams.height = calculatedHeightPx; // Set calculated height
                    holder.videoView.setLayoutParams(layoutParams);

                    //mediaPlayer.setLooping(true); // Loop video
                    holder.videoView.start();
                    Log.d("VideoPlayback", "Video started successfully at position " + position);
                });

                holder.videoView.setOnErrorListener((mp, what, extra) -> {
                    Log.e("VideoError", "Error playing video at position " + position + ". Code: " + what + ", Extra: " + extra);
                    return true; // Error handled
                });

            } catch (Exception e) {
                Log.e("VideoPlaybackError", "Exception occurred while setting video at position " + position, e);
            }
        } else {
            Log.e("VideoPlayback", "Invalid or empty URI for video at position " + position);
        }
        holder.parentCard.setOnClickListener(v -> {
            if (currentItem.getText().equals("Background Remover")) {
                Intent intent = new Intent(context, BgremoverActivity.class);
                context.startActivity(intent);
            }
        });
    }




    @Override
    public int getItemCount() {
        return cardItemsList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        VideoView videoView;
        CardView parentCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views

            textView = itemView.findViewById(R.id.tool_name);
            videoView = itemView.findViewById(R.id.video_view);
            parentCard = itemView.findViewById(R.id.parent_card_id);
        }
    }
}
