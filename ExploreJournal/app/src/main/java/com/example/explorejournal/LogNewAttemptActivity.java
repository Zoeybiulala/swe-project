package com.example.explorejournal;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONObject;

// Done button on multi-line editText using method from here:
// https://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button

public class LogNewAttemptActivity extends BaseActivity{

    String recipe_id;
    String google_uid;
    String recipe_name;
    EditText descriptionInput;
    RatingBar ratingInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_new_attempt);
        recipe_id = getIntent().getStringExtra("recipe_id");
        recipe_name = getIntent().getStringExtra("recipe_name");
        google_uid = getIntent().getStringExtra("google_uid");

        TextView recipeNameText = findViewById(R.id.recipe_name2);
        recipeNameText.setText(recipe_name);

        descriptionInput = findViewById(R.id.editTextTextPersonName);
        descriptionInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descriptionInput.setRawInputType(InputType.TYPE_CLASS_TEXT);

        ratingInput = findViewById(R.id.ratingBar);
    }

    public void onBackButtonClick(View view) {
        finish();
    }

    public void onSubmitButtonClick(View view) {
        String description = "(no description)";
        if(!String.valueOf(descriptionInput.getText()).isEmpty()){
            description = String.valueOf(descriptionInput.getText());
        }

        Log.v("attempt", "newattempt?uid=" + google_uid + "&rid=" + recipe_id + "&note=" + description + "&rating=" + ratingInput.getRating());
        JSONObject result = new ServerConnection("http://10.0.2.2:3000").get("newattempt?uid=" + google_uid + "&rid=" + recipe_id + "&note=" + description + "&rating=" + ratingInput.getRating());
        Log.v("attempt", result.toString());

        finish();
    }
}