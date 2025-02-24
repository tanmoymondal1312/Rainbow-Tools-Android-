package com.mediaghor.rainbowtools.Adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.mediaghor.rainbowtools.Dialog.TextEditorFullScreenDialog;
import com.mediaghor.rainbowtools.OthersClasses.ButtonAnimationManager;
import com.mediaghor.rainbowtools.R;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeneratedTextItemAdapterEXT extends RecyclerView.Adapter<GeneratedTextItemAdapterEXT.ViewHolder> {

    private final List<Uri> apiUrlList;
    private final ArrayList<String> texts = new ArrayList<>();
    private final Context context;
    private final ButtonAnimationManager buttonAnimationManager;
    private final Handler handler = new Handler();
    private boolean isScrolling = false;
    private final String SCROLL_TAG = "ScrollInRecycler";
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);


    public GeneratedTextItemAdapterEXT(Context context, List<Uri> apiUrlList) {
        this.context = context;
        this.apiUrlList = apiUrlList;
        this.buttonAnimationManager = new ButtonAnimationManager(context);


        for (int i = 0; i < apiUrlList.size(); i++) {
            texts.add("Loading...");
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_for_text_item_in_ext, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.textView.setText(texts.get(position));

        // If data is still "Loading...", fetch it
        if (texts.get(position).equals("Loading...")) {
            fetchTextFromApi(position, apiUrlList.get(position));
        }



        buttonAnimationManager.AnimCopyTextsManager("initial", holder.itemView);
        holder.animCopy.setOnClickListener(v -> {
            String textToCopy = holder.textView.getText().toString();
            if (!textToCopy.isEmpty()) {
                copyToClipboard(textToCopy, v.getContext());
                buttonAnimationManager.AnimCopyTextsManager("play", holder.itemView);
            }
        });
        setupScrollControls(holder);


        holder.saveAsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time and format it
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Rainbow_Tools_Extracted_Texts_" + timeStamp + ".txt";
                String content = holder.textView.getText().toString();
                saveAsTXT(fileName, content, context);
            }
        });
        holder.saveAsPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Rainbow_Tools_Extracted_Texts_" + timeStamp;
                String content = holder.textView.getText().toString();
                saveAsPDF(fileName, content, context);
            }
        });
        holder.saveAsDocx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fileName = "Rainbow_Tools_Extracted_Texts_" + timeStamp;
                String content = holder.textView.getText().toString();
                saveAsDOCX(fileName, content, context);
            }
        });

        holder.edtTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = holder.textView.getText().toString();
                int position = holder.getAdapterPosition();

                Intent intent = new Intent(context, TextEditorFullScreenDialog.class);
                intent.putExtra("texts", content);
                intent.putExtra("position", position);


                // Ensure the context is an Activity before casting
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, 121);
                } else {
                    context.startActivity(intent); // Fallback if context is not an Activity
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return apiUrlList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LottieAnimationView animCopy;
        ImageButton edtTxt, scrollUp, scrollDown,saveAsTxt,saveAsPdf,saveAsDocx;
        NestedScrollView nestedScrollView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.texts_is_ext);
            animCopy = itemView.findViewById(R.id.anim_copy_txt_in_ext);
            edtTxt = itemView.findViewById(R.id.btn_edit_text_ext_recycler);
            nestedScrollView = itemView.findViewById(R.id.scrollView_in_ext_card_item);
            scrollUp = itemView.findViewById(R.id.btn_scroll_up_ext_item);
            scrollDown = itemView.findViewById(R.id.btn_scroll_down_ext_item);
            saveAsTxt = itemView.findViewById(R.id.btn_save_as_txt_ext_item);
            saveAsPdf = itemView.findViewById(R.id.btn_save_as_pdf_ext_item);
            saveAsDocx = itemView.findViewById(R.id.btn_save_as_docx_ext_item);

        }
    }


    private void fetchTextFromApi(int position, Uri uri) {
        executorService.execute(() -> {
            try {
                URL url = new URL(uri.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    reader.close();

                    String finalText = result.toString().trim();
                    if (!finalText.isEmpty()) {
                        texts.set(position, finalText);
                        notifyItemChangedOnUiThread(position);
                    }else {
                        texts.set(position, "<--No Texts Find In Image-->");
                        notifyItemChangedOnUiThread(position);
                    }
                } else {
                    showToast("Failed: " + responseCode);
                }
            } catch (Exception e) {
                showToast("Error: " + e.getMessage());
            }
        });
    }

    private void notifyItemChangedOnUiThread(int position) {
        ((android.app.Activity) context).runOnUiThread(() -> notifyItemChanged(position));
    }

    private void showToast(String message) {
        ((android.app.Activity) context).runOnUiThread(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }




    private void autoScroll(NestedScrollView scrollView, int scrollAmount) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScrolling) {
                    scrollView.smoothScrollBy(0, scrollAmount);
                    handler.postDelayed(this, 50);
                }
            }
        }, 50);
    }
    private void setupScrollControls(ViewHolder holder) {
        View child = holder.nestedScrollView.getChildAt(0);
        if (child == null) {
            Log.e(SCROLL_TAG, "No child view inside NestedScrollView.");
            return;
        }

        child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                child.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                holder.scrollUp.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        isScrolling = true;
                        autoScroll(holder.nestedScrollView, -10);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        isScrolling = false;
                    }
                    return false;
                });

                holder.scrollDown.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        isScrolling = true;
                        autoScroll(holder.nestedScrollView, 10);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        isScrolling = false;
                    }
                    return false;
                });

                holder.scrollUp.setOnClickListener(v -> {
                    if (holder.nestedScrollView.canScrollVertically(-1)) { // Check if scrolling up is possible
                        holder.nestedScrollView.smoothScrollBy(0, -50);
                    }
                });

                holder.scrollDown.setOnClickListener(v -> {
                    if (holder.nestedScrollView.canScrollVertically(1)) { // Check if scrolling down is possible
                        holder.nestedScrollView.smoothScrollBy(0, 50);
                    }
                });
            }
        });
    }


    private void copyToClipboard(String text, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
    private void scanFile(Context context, File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                (path, uri) -> Log.d("MediaScanner", "Scanned: " + path));
    }


    public void saveAsTXT(String fileName, String content, Context context) {
        try {
            // Define the directory
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Rainbow Tools Documents");
            if (!dir.exists()) dir.mkdirs();

            // Create a unique filename if the file exists
            File file = new File(dir, fileName);
            int count = 1;
            while (file.exists()) {
                file = new File(dir, fileName.replace(".txt", "_" + count + ".txt"));
                count++;
            }

            // Write content to the file
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();

            // Scan the file so it appears in storage
            scanFile(context, file);
            Toast.makeText(context, "Saved as txt", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save TXT", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveAsPDF(String fileName, String content, Context context) {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Rainbow Tools Documents");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName + ".pdf");

            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawText(content, 50, 50, paint);
            pdfDocument.finishPage(page);
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();

            // Scan the file
            scanFile(context, file);
            Toast.makeText(context, "Saved as PDF", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveAsDOCX(String fileName, String content, Context context) {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Rainbow Tools Documents");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName + ".docx");

            XWPFDocument document = new XWPFDocument();
            FileOutputStream out = new FileOutputStream(file);
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(content);
            document.write(out);
            out.close();
            document.close();

            // Scan the file
            scanFile(context, file);
            Toast.makeText(context, "Saved as DOCX", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save DOCX", Toast.LENGTH_SHORT).show();
        }
    }



    public void updateText(int position, String newText) {
        if (position >= 0 && position < apiUrlList.size()) { // Ensure valid index
            texts.add(position,newText);
            notifyItemChangedOnUiThread(position);
            Log.d("RE_EXT", "Updated text at position " + position + ": " + newText);
        } else {
            Log.e("RE_EXT", "Invalid position: " + position);
        }
    }






}
