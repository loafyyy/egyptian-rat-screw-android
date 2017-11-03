package com.example.android.egyptianratscrew;

import java.util.LinkedList;

/**
 * Created by Jackie on 17-08-13.
 *
 *
 *  Name: Yaeeun Jeon
 *  PennKey: yje
 *  Recitation: 201
 *
 *  Name: Jacqueline Peng
 *  PennKey: pengja
 *  Recitation: 212
 *
 *  Execution: java Deck
 *
 *  Description: This class creates a deck
 *  object that can have 52 different Cards just
 *  like a regular deck of cards. One can also
 *  shuffle the Cards in the deck.
 */

public class Deck {

    // fields
    private LinkedList<Card> deck;

    /* Input: void
     * Output: void
     *
     * Description: Uses a linked list to represent a
     * new deck of cards sorted in order.
     */
    public void initializeDeck() {

        // create new linked list
        deck = new LinkedList<Card>();

        // iterate through each number
        for (int i = 1; i <= 13; i++) {

            // determine whether or not the card is a
            // face card (jack, queen, king)
            boolean isFaceCard;

            if (i >= 11) {
                isFaceCard = true;
            }
            else {
                isFaceCard = false;
            }

            // iterate through each suite
            for (int j = 1; j <= 4; j++) {

                // determine the suite the card should be
                String suite;

                if (j == 1) {
                    suite = "diamonds";
                }
                else if (j == 2) {
                    suite = "clubs";
                }
                else if (j == 3) {
                    suite = "hearts";
                }
                else {
                    suite = "spades";
                }

                // create the new card
                Card newCard = new Card(i, suite, isFaceCard);

                // add card to deck
                deck.add(newCard);
            }
        }
    }

    /* Input: void
     * Output: void
     *
     * Description: Shuffles the deck by setting each card
     * to a new random index in the linked list.
     */
    public void shuffleDeck() {

        // shuffle the deck by changing the placement of each card
        for (int i = 0; i < 52; i++) {
            // generates random integer from 0 - 50
            // this represents the new position of the card
            int randomIdx = (int) (51 * Math.random());

            // get the card
            Card card = deck.remove(i);

            // add card to random index
            deck.add(randomIdx, card);
        }
    }

    /* Input: void
     * Output: the next Card
     *
     * Description: Returns the next card
     * in the deck
     */
    public Card getNextCard() {
        return deck.remove(0);
    }
}