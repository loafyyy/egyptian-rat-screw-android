package com.egyptianratscrew.android;

import java.util.LinkedList;

/**
 * This class can be used to create
 * the players' hand/deck in the game
 * Egyptian Rat Screw
 */

public class PlayerDeck {

    // fieldss
    private LinkedList<Card> deck;

    /**
     * Constructor
     */
    public PlayerDeck() {
        deck = new LinkedList<Card>();
    }

    /**
     * Input: void
     * Output: the first Card in the list
     * Description: returns the first Card in
     * the player's hand/deck and removes it
     */
    public Card playTopCard() {
        return deck.remove(0);
    }

    /**
     * Input: the new Card to add
     * Output: void
     * Description: adds the new Card to
     * the bottom/end of the player's deck
     */
    public void addCard(Card newCard) {
        deck.add(newCard);
    }

    /**
     * Input: void
     * Output: integer
     * Description: returns the size of the player's deck
     */
    public int getSize() {
        return deck.size();
    }
}