package com.example.explorejournal;

import android.os.Bundle;
import android.view.View;

public class LogNewAttemptActivity extends BaseActivity{

    String recipe_id;
    String google_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_new_attempt);
        recipe_id = getIntent().getStringExtra("recipe_id");
        google_uid = getIntent().getStringExtra("google_uid");
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}