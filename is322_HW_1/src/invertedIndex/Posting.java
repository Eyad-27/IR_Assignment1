/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ehab
 */
 
public class Posting {
    public Posting next = null;
    int docId;
    int dtf = 1; 
    public ArrayList<Integer> positions = new ArrayList<>(); // Store the word offsets

    // Constructor for building index (first occurrence)
    Posting(int id, int pos) {
        this.docId = id;
        this.positions.add(pos);
    }

    // Constructor for intersection and loading from disk
    Posting(int id, int t, ArrayList<Integer> p) {
        this.docId = id;
        this.dtf = t;
        this.positions = p;
    }
}