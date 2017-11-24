package com.example.android.egyptianratscrew;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private ImageView playerOneImage, playerTwoImage, nextCardImage;

    // indicates how many cards left in pile
    private TextView numCardsPlayerOne, numCardsPlayerTwo, numCardsCenter;

    // burn/slap/reset text view
    private TextView burnSlap;

    // play indicators (whose turn it is)
    private ImageView p1PlayIndicator, p2PlayIndicator;

    private Button p1PlayButton, p2PlayButton, p1SlapButton, p2SlapButton;

    private ViewGroup rootView;

    private SharedPreferences sp;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();

        // initialize the images for the piles
        playerOneImage = (ImageView) findViewById(R.id.player_one_card);
        playerTwoImage = (ImageView) findViewById(R.id.player_two_card);
        nextCardImage = (ImageView) findViewById(R.id.middle_card);

        // initialize text views that show number of cards in the piles
        numCardsPlayerOne = (TextView) findViewById(R.id.num_cards_player_one);
        numCardsPlayerTwo = (TextView) findViewById(R.id.num_cards_player_two);
        numCardsCenter = (TextView) findViewById(R.id.num_cards_middle);

        // play indicators
        p1PlayIndicator = (ImageView) findViewById(R.id.p1_play_indicator);
        p2PlayIndicator = (ImageView) findViewById(R.id.p2_play_indicator);
        p2PlayIndicator.setVisibility(View.INVISIBLE);

        // initialize burn/slap text view
        burnSlap = (TextView) findViewById(R.id.burn_slap);

        // initialize buttons
        p1PlayButton = (Button) findViewById(R.id.p1_play);
        p2PlayButton = (Button) findViewById(R.id.p2_play);
        p1SlapButton = (Button) findViewById(R.id.p1_slap);
        p2SlapButton = (Button) findViewById(R.id.p2_slap);

        rootView = (ViewGroup) findViewById(R.id.rootLayout);

        sp = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        boolean newGame = getIntent().getBooleanExtra("newGame", true);
        if (newGame) {
            resetGame();
            burnSlap.setText("New Game");
        } else {
            loadData();
        }
    }

    public void playerOneSlap(View view) {

        // if the slap was correct
        if (middlePile.shouldSlap()) {

            // p1 turn
            p1PlayIndicator.setVisibility(View.VISIBLE);
            p2PlayIndicator.setVisibility(View.INVISIBLE);

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
            burnSlap.setText("P1 SLAPS");
        }

        // if the slap was incorrect
        else {
            // burn player one's next card
            middlePile.burn(playerOne.playTopCard());
            moveCardToCenterBurn(playerOneImage, true);

            // p1 runs out of cards - player two wins
            if (playerOne.getSize() == 0) {
                endGame("Player Two Wins!");
            } else {
                burnSlap.setText("P1 BURN");
            }
        }
        updateNumCards();
    }

    public void playerTwoSlap(View view) {
        // if the slap was correct
        if (middlePile.shouldSlap()) {

            // p2 turn
            p1PlayIndicator.setVisibility(View.INVISIBLE);
            p2PlayIndicator.setVisibility(View.VISIBLE);

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
            burnSlap.setText("P2 SLAPS");
        }

        // if the slap was incorrect
        else {

            // burn player two's next card
            middlePile.burn(playerTwo.playTopCard());
            moveCardToCenterBurn(playerTwoImage, false);

            // p2 runs out of cards - player one wins
            if (playerTwo.getSize() == 0) {
                endGame("Player One Wins!");
            } else {
                burnSlap.setText("P2 BURN");
            }
        }
        updateNumCards();
    }

    public void playerOnePlay(View view) {

        burnSlap.setText("");

        // player one can only play when it's his/her turn
        if (playerOnePlays != 0) {

            // player two wins
            if (playerOne.getSize() <= 1) {
                endGame("Player Two Wins!");
            }

            Card nextCard = playerOne.playTopCard();
            middlePile.addCard(nextCard);
            final int resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());

            moveCardToCenter(playerOneImage, resID, true);

            // checks if the card is a face card
            if (nextCard.isFaceCard()) {

                // change play indicator
                p1PlayIndicator.setVisibility(View.INVISIBLE);
                p2PlayIndicator.setVisibility(View.VISIBLE);

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
                p1PlayIndicator.setVisibility(View.INVISIBLE);
                p2PlayIndicator.setVisibility(View.VISIBLE);

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
        }
    }

    public void playerTwoPlay(View view) {

        burnSlap.setText("");

        // player two can only play when it's his/her turn
        if (playerTwoPlays != 0) {

            // player one wins
            if (playerTwo.getSize() <= 1) {
                endGame("Player One Wins!");
            }

            Card nextCard = playerTwo.playTopCard();
            middlePile.addCard(nextCard);
            final int resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());
            moveCardToCenter(playerTwoImage, resID, false);

            // checks if the card is a face card
            if (nextCard.isFaceCard()) {

                // change play indicator
                p2PlayIndicator.setVisibility(View.INVISIBLE);
                p1PlayIndicator.setVisibility(View.VISIBLE);

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
                p2PlayIndicator.setVisibility(View.INVISIBLE);
                p1PlayIndicator.setVisibility(View.VISIBLE);

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
        }
    }

    public void reset(View view) {
        // todo add animation
        resetGame();
    }

    public void home(View view) {
        saveData();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void moveCardToCenter(final View view, final int imageId, boolean playerOne) {

        view.bringToFront();

        int centerX = rootView.getWidth() / 2;

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        int startX = originalPos[0];

        int cardWidth = view.getWidth() / 2;

        int deltaX;
        if (playerOne) {
            deltaX = centerX - startX - cardWidth;
        } else {
            deltaX = centerX - startX + cardWidth;
        }

        TranslateAnimation anim = new TranslateAnimation(0, deltaX, 0, 0);
        anim.setDuration(150);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                disableButtons();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setImageResource(imageId);
                nextCardImage.bringToFront();
                enableButtons();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private void moveCardToCenterBurn(View view, final boolean playerOne) {

        int centerX = rootView.getWidth() / 2;

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        int startX = originalPos[0];

        int cardWidth = view.getMeasuredWidth() / 2;

        int deltaX;
        if (playerOne) {
            deltaX = centerX - startX - cardWidth;
        } else {
            deltaX = centerX - startX + cardWidth;
        }

        TranslateAnimation anim = new TranslateAnimation(0, deltaX, 0, 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                nextCardImage.bringToFront();
                // nothing in the pile
                if (middlePile.getSize() == 1) {
                    nextCardImage.setVisibility(View.GONE);
                }
                disableButtons();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setVisibility(View.VISIBLE);
                if (middlePile.getSize() == 1) {
                    if (playerOne) {
                        nextCardImage.setImageResource(R.drawable.player1card);
                    } else {
                        nextCardImage.setImageResource(R.drawable.player2card);
                    }
                }
                enableButtons();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private void moveCardFromCenter(final boolean playerOne) {

        int cardWidth = nextCardImage.getWidth() / 2;

        int startX = rootView.getWidth() / 2;

        int endPos[] = new int[2];
        int deltaX;
        if (playerOne) {
            playerOneImage.getLocationOnScreen(endPos);
            deltaX = endPos[0] - cardWidth - startX;
        } else {
            playerTwoImage.getLocationOnScreen(endPos);
            deltaX = endPos[0] + cardWidth - startX;
        }

        TranslateAnimation anim = new TranslateAnimation(0, deltaX, 0, 0);
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
                nextCardImage.setImageResource(R.drawable.rat);
                enableButtons();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        nextCardImage.startAnimation(anim);
    }

    private void endGame(String msg) {
        disableButtons();
        burnSlap.setText(msg);
    }

    private void disableButtons() {
        p1PlayButton.setClickable(false);
        p2PlayButton.setClickable(false);
        p1SlapButton.setClickable(false);
        p2SlapButton.setClickable(false);

    }

    private void enableButtons() {
        p1PlayButton.setClickable(true);
        p2PlayButton.setClickable(true);
        p1SlapButton.setClickable(true);
        p2SlapButton.setClickable(true);
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
        editor.putString("p1Deck", p1Json);
        editor.putString("p2Deck", p2Json);
        editor.putString("midDeck", midJson);
        editor.apply();
    }

    private void loadData() {

        String p1Json = sp.getString("p1Deck", null);
        String p2Json = sp.getString("p2Deck", null);
        String midJson = sp.getString("midDeck", null);

        // json cannot be retrieved from shared preferences
        if (p1Json == null || p2Json == null || midJson == null) {
            Log.i("MainActivity", "Cannot get json from shared preferences");

            initializeDecks();
            return;
        }

        playerOne = gson.fromJson(p1Json, PlayerDeck.class);
        playerTwo = gson.fromJson(p2Json, PlayerDeck.class);
        middlePile = gson.fromJson(midJson, MiddlePile.class);

        updateNumCards();
    }

    private void updateNumCards() {
        numCardsPlayerOne.setText(Integer.toString(playerOne.getSize()));
        numCardsPlayerTwo.setText(Integer.toString(playerTwo.getSize()));
        numCardsCenter.setText(Integer.toString(middlePile.getSize()));
    }

    private void resetGame() {
        initializeDecks();
        p1PlayIndicator.setVisibility(View.VISIBLE);
        p2PlayIndicator.setVisibility(View.INVISIBLE);
        updateNumCards();
        playerOnePlays = 1;
        playerTwoPlays = 0;
        isFCPlayerOne = false;
        isFCPlayerTwo = false;
        burnSlap.setText("RESET");
        nextCardImage.setImageResource(R.drawable.rat);
        enableButtons();
    }
}