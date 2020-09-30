package com.sg.tmdbmovies.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sg.tmdbmovies.BuildConfig;
import com.sg.tmdbmovies.R;

public class TicketBookingActivity extends AppCompatActivity {

    WebView myWebView;
    String bookingUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booking);
        myWebView = findViewById(R.id.myWebView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        bookingUrl = BuildConfig.BookingUrl;
        myWebView.loadUrl(bookingUrl);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                myWebView.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}