package com.example.android.egyptianratscrew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void newGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("newGame", true);
        startActivity(intent);
    }

    public void continueGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("newGame", false);
        startActivity(intent);
    }

    public void about(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void howTo(View view) {
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }
}
