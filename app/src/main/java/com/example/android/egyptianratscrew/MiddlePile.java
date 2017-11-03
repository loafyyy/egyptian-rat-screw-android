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
 *  Execution: java MiddlePile
 *
 *  This class can be used to create
 *  the middle pile object in the game
 *  Egyptian Rat Screw
 */

public class MiddlePile {

    // fields
    private LinkedList<Card> deck;
    private boolean shouldSlap;

    /*
     * Constructor
     */
    public MiddlePile() {
        shouldSlap = false;
        deck = new LinkedList<Card>();
    }

    /**
     * Input: the Card to add
     * Output: void
     * Description: adds a Card to the middle pile
     * and checks whether the players should slap
     */
    public void addCard(Card newCard) {
        // add card to deck
        deck.add(newCard);
        // temp card
        Card currentCard = newCard;
        // get size
        int size = deck.size();

        shouldSlap = false;

        // check to make sure there are enough cards
        if (size >= 2) {
            // get second to last card
            Card doubleCard = deck.get(size - 2);
            // double slap case
            if (currentCard.getNumber() == doubleCard.getNumber()) {
                shouldSlap = true;
            }
        }
        // check to make sure there are enough cards
        if (size >= 3) {
            // get third to last card
            Card sandwichCard = deck.get(size - 3);
            // sandwich slap case
            if (currentCard.getNumber() == sandwichCard.getNumber()) {
                shouldSlap = true;
            }
        }
    }

    /**
     * Input: void
     * Output: integer
     * Description: returns the size of the middle deck
     */
    public int getSize() {
        return deck.size();
    }

    /**
     * Input: void
     * Output: boolean
     * Description: returns whether or not the
     * players should slap the middle pile
     */
    public boolean shouldSlap() {
        return shouldSlap;
    }

    /**
     * Input: the Card to burn
     * Output: void
     * Description: adds the Card to burn at the
     * start/bottom of the middle pile
     */
    public void burn(Card card) {
        deck.add(0, card);
    }

    /**
     * Input: a PlayerDeck to empty the cards to
     * Output: void
     * Description: empties all the cards in the
     * middle deck to a player's hand/deck
     */
    public void emptyPile(PlayerDeck player) {
        int middlePileSize = deck.size();

        for (int i = 0; i < middlePileSize; i++) {
            player.addCard(getTail());
        }
    }

    public void shouldntSlap() {
        shouldSlap = false;
    }

    /**
     * Input: void
     * Output: Card
     * Description: returns the last/top Card in
     * the middle pile
     */
    private Card getTail() {
        return deck.remove(deck.size() - 1);
    }
}