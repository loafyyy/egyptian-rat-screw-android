package com.egyptianratscrew.android;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Description: This class creates a deck
 * object that can have 52 different Cards just
 * like a regular deck of cards. One can also
 * shuffle the Cards in the deck.
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

            if (i >= 11 || i == 1) {
                isFaceCard = true;
            } else {
                isFaceCard = false;
            }

            // iterate through each suite
            for (int j = 1; j <= 4; j++) {

                // determine the suite the card should be
                String suite;

                if (j == 1) {
                    suite = "diamonds";
                } else if (j == 2) {
                    suite = "clubs";
                } else if (j == 3) {
                    suite = "hearts";
                } else {
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
        Collections.shuffle(deck);
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