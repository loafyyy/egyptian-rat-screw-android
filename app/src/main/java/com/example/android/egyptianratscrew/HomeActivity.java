package com.example.android.egyptianratscrew;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class HomeActivity extends AppCompatActivity {

    private ImageView playImage;
    private ImageView slapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        playImage = (ImageView) findViewById(R.id.play_iv);
        slapImage = (ImageView) findViewById(R.id.slap_iv);

        Glide.with(this).load(R.drawable.play_icon).into(playImage);
        Glide.with(this).load(R.drawable.hand_icon).into(slapImage);
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
