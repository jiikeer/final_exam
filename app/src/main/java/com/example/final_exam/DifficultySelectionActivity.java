package com.example.final_exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DifficultySelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selection);

        Button primaryButton = findViewById(R.id.primary_button);
        Button middleButton = findViewById(R.id.middle_button);
        Button universityButton = findViewById(R.id.university_button);

        primaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity("primary");
            }
        });

        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity("middle");
            }
        });

        universityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity("university");
            }
        });
    }

    private void startMainActivity(String level) {
        Intent intent = new Intent(DifficultySelectionActivity.this, MainActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
        finish();
    }
}