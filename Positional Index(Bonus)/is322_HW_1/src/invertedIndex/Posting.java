/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

 
package invertedIndex;

import java.util.ArrayList;

/**
 * Posting class - Represents a single posting in the posting list
 * Contains document ID, document term frequency, and positions of the term in the document
 */
public class Posting {

    /**
     * next - Pointer to the next posting in the linked list
     */
    public Posting next = null;
    
    /**
     * docId - Document identifier
     */
    int docId;
    
    /**
     * dtf - Document term frequency (how many times the term appears in this document)
     */
    int dtf = 1;

    /**
     * positions - List to store all positions where the term appears in the document
     * positional index - Enables phrase and proximity searches
     */
    ArrayList<Integer> positions = new ArrayList<>();

    /**
     * Constructor with document ID and document term frequency
     * @param id the document ID
     * @param t the document term frequency
     */
    Posting(int id, int t) {
        docId = id;
        dtf = t;
    }
    
    /**
     * Constructor with only document ID (dtf defaults to 1)
     * @param id the document ID
     */
    Posting(int id) {
        docId = id;
    }

    /**
     * Add a position where the term appears in the document
     * @param pos the position/index in the document
     */
    void addPosition(int pos) {
        positions.add(pos);
    }
}