package com.example.android.egyptianratscrew;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // how many cards does each player have to play
    // start with player one
    int playerOnePlays = 1;
    int playerTwoPlays = 0;

    // determines who played the last face card
    boolean isFCPlayerOne = false;
    boolean isFCPlayerTwo = false;

    // player decks
    PlayerDeck playerOne;
    PlayerDeck playerTwo;

    // middle pile
    MiddlePile middlePile;

    // images for the different piles
    ImageView playerOneImage;
    ImageView playerTwoImage;
    ImageView nextCardImage;

    // player one num of cards
    TextView numCardsPlayerOne;
    TextView numCardsPlayerTwo;
    TextView numCardsCenter;

    // burn/slap text view
    TextView burnSlap;

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
        numCardsCenter = (TextView) findViewById(R.id.num_cards_middle);

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

            // show animation
            // TODO

            // display the slap on PennDraw
            burnSlap.setText("P1 SLAPS");

            // display empty pile
            nextCardImage.setImageResource(R.drawable.rat);
        }

        // if the slap was incorrect
        else {
            // burn player one's next card
            middlePile.burn(playerOne.playTopCard());

            // animate the card going into the middle
            // TODO

            // OOH BURN text TODO
            burnSlap.setText("P1 BURN");
        }

        // update text field that display deck sizes
        numCardsPlayerOne.setText("" + playerOne.getSize());
        numCardsPlayerTwo.setText("" + playerTwo.getSize());
        numCardsCenter.setText("" + middlePile.getSize());
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

            // show animation
            // TODO

            // display the slap on PennDraw
            // TODO
            burnSlap.setText("P2 SLAPS");

            // display empty pile
            nextCardImage.setImageResource(R.drawable.rat);
        }

        // if the slap was incorrect
        else {
            // burn player two's next card
            middlePile.burn(playerTwo.playTopCard());

            // animate the card going into the middle
            // TODO

            // OOH BURN text
            // TODO
            burnSlap.setText("P2 BURN");
        }

        // update text field that display deck sizes
        numCardsPlayerOne.setText("" + playerOne.getSize());
        numCardsPlayerTwo.setText("" + playerTwo.getSize());
        numCardsCenter.setText("" + middlePile.getSize());
    }

    public void playerOnePlay(View view) {
        // player one can only play when it's his/her turn
        if (playerOnePlays != 0) {

            // update the last card
            Card nextCard = playerOne.playTopCard();

            // add the card to the middle pile
            middlePile.addCard(nextCard);

            // checks if the card is a face card
            if (nextCard.isFaceCard()) {
                // if it is, then it means
                // player one played the last face card
                isFCPlayerOne = true;
                isFCPlayerTwo = false;

                // play transfers to player two
                playerTwoPlays += nextCard.getNumber() - 10;
                playerOnePlays = 0;
            }

            // if player one still has plays
            else if (playerOnePlays > 1) {
                playerOnePlays -= 1;
            }

            // if player one has no more plays,
            // play transfers to player two
            else {
                playerOnePlays -= 1;
                playerTwoPlays += 1;

                if (isFCPlayerTwo) {

                    // add deck cards to player one
                    middlePile.emptyPile(playerTwo);

                    // update boolean
                    isFCPlayerTwo = false;

                    // show animation
                    // TODO

                    // empty pile image
                    nextCardImage.setImageResource(R.drawable.rat);
                }
            }

            // animate the card going into the middle
            // TODO!!!

            // draw next card
            if (middlePile.getSize() != 0) {
                int resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());
                nextCardImage.setImageResource(resID);
            }

            // update text field that display deck sizes
            numCardsPlayerOne.setText("" + playerOne.getSize());
            numCardsPlayerTwo.setText("" + playerTwo.getSize());
            numCardsCenter.setText("" + middlePile.getSize());
        }
    }

    public void playerTwoPlay(View view) {
        // player two can only play when it's his/her turn
        if (playerTwoPlays != 0) {

            // update the last card
            Card nextCard = playerTwo.playTopCard();

            // add the card to the middle pile
            middlePile.addCard(nextCard);

            // checks if the card is a face card
            if (nextCard.isFaceCard()) {
                // if it is, then it means
                // player two played the last face card
                isFCPlayerTwo = true;
                isFCPlayerOne = false;

                // play transfers to player one
                playerOnePlays += nextCard.getNumber() - 10;
                playerTwoPlays = 0;
            }

            // if player two still has plays
            else if (playerTwoPlays > 1) {
                playerTwoPlays -= 1;
            }

            // if player two has no more plays,
            // play transfers to player one
            else {
                playerTwoPlays -= 1;
                playerOnePlays += 1;

                if (isFCPlayerOne) {

                    // add deck cards to player one
                    middlePile.emptyPile(playerOne);

                    // update boolean
                    isFCPlayerOne = false;

                    // show animation
                    // TODO


                    // empty pile image
                    nextCardImage.setImageResource(R.drawable.rat);
                }
            }

            // animate the card going into the middle
            // TODO

            // draw next card
            if (middlePile.getSize() != 0) {
                int resID = getResources().getIdentifier(nextCard.toString(), "drawable", getPackageName());
                nextCardImage.setImageResource(resID);
            }

            // update text field that display deck sizes
            numCardsPlayerOne.setText("" + playerOne.getSize());
            numCardsPlayerTwo.setText("" + playerTwo.getSize());
            numCardsCenter.setText("" + middlePile.getSize());
        }
    }
}