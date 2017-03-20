package com.kiriloff.max.loyaltyplant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;


public class ProgressActivity extends AppCompatActivity {

    private ContentLoadingProgressBar contentLoadingProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        contentLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar_full_view);
        contentLoadingProgressBar.show();



    }
}
