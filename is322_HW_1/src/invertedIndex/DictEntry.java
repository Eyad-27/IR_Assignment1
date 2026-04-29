/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

/**
 *
 * @author ehab
 */
public class DictEntry {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
//=====================================================================
    //public HashSet<Integer> postingList;
    Posting pList = null;
    Posting last = null;
//------------------------------------------------

public boolean postingListContains(int i) {
        return getPostingNode(i) != null;
    }
//------------------------------------------------

public int getPosting(int i) {
        Posting p = getPostingNode(i);
        return (p != null) ? p.dtf : 0;
    }
//------------------------------------------------

public Posting getPostingNode(int i) {
        Posting p = pList;
        while (p != null) {
            if (p.docId == i) {
                return p;
            }
            if (p.docId > i) {
                break; // Optimization: postings are sorted by docId
            }
            p = p.next;
        }
        return null;
    }    
//------------------------------------------------
public void addPosting(int i, int pos) {
        if (pList == null) {
            pList = new Posting(i, pos);
            last = pList;
        } else {
            // Check if we are still processing the same document
            if (last.docId == i) {
                last.positions.add(pos);
                last.dtf++;
            } else {
                // New document, create a new posting node
                last.next = new Posting(i, pos);
                last = last.next;
            }
        }
    }
// implement insert (int docId) method
 
    DictEntry() {
        //  postingList = new HashSet<Integer>();
    }

    DictEntry(int df, int tf) {
        doc_freq = df; 
        term_freq = tf;
    }

}
