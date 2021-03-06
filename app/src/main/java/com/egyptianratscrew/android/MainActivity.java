package com.egyptianratscrew.android;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.egyptianratscrew.android.egyptianratscrew.R;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    // how many cards does each player have to play
    // start with player one
    private int playerOnePlays = 1;
    private int playerTwoPlays = 0;

    // determines who played the last face card
    private boolean isFCPlayerOne = false;
    private boolean isFCPlayerTwo = false;

    // Decks
    private PlayerDeck playerOne, playerTwo;
    private MiddlePile middlePile;

    // images for the different piles
    private ImageView playerOneImage, playerTwoImage, nextCardImage, middleIcon;
    // middle card image ID - used for saving
    private int resID;

    // indicates how many cards left in pile
    private TextView numCardsPlayerOne, numCardsPlayerTwo, numCardsCenter;

    // burn/slap/reset text view
    private TextView burnSlap;

    // play indicators (whose turn it is)
    private int playIndicatorOn = R.drawable.card_edge_glow;
    private int playIndicatorOff = R.drawable.card;

    private ImageButton p1PlayButton, p2PlayButton, p1SlapButton, p2SlapButton;
    private Button helpButton;

    private ViewGroup rootView;

    private SharedPreferences sp;
    private Gson gson;

    private Context mContext;
    private boolean gameEnd;

    private boolean onePlayer;

    // timer for computer for 1 player mode
    private CountDownTimer slapTimer = null;
    private int slapTime;
    private CountDownTimer playTimer = null;
    private int playTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        gson = new Gson();
        mContext = this;
        gameEnd = false;
        slapTime = sp.getInt(getString(R.string.difficulty_preference), SettingsActivity.EASY_COMP);
        playTime = sp.getInt(getString(R.string.deal_speed_preference), SettingsActivity.SLOW_DEAL);

        // VIEWS
        rootView = (ViewGroup) findViewById(R.id.rootLayout);

        // initialize the images for the piles
        playerOneImage = (ImageView) findViewById(R.id.player_one_card);
        playerTwoImage = (ImageView) findViewById(R.id.player_two_card);
        nextCardImage = (ImageView) findViewById(R.id.middle_card);
        middleIcon = (ImageView) findViewById(R.id.middle_icon);
        middleIcon.setRotation(0);
        middleIcon.setImageDrawable(null);

        // initialize text views that show number of cards in the piles
        numCardsPlayerOne = (TextView) findViewById(R.id.num_cards_player_one);
        numCardsPlayerTwo = (TextView) findViewById(R.id.num_cards_player_two);
        numCardsCenter = (TextView) findViewById(R.id.num_cards_middle);

        // initialize burn/slap text view
        burnSlap = (TextView) findViewById(R.id.burn_slap);
        burnSlap.setRotation(0);

        // initialize buttons
        p1PlayButton = (ImageButton) findViewById(R.id.p1_play);
        p1PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerOnePlay();
            }
        });
        p2PlayButton = (ImageButton) findViewById(R.id.p2_play);
        p2PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerTwoPlay();
            }
        });
        p1SlapButton = (ImageButton) findViewById(R.id.p1_slap);
        p1SlapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerOneSlap();
            }
        });
        p2SlapButton = (ImageButton) findViewById(R.id.p2_slap);
        p2SlapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerTwoSlap();
            }
        });

        helpButton = findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(mContext);
                dialog.setTitle("Instructions");
                dialog.setContentView(R.layout.instructions);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialog_ok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        // one player settings
        onePlayer = getIntent().getBooleanExtra(getString(R.string.game_mode_preference), false);

        boolean newGame = getIntent().getBooleanExtra(getString(R.string.new_game_preference), true);
        if (newGame) {
            resetGame();
        } else {
            loadData();
        }

        if (onePlayer) {
            slapTimer = new CountDownTimer(slapTime, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    playerTwoSlap();
                }
            };

            playTimer = new CountDownTimer(playTime, 1000) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    playerTwoPlay();
                }
            };

            // get rid of player two play and slap buttons
            p2PlayButton.setVisibility(View.GONE);
            p2SlapButton.setVisibility(View.GONE);
        }
    }

    public void homeButton(View view) {
        startActivity(new Intent(mContext, HomeActivity.class));
    }

    public void resetButton(View view) {
        burnSlap.bringToFront();
        burnSlap.setRotation(0);
        burnSlap.setText(getString(R.string.reset));
        resetGame();
    }

    public void saveButton(View view) {
        burnSlap.bringToFront();
        burnSlap.setRotation(0);
        burnSlap.setText(getString(R.string.save));
        saveData();
    }

    public void playerOneSlap() {

        if (onePlayer) {
            slapTimer.cancel();
        }

        burnSlap.setText("");
        middleIcon.setImageDrawable(null);

        // if the slap was correct
        if (middlePile.shouldSlap()) {

            // p1 turn
            playerOneImage.setImageResource(playIndicatorOn);
            playerTwoImage.setImageResource(playIndicatorOff);

            // update shouldSlap so player two cannot slap
            middlePile.shouldntSlap();

            // update FC booleans
            isFCPlayerOne = false;
            isFCPlayerTwo = false;

            // player one starts again
            playerOnePlays = 1;
            playerTwoPlays = 0;

            // add middle pile to player one
            middlePile.emptyPile(playerOne);
            moveCardFromCenter(true);
            middleIcon.bringToFront();
            middleIcon.setRotation(0);
            middleIcon.setImageResource(R.drawable.hand_icon);
        }

        // if the slap was incorrect
        else {
            // burn player one's next card
            middlePile.burn(playerOne.playTopCard());
            moveCardToCenterBurn(playerOneImage, true);

            // p1 runs out of cards - player two wins
            if (playerOne.getSize() == 0) {
                burnSlap.setRotation(180);
                endGame(getString(R.string.p2_wins_message));
            }
        }
        updateNumCards();
    }

    public void playerTwoSlap() {

        burnSlap.setText("");
        middleIcon.setImageDrawable(null);

        // if the slap was correct
        if (middlePile.shouldSlap()) {

            // p2 turn
            playerOneImage.setImageResource(playIndicatorOff);
            playerTwoImage.setImageResource(playIndicatorOn);

            // update shouldSlap so player one cannot slap again
            middlePile.shouldntSlap();

            // update FC booleans
            isFCPlayerOne = false;
            isFCPlayerTwo = false;

            // player two starts again
            playerOnePlays = 0;
            playerTwoPlays = 1;

            // add middle pile to player two
            middlePile.emptyPile(playerTwo);
            moveCardFromCenter(false);
            middleIcon.bringToFront();
            middleIcon.setRotation(180);
            middleIcon.setImageResource(R.drawable.hand_icon);
        }

        // if the slap was incorrect
        else {
            // burn player two's next card
            middlePile.burn(playerTwo.playTopCard());
            moveCardToCenterBurn(playerTwoImage, false);

            // p2 runs out of cards - player one wins
            if (playerTwo.getSize() == 0) {
                burnSlap.setRotation(0);
                endGame(getString(R.string.p1_wins_message));
            }
        }
        updateNumCards();

        if (onePlayer) {
            if (playerTwoPlays != 0) {
                playTimer.start();
            }
        }
    }

    public void playerOnePlay() {

        burnSlap.setText("");
        middleIcon.setImageDrawable(null);

        // player one can only play when it's his/her turn
        if (playerOnePlays != 0) {

            Card nextCard = playerOne.playTopCard();
            middlePile.addCard(nextCard);
            resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());

            moveCardToCenter(playerOneImage, resID, true);

            // checks if the card is a face card
            if (nextCard.isFaceCard()) {

                // change play indicator
                playerOneImage.setImageResource(playIndicatorOff);
                playerTwoImage.setImageResource(playIndicatorOn);

                // if it is, then it means
                // player one played the last face card
                isFCPlayerOne = true;
                isFCPlayerTwo = false;

                // play transfers to player two
                int num = nextCard.getNumber();
                // ace
                if (num == 1) {
                    playerTwoPlays += 4;
                } else {
                    playerTwoPlays += num - 10;
                }
                playerOnePlays = 0;
            }

            // if player one still has plays
            else if (playerOnePlays > 1) {
                playerOnePlays -= 1;
            }

            // if player one has no more plays,
            // play transfers to player two
            else {

                // change play indicator
                playerOneImage.setImageResource(playIndicatorOff);
                playerTwoImage.setImageResource(playIndicatorOn);

                playerOnePlays -= 1;
                playerTwoPlays += 1;

                if (isFCPlayerTwo) {

                    // add deck cards to player two
                    middlePile.emptyPile(playerTwo);
                    moveCardFromCenter(false);

                    // update boolean
                    isFCPlayerTwo = false;
                    middlePile.shouldntSlap();
                }
            }
            // update text field that display deck sizes
            updateNumCards();

            // player two wins
            if (playerOne.getSize() == 0) {
                burnSlap.setRotation(180);
                endGame(getString(R.string.p2_wins_message));
            }

            else if (onePlayer) {
                if (middlePile.shouldSlap()) {
                    slapTimer.start();
                }

                else if (playerTwoPlays != 0) {
                    playTimer.start();
                }
            }
        }
    }

    public void playerTwoPlay() {

        burnSlap.setText("");
        middleIcon.setImageDrawable(null);

        // player two can only play when it's his/her turn
        if (playerTwoPlays != 0) {

            Card nextCard = playerTwo.playTopCard();
            middlePile.addCard(nextCard);
            resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());
            moveCardToCenter(playerTwoImage, resID, false);

            // checks if the card is a face card
            if (nextCard.isFaceCard()) {

                // change play indicator
                playerOneImage.setImageResource(playIndicatorOn);
                playerTwoImage.setImageResource(playIndicatorOff);

                // if it is, then it means
                // player two played the last face card
                isFCPlayerTwo = true;
                isFCPlayerOne = false;

                // play transfers to player one
                int num = nextCard.getNumber();
                // ace
                if (num == 1) {
                    playerOnePlays += 4;
                } else {
                    playerOnePlays += num - 10;
                }
                playerTwoPlays = 0;
            }

            // if player two still has plays
            else if (playerTwoPlays > 1) {
                playerTwoPlays -= 1;
            }

            // if player two has no more plays,
            // play transfers to player one
            else {

                // change play indicator
                playerOneImage.setImageResource(playIndicatorOn);
                playerTwoImage.setImageResource(playIndicatorOff);

                playerTwoPlays -= 1;
                playerOnePlays += 1;

                if (isFCPlayerOne) {

                    // add deck cards to player one
                    middlePile.emptyPile(playerOne);
                    moveCardFromCenter(true);

                    // update boolean
                    isFCPlayerOne = false;
                    middlePile.shouldntSlap();
                }
            }
            // update text field that display deck sizes
            updateNumCards();

            // player one wins
            if (playerTwo.getSize() == 0) {
                burnSlap.setRotation(0);
                endGame(getString(R.string.p1_wins_message));
            }

            else if (onePlayer) {
                if (middlePile.shouldSlap()) {
                    slapTimer.start();
                } else if (playerTwoPlays != 0) {
                    playTimer.start();
                }
            }
        }
    }

    private void moveCardToCenter(final View view, final int imageId, boolean playerOne) {

        view.bringToFront();

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - rootView.getMeasuredHeight();

        int centerY = rootView.getHeight() / 2;

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        int startY = originalPos[1];

        int cardHeight = view.getHeight() / 2;

        int deltaY;
        if (playerOne) {
            deltaY = centerY - startY - cardHeight + statusBarOffset;
        } else {
            deltaY = centerY - startY + cardHeight - statusBarOffset;
        }

        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, deltaY);
        anim.setDuration(150);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                disableButtons();
                if (gameEnd) {
                    burnSlap.bringToFront();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setImageResource(imageId);
                nextCardImage.setVisibility(View.VISIBLE);
                nextCardImage.bringToFront();
                enableButtons();
                numCardsPlayerOne.bringToFront();
                numCardsPlayerTwo.bringToFront();
                if (gameEnd) {
                    burnSlap.bringToFront();
                    nextCardImage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private void moveCardToCenterBurn(View view, final boolean playerOne) {

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - rootView.getMeasuredHeight();

        int centerY = rootView.getHeight() / 2;

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        int startY = originalPos[1];

        int cardHeight = view.getMeasuredHeight() / 2;

        int deltaY;
        if (playerOne) {
            deltaY = centerY - startY - cardHeight + statusBarOffset;
        } else {
            deltaY = centerY - startY + cardHeight - statusBarOffset;
        }

        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, deltaY);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if (!gameEnd) {
                    nextCardImage.bringToFront();
                } else {
                    burnSlap.bringToFront();
                }
                // nothing in the pile
                if (middlePile.getSize() == 1) {
                    nextCardImage.setVisibility(View.INVISIBLE);
                }
                disableButtons();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setVisibility(View.VISIBLE);
                if (middlePile.getSize() == 1) {
                    if (playerOne) {
                        nextCardImage.setImageResource(R.drawable.card);
                    } else {
                        nextCardImage.setImageResource(R.drawable.card);
                    }
                }
                enableButtons();
                middleIcon.bringToFront();
                middleIcon.setImageResource(R.drawable.burn_icon);

                if (gameEnd) {
                    nextCardImage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private void moveCardFromCenter(final boolean playerOne) {

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int statusBarOffset = dm.heightPixels - rootView.getMeasuredHeight();

        int cardHeight = nextCardImage.getHeight() / 2;
        int startY = rootView.getHeight() / 2;

        int endPos[] = new int[2];
        int deltaY;
        if (playerOne) {
            playerOneImage.getLocationOnScreen(endPos);
            deltaY = endPos[1] - cardHeight - startY + statusBarOffset;
        } else {
            playerTwoImage.getLocationOnScreen(endPos);
            deltaY = endPos[1] + cardHeight - startY - statusBarOffset;
        }

        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, deltaY);
        anim.setDuration(300);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (playerOne) {
                    playerOneImage.bringToFront();
                } else {
                    playerTwoImage.bringToFront();
                }
                disableButtons();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setVisibility(View.INVISIBLE);
                enableButtons();
                numCardsPlayerOne.bringToFront();
                numCardsPlayerTwo.bringToFront();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        nextCardImage.startAnimation(anim);
    }

    private void endGame(String msg) {
        middleIcon.setVisibility(View.INVISIBLE);
        gameEnd = true;
        disableButtons();
        updateNumCards();

        if (onePlayer) {
            burnSlap.setRotation(0);
            if (playerOne.getSize() == 0) {
                burnSlap.setText(getString(R.string.p1_loses_message));
            }
            else {
                burnSlap.setText(R.string.p1_wins_message);
            }
        }
        else {
            burnSlap.setText(msg);
        }
    }

    private void disableButtons() {
        p1PlayButton.setClickable(false);
        p2PlayButton.setClickable(false);
        p1SlapButton.setClickable(false);
        p2SlapButton.setClickable(false);
    }

    private void enableButtons() {
        if (!gameEnd) {
            p1PlayButton.setClickable(true);
            p2PlayButton.setClickable(true);
            p1SlapButton.setClickable(true);
            p2SlapButton.setClickable(true);
        }
    }

    private void initializeDecks() {

        // initialize a deck of cards and shuffle
        Deck startingDeck = new Deck();
        startingDeck.initializeDeck();
        startingDeck.shuffleDeck();

        // create the hands/decks for two players
        playerOne = new PlayerDeck();
        playerTwo = new PlayerDeck();

        // distribute cards to player one
        for (int i = 0; i < 26; i++) {
            playerOne.addCard(startingDeck.getNextCard());
        }

        // distribute cards to player two
        for (int i = 0; i < 26; i++) {
            playerTwo.addCard(startingDeck.getNextCard());
        }

        // initialize middle pile
        middlePile = new MiddlePile();
        updateNumCards();
    }

    private void saveData() {
        SharedPreferences.Editor editor = sp.edit();
        String p1Json = gson.toJson(playerOne);
        String p2Json = gson.toJson(playerTwo);
        String midJson = gson.toJson(middlePile);

        // put one player or two player
        if (onePlayer) {
            editor.putBoolean(getString(R.string.game_mode_save_preference), true);
        } else {
            editor.putBoolean(getString(R.string.game_mode_save_preference), false);
        }

        editor.putString(getString(R.string.p1_save_preference), p1Json);
        editor.putString(getString(R.string.p2_save_preference), p2Json);
        editor.putString(getString(R.string.mid_save_preference), midJson);
        editor.putInt(getString(R.string.p1_num_plays_preference), playerOnePlays);
        editor.putInt(getString(R.string.p2_num_plays_preference), playerTwoPlays);
        editor.putBoolean(getString(R.string.p1_face_card_preference), isFCPlayerOne);
        editor.putBoolean(getString(R.string.p2_face_card_preference), isFCPlayerTwo);

        if (middlePile.getSize() > 0) {
            editor.putInt(getString(R.string.mid_card_img_id_preference), resID);
        } else {
            editor.putInt(getString(R.string.mid_card_img_id_preference), -1);
        }

        editor.apply();
    }

    private void loadData() {
        middleIcon.setVisibility(View.VISIBLE);

        String p1Json = sp.getString(getString(R.string.p1_save_preference), null);
        String p2Json = sp.getString(getString(R.string.p2_save_preference), null);
        String midJson = sp.getString(getString(R.string.mid_save_preference), null);

        // json cannot be retrieved from shared preferences
        if (p1Json == null || p2Json == null || midJson == null) {
            resetGame();
            return;
        }

        // get one player or two player mode
        onePlayer = sp.getBoolean(getString(R.string.game_mode_save_preference), true);

        playerOne = gson.fromJson(p1Json, PlayerDeck.class);
        playerTwo = gson.fromJson(p2Json, PlayerDeck.class);
        middlePile = gson.fromJson(midJson, MiddlePile.class);
        playerOnePlays = sp.getInt(getString(R.string.p1_num_plays_preference), 1);
        playerTwoPlays = sp.getInt(getString(R.string.p2_num_plays_preference), 0);
        isFCPlayerOne = sp.getBoolean(getString(R.string.p1_face_card_preference), false);
        isFCPlayerTwo = sp.getBoolean(getString(R.string.p2_face_card_preference), false);

        // set play indicator
        if (playerOnePlays != 0) {
            // play indicators
            playerOneImage.setImageResource(playIndicatorOn);
            playerTwoImage.setImageResource(playIndicatorOff);
        } else {
            // play indicators
            playerOneImage.setImageResource(playIndicatorOff);
            playerTwoImage.setImageResource(playIndicatorOn);
        }

        // set middle card image
        int id = sp.getInt(getString(R.string.mid_card_img_id_preference), -1);
        if (id != -1) {
            nextCardImage.setVisibility(View.VISIBLE);
            nextCardImage.setImageResource(id);
        } else {
            nextCardImage.setVisibility(View.INVISIBLE);
        }
        updateNumCards();

        // if was saved at end of game
        int p1Size = playerOne.getSize();
        int p2Size = playerTwo.getSize();
        if (p1Size == 0 || p2Size == 0) {
            nextCardImage.setVisibility(View.INVISIBLE);
            // p2 wins
            if (p1Size == 0) {
                burnSlap.setRotation(180);
                endGame(getString(R.string.p2_wins_message));
            }
            // p1 wins
            else {
                endGame(getString(R.string.p1_wins_message));
            }
        }
    }

    private void updateNumCards() {
        numCardsPlayerOne.setText(Integer.toString(playerOne.getSize()));
        numCardsPlayerTwo.setText(Integer.toString(playerTwo.getSize()));
        numCardsCenter.setText(Integer.toString(middlePile.getSize()));
    }

    private void resetGame() {
        middleIcon.setImageDrawable(null);
        middleIcon.setVisibility(View.VISIBLE);
        gameEnd = false;
        initializeDecks();
        playerOneImage.setImageResource(playIndicatorOn);
        playerTwoImage.setImageResource(playIndicatorOff);
        updateNumCards();
        playerOnePlays = 1;
        playerTwoPlays = 0;
        isFCPlayerOne = false;
        isFCPlayerTwo = false;
        nextCardImage.setVisibility(View.INVISIBLE);
        enableButtons();
    }
}