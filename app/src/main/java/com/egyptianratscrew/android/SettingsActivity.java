package com.egyptianratscrew.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.egyptianratscrew.android.egyptianratscrew.R;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

public class SettingsActivity extends AppCompatActivity {

    private Spinner dealSpeedSpinner;
    private Spinner computerDifficultySpinner;
    private SharedPreferences sp;
    private Context mContext;

    public static final int SLOW_DEAL = 2000;
    public static final int MED_DEAL = 1250;
    public static final int FAST_DEAL = 1100;

    public static final int EASY_COMP = 3000;
    public static final int MED_COMP = 1000;
    public static final int HARD_COMP = 800;
    public static final int IMPOSSIBLE_COMP = 500;

    private int lastDealSpeed;
    private int lastComputerSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sp = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mContext = this;

        dealSpeedSpinner = findViewById(R.id.deal_speed_spinner);
        computerDifficultySpinner = findViewById(R.id.computer_difficulty_spinner);

        int dealSpeedPref = sp.getInt(getString(R.string.deal_speed_preference), SLOW_DEAL);
        lastDealSpeed = dealSpeedPref;
        switch (dealSpeedPref) {
            case SLOW_DEAL:
                dealSpeedSpinner.setSelection(0);
                break;
            case MED_DEAL:
                dealSpeedSpinner.setSelection(1);
                break;
            case FAST_DEAL:
                dealSpeedSpinner.setSelection(2);
                break;
        }

        int compDiffPref = sp.getInt(getString(R.string.difficulty_preference), EASY_COMP);
        lastComputerSpeed = compDiffPref;
        switch (compDiffPref) {
            case EASY_COMP:
                computerDifficultySpinner.setSelection(0);
                break;
            case MED_COMP:
                computerDifficultySpinner.setSelection(1);
                break;
            case HARD_COMP:
                computerDifficultySpinner.setSelection(2);
                break;
            case IMPOSSIBLE_COMP:
                computerDifficultySpinner.setSelection(3);
                break;
        }

        dealSpeedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int dealSpeed;
                switch (i) {
                    case 0:
                        dealSpeed = SLOW_DEAL;
                        break;
                    case 1:
                        dealSpeed = MED_DEAL;
                        break;
                    case 2:
                        dealSpeed = FAST_DEAL;
                        break;
                    default:
                        dealSpeed = SLOW_DEAL;
                        break;
                }
                sp.edit().putInt(getString(R.string.deal_speed_preference), dealSpeed).apply();

                if (dealSpeed != lastDealSpeed) {
                    Toast.makeText(mContext, "Deal speed set to " + dealSpeed + " ms/card", Toast.LENGTH_SHORT).show();
                    lastDealSpeed = dealSpeed;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        computerDifficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int computerSpeed;
                String computerDifficulty = "";
                switch (i) {
                    case 0:
                        computerSpeed = EASY_COMP;
                        computerDifficulty = "easy";
                        break;
                    case 1:
                        computerSpeed = MED_COMP;
                        computerDifficulty = "medium";
                        break;
                    case 2:
                        computerSpeed = HARD_COMP;
                        computerDifficulty = "hard";
                        break;
                    case 3:
                        computerSpeed = IMPOSSIBLE_COMP;
                        computerDifficulty = "impossible";
                        break;
                    default:
                        computerSpeed = EASY_COMP;
                        computerDifficulty = "easy";
                        break;
                }
                sp.edit().putInt(getString(R.string.difficulty_preference), computerSpeed).apply();

                if (computerSpeed != lastComputerSpeed) {
                    Toast.makeText(mContext, "Computer difficulty set to " + computerDifficulty, Toast.LENGTH_SHORT).show();
                    lastComputerSpeed = computerSpeed;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void about(View view) {
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
    }

    public void home(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
