package com.egyptianratscrew.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.egyptianratscrew.android.egyptianratscrew.R;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void newGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getResources().getString(R.string.new_game_preference), true);
        startActivity(intent);
    }

    public void continueGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getResources().getString(R.string.new_game_preference), false);
        startActivity(intent);
    }

    public void about(View view) {
        Intent intent = new Intent(this, OssLicensesMenuActivity.class);
        startActivity(intent);
    }
}
