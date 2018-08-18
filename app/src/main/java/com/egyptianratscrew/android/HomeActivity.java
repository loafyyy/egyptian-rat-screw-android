package com.egyptianratscrew.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.egyptianratscrew.android.egyptianratscrew.R;

public class HomeActivity extends AppCompatActivity {

    private Button newGameButton, onePlayerButton, twoPlayerButton;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;
        onePlayerButton = findViewById(R.id.one_player_button);
        onePlayerButton.setVisibility(View.GONE);
        onePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(getResources().getString(R.string.new_game_preference), true);
                // one player mode
                intent.putExtra(getResources().getString(R.string.game_mode_preference), true);
                startActivity(intent);
            }
        });
        twoPlayerButton = findViewById(R.id.two_player_button);
        twoPlayerButton.setVisibility(View.GONE);
        twoPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(getResources().getString(R.string.new_game_preference), true);
                // two player mode
                intent.putExtra(getResources().getString(R.string.game_mode_preference), false);
                startActivity(intent);
            }
        });

        newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onePlayerButton.setVisibility(View.VISIBLE);
                twoPlayerButton.setVisibility(View.VISIBLE);
                newGameButton.setBackgroundResource(R.drawable.home_button_selected);
            }
        });
    }

    public void continueGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(getResources().getString(R.string.new_game_preference), false);
        startActivity(intent);
    }


    public void settings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}
