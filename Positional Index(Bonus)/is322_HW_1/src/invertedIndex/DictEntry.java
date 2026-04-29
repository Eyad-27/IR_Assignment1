/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

/**
 * DictEntry class - Represents a dictionary entry for a term in the inverted index
 * Contains document frequency, term frequency, and a linked list of postings
 * @author ehab
 */
public class DictEntry {

    /**
     * doc_freq - Number of documents that contain the term
     */
    public int doc_freq = 0; // number of documents that contain the term
    
    /**
     * term_freq - Total number of times the term appears in the collection
     */
    public int term_freq = 0; //number of times the term is mentioned in the collection
//=====================================================================
    /**
     * pList - Head pointer to the posting list (linked list of postings)
     */
    Posting pList = null;
    
    /**
     * last - Pointer to the last node in the posting list for efficient insertion
     */
    Posting last = null;
//------------------------------------------------

    /**
     * Check if the posting list contains a posting with a specific document ID
     * @param i the document ID to search for
     * @return true if document ID is found in posting list, false otherwise
     */
    boolean postingListContains(int i) {
        boolean found = false;
        Posting p = pList;
        while (p != null) {
            if (p.docId == i) {
                return true;
            }
            p = p.next;
        }
        return found;
    }
//------------------------------------------------

    /**
     * Get the document term frequency for a specific document
     * @param i the document ID
     * @return the term frequency in document i, or 0 if not found
     */
    int getPosting(int i) {
        int found = 0;
        Posting p = pList;
        while (p != null) {
            if (p.docId >= i) {
                if (p.docId == i) {
                    return p.dtf;
                } else {
                    return 0;
                }
            }
            p = p.next;
        }
        return found;
    }
//------------------------------------------------

    /**
     * Add a new posting to the end of the posting list
     * @param i the document ID for the new posting
     */
    void addPosting(int i) {
        // pList = new Posting(i);
        if (pList == null) {
            pList = new Posting(i);
            last = pList;
        } else {
            last.next = new Posting(i);
            last = last.next;
        }
    }
// implement insert (int docId) method
 
    /**
     * Default constructor
     */
    DictEntry() {
        //  postingList = new HashSet<Integer>();
    }

    /**
     * Constructor with initial document and term frequency
     * @param df the document frequency
     * @param tf the term frequency
     */
    DictEntry(int df, int tf) {
        doc_freq = df; 
        term_freq = tf;
    }

}