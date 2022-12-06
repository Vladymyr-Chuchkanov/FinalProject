package com.chuchkanov.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.fbreader.book.Book;
import org.fbreader.book.BookLoader;
import org.fbreader.format.BookException;

public class ReaderActivity extends AppCompatActivity {
    private org.fbreader.sample.extensions.TextWidgetExt widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        widget = findViewById(R.id.text_widget);
        View errorView = findViewById(R.id.error_message);
        widget.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        String filepath = getIntent().getStringExtra("filepath");
        try {
            widget.setBook(BookLoader.fromFile(filepath, this, 1L));
            Book book = widget.controller().book;
            if (book != null) {
                widget.invalidate();
                widget.post(new Runnable() {
                    @Override public void run() {
                        widget.gotoPage(0);
                        setTitle(book.getTitle());
                    }
                });
            } else {
                errorView.setVisibility(View.VISIBLE);
            }
        } catch (BookException e) {
            e.printStackTrace();
            errorView.setVisibility(View.VISIBLE);
        }
    }
}