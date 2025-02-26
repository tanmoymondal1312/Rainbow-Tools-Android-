package com.mediaghor.rainbowtools.Dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.mediaghor.rainbowtools.R;

import jp.wasabeef.richeditor.RichEditor;

public class TextEditorFullScreenDialog extends AppCompatActivity {
    private PhotoView photoView;
    private RichEditor editor;
    private FrameLayout editorContainer; // Added FrameLayout for moving the editor
    LinearLayout BottomOkCancelLayout;
    private View rootView;
    private int keyboardHeight = 0;
    private boolean isKeyboardVisible = false;
    ImageButton btnUndo,btnRedo,btnCloseActivity,btnOkActivity;
    Uri imgUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_text_editor_full_screen_dialog);

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        photoView = findViewById(R.id.photoView_in_edt_txt_dialog);
        editor = findViewById(R.id.rich_editor_in_edit_txt_dialog);
        editorContainer = findViewById(R.id.editor_container_in_edit_text_dialog); // FrameLayout to adjust position
        BottomOkCancelLayout = findViewById(R.id.bottom_ok_cancel_txt_editor_dialog);
        rootView = findViewById(android.R.id.content);
        btnUndo = findViewById(R.id.btn_undo_txt_edt_txt_dialog);
        btnRedo = findViewById(R.id.btn_redo_txt_edt_txt_dialog);
        btnCloseActivity = findViewById(R.id.btn_close_activity_txt_editor_dialog);
        btnOkActivity = findViewById(R.id.btn_ok_activity_txt_editor_dialog);

        // 1️⃣ Set the default text
        String receivedText = getIntent().getStringExtra("texts");
        int position = getIntent().getIntExtra("position", -1); // Default value -1 if not found
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            imgUri = getIntent().getParcelableExtra("imgUri", Uri.class);
        } else {
            imgUri = getIntent().getParcelableExtra("imgUri");
        }


        editor.setHtml(receivedText);

        // 2️⃣ Reset undo/redo history (Prevent undo/redo of default text)
        resetUndoRedoHistory();


        // Set Background Image
        if (imgUri != null) {
            Glide.with(this)
                    .load(imgUri)
                    .into(photoView);
        }


        // Configure RichEditor
        setupRichEditor();

        // Detect Keyboard State
        detectKeyboardState();



        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.undo();
            }
        });
        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.redo();
            }
        });
        btnCloseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnOkActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedTextHtml = editor.getHtml(); // Get HTML text
                Spanned spannedText = HtmlCompat.fromHtml(editedTextHtml, HtmlCompat.FROM_HTML_MODE_LEGACY); // Convert to spanned
                String cleanedText = spannedText.toString().trim(); // Remove extra spaces

                Intent resultIntent = new Intent();
                resultIntent.putExtra("editedText", cleanedText);
                resultIntent.putExtra("position", position);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });


    }

    private void setupRichEditor() {
        editor.setEditorHeight(200);
        editor.setEditorFontSize(18);
        editor.setEditorBackgroundColor(0);
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder("Start writing...");
    }

    private void detectKeyboardState() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int heightDiff = screenHeight - r.bottom;

            if (heightDiff > screenHeight * 0.15) { // Keyboard Open
                if (!isKeyboardVisible) {
                    isKeyboardVisible = true;
                    keyboardHeight = heightDiff;
                    moveEditorUp(keyboardHeight);
                }
            } else { // Keyboard Closed
                if (isKeyboardVisible) {
                    isKeyboardVisible = false;
                    resetEditorPosition();
                }
            }
        });
    }

    // Move editor up when keyboard opens
    private void moveEditorUp(int height) {
        int LinearLHeight = BottomOkCancelLayout.getHeight();
        editorContainer.setPadding(0, 0, 0, height-LinearLHeight);
    }

    // Reset editor position when keyboard closes
    private void resetEditorPosition() {
        editorContainer.setPadding(0, 0, 0, 0);
    }
    private void resetUndoRedoHistory() {
        // Bring focus to the editor and immediately remove it
        editor.focusEditor();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editor.clearFocus(); // Ensures the initial state is locked
            }
        }, 100);
    }
}
