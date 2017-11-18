package com.example.android.egyptianratscrew;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // how many cards does each player have to play
    // start with player one
    private int playerOnePlays = 1;
    private int playerTwoPlays = 0;

    // determines who played the last face card
    private boolean isFCPlayerOne = false;
    private boolean isFCPlayerTwo = false;

    // player decks
    private PlayerDeck playerOne;
    private PlayerDeck playerTwo;

    // middle pile
    private MiddlePile middlePile;

    // images for the different piles
    private ImageView playerOneImage;
    private ImageView playerTwoImage;
    private ImageView nextCardImage;

    // player one num of cards
    private TextView numCardsPlayerOne;
    private TextView numCardsPlayerTwo;
    // TextView numCardsCenter;

    // burn/slap text view
    private TextView burnSlap;

    // play indicators
    private ImageView p1PlayIndicator;
    private ImageView p2PlayIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // initialize the images for the piles
        playerOneImage = (ImageView) findViewById(R.id.player_one_card);
        playerTwoImage = (ImageView) findViewById(R.id.player_two_card);
        nextCardImage = (ImageView) findViewById(R.id.middle_card);

        // initialize text views that show number of cards in the piles
        numCardsPlayerOne = (TextView) findViewById(R.id.num_cards_player_one);
        numCardsPlayerTwo = (TextView) findViewById(R.id.num_cards_player_two);
        // numCardsCenter = (TextView) findViewById(R.id.num_cards_middle);

        // play indicators
        p1PlayIndicator = (ImageView) findViewById(R.id.p1_play_indicator);
        p2PlayIndicator = (ImageView) findViewById(R.id.p2_play_indicator);
        p2PlayIndicator.setVisibility(View.INVISIBLE);

        // initialize burn/slap text view
        burnSlap = (TextView) findViewById(R.id.burn_slap);
    }

    public void playerOneSlap(View view) {
        // if the slap was correct
        if (middlePile.shouldSlap()) {

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
            burnSlap.setText("P1 BURN");
        }

        // update text field that display deck sizes
        numCardsPlayerOne.setText("" + playerOne.getSize());
        numCardsPlayerTwo.setText("" + playerTwo.getSize());
        // numCardsCenter.setText("" + middlePile.getSize());
    }

    public void playerTwoSlap(View view) {
        // if the slap was correct
        if (middlePile.shouldSlap()) {

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
            burnSlap.setText("P2 BURN");
        }

        // update text field that display deck sizes
        numCardsPlayerOne.setText("" + playerOne.getSize());
        numCardsPlayerTwo.setText("" + playerTwo.getSize());
        // numCardsCenter.setText("" + middlePile.getSize());
    }

    public void playerOnePlay(View view) {
        // player one can only play when it's his/her turn
        if (playerOne.getSize() <= 0) {

        }

        if (playerOnePlays != 0) {

            Card nextCard = playerOne.playTopCard();
            middlePile.addCard(nextCard);
            int resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());
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
                }
                else {
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

                }
            }

            // update text field that display deck sizes
            numCardsPlayerOne.setText("" + playerOne.getSize());
            numCardsPlayerTwo.setText("" + playerTwo.getSize());
            // numCardsCenter.setText("" + middlePile.getSize());
        }
    }

    public void playerTwoPlay(View view) {

        if (playerTwo.getSize() <= 0) {

        }

        // player two can only play when it's his/her turn
        if (playerTwoPlays != 0) {

            Card nextCard = playerTwo.playTopCard();
            middlePile.addCard(nextCard);
            int resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());
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
                }
                else {
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
                }
            }

            // update text field that display deck sizes
            numCardsPlayerOne.setText("" + playerOne.getSize());
            numCardsPlayerTwo.setText("" + playerTwo.getSize());
            // numCardsCenter.setText("" + middlePile.getSize());
        }
    }

    private void moveCardToCenter(final View view, final int imageId, boolean playerOne) {

        Log.i("toCenter", "toCenter");

        view.bringToFront();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int xDest = dm.widthPixels / 2;

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        int start = originalPos[0];

        int cardWidth = view.getMeasuredWidth() / 2;

        int end;
        if (playerOne) {
            end = xDest - start - cardWidth;
        } else {
            end = xDest - start + cardWidth;
        }

        TranslateAnimation anim = new TranslateAnimation(0, end, 0, 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setImageResource(imageId);
                nextCardImage.bringToFront();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private void moveCardToCenterBurn(View view, final boolean playerOne) {

        Log.i("Burn", "Burn");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int xDest = dm.widthPixels / 2;

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);
        int start = originalPos[0];

        int cardWidth = view.getMeasuredWidth() / 2;

        int end;
        if (playerOne) {
            end = xDest - start - cardWidth;
        } else {
            end = xDest - start + cardWidth;
        }

        TranslateAnimation anim = new TranslateAnimation(0, end, 0, 0);
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                nextCardImage.bringToFront();
                // nothing in the pile
                if  (middlePile.getSize() == 1) {
                    nextCardImage.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setVisibility(View.VISIBLE);
                if  (middlePile.getSize() == 1) {
                    if (playerOne) {
                        nextCardImage.setImageResource(R.drawable.player1card);
                    }
                    else {
                        nextCardImage.setImageResource(R.drawable.player2card);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

    private void moveCardFromCenter(final boolean playerOne) {

        Log.i("moveCardFromCenter", "moveCardFromCenter");

        DisplayMetrics dm = new DisplayMetrics();
        int cardWidth = nextCardImage.getMeasuredWidth() / 2;

        int originalPos[] = new int[2];
        nextCardImage.getLocationOnScreen(originalPos);
        int start = originalPos[0];

        int endPos[] = new int[2];
        int end;
        if (playerOne) {
            playerOneImage.getLocationOnScreen(endPos);
            end = endPos[0] - cardWidth;
        } else {
            playerTwoImage.getLocationOnScreen(endPos);
            end = endPos[0] - cardWidth;
        }
        int pos = end - start;

        TranslateAnimation anim = new TranslateAnimation(0, pos, 0, 0);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (playerOne) {
                    //playerOneImage.setVisibility(View.INVISIBLE);
                    playerOneImage.bringToFront();
                } else {
                    playerTwoImage.bringToFront();
                    //playerTwoImage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextCardImage.setImageResource(R.drawable.rat);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        nextCardImage.startAnimation(anim);
    }



        /*
        RelativeLayout root = (RelativeLayout) findViewById(R.id.rootLayout);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int originalPos[] = new int[2];
        view.getLocationOnScreen(originalPos);

        int xDest = dm.widthPixels / 2;
        int cardWidth = (view.getMeasuredWidth() / 2);
        xDest += cardWidth;

        int start = xDest - originalPos[0];
        int end = dm.widthPixels - originalPos[0] + cardWidth / 2; */

}