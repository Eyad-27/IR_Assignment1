/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.util.ArrayList;

/**
 * Represents a node in the posting list.
 * Stores the document ID, term frequency, and exact word positions (Bonus Requirement).
 * 
 * @author ehab
 */
public class Posting {

    public Posting next = null;
    public int docId;
    public int dtf = 1;
    public ArrayList<Integer> positions; // BONUS: Stores exact positions of the word in the document

    /**
     * Constructor for a new Posting with a document ID.
     * @param id The document ID.
     */
    public Posting(int id) {
        docId = id;
        positions = new ArrayList<Integer>();
    }

    /**
     * Constructor for a new Posting with document ID and document term frequency.
     * @param id The document ID.
     * @param t The term frequency.
     */
    public Posting(int id, int t) {
        docId = id;
        dtf = t;
        positions = new ArrayList<Integer>();
    }
}