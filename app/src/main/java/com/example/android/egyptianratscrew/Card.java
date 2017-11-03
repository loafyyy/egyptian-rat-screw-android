package com.example.android.egyptianratscrew;

/**
 * Created by Jackie on 17-08-13.
 *
 *  Name: Yaeeun Jeon
 *  PennKey: yje
 *  Recitation: 201
 *
 *  Name: Jacqueline Peng
 *  PennKey: pengja
 *  Recitation: 212
 *
 *  Execution: java Card
 *
 *  This Card class creates
 *  objects that represent cards
 *  in a deck
 */

public class Card {

    // fields
    private int number;
    private String suite;
    private boolean isFaceCard;

    /**
     * Constructor
     */
    public Card(int number, String suite, boolean isFaceCard) {
        this.number = number;
        this.suite = suite;
        this.isFaceCard = isFaceCard;
    }

    /**
     * Input: void
     * Output: string
     * Description: returns a string representation of the card
     */
    public String toString() {
        return "c" + number + "of" + suite;
    }

    /**
     * Input: void
     * Output: integer
     * Description: returns the integer value of the card
     */
    public int getNumber() {
        return number;
    }

    /**
     * Input: void
     * Output: boolean
     * Description: returns whether or not the card
     * is a face card
     */
    public boolean isFaceCard() {
        return isFaceCard;
    }
}
