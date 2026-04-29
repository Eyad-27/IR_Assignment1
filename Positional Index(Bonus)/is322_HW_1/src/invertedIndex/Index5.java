/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The core class responsible for building, storing, loading, and querying 
 * an Inverted Index for Information Retrieval, including Positional Indexing.
 * 
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.
    public HashMap<String, DictEntry> index; // THe inverted index
    
    // BONUS: Tracks the position of the word in the current document being read
    private int currentDocPos = 0;
    //--------------------------------------------

    /**
     * Constructor for the Index5 class.
     * Initializes the sources map and the inverted index dictionary.
     */
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    /**
     * Sets the total number of documents in the collection.
     * @param n The number of documents.
     */
    public void setN(int n) {
        N = n;
    }

    //---------------------------------------------
    /**
     * Prints the posting list for a dictionary term.
     * Displays a bracketed list of document IDs.
     * @param p The head of the Posting list to print.
     */
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
            System.out.print(p.docId);
            p = p.next;
            if (p != null) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    //---------------------------------------------
    /**
     * Prints the entire inverted index dictionary to the console.
     * Displays the term, document frequency, and its associated posting list.
     */
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }
 
    //-----------------------------------------------
    /**
     * Builds the inverted index by reading and parsing a list of text files.
     * Resets the positional counter for each new document.
     * @param files An array of file paths to process.
     */
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                this.currentDocPos = 0; // BONUS: Reset position counter for the new document
                
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    ///**** hint   flen +=  ________________(ln, fid);
                    flen += indexOneLine(ln, fid);
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }

    //----------------------------------------------------------------------------  
    /**
     * Processes a single line of text by tokenizing, filtering stop words,
     * stemming, and updating the inverted index mapping with positions.
     * @param ln The string line of text to process.
     * @param fid The document ID of the file the line belongs to.
     * @return The number of words processed in the line.
     */
    public int indexOneLine(String ln, int fid) {
        int flen = 0;
        String[] words = ln.split("\\W+");
        flen += words.length;
        
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                this.currentDocPos++; // BONUS: Keep advancing position so phrases are tracked accurately
                continue;
            }
            word = stemWord(word);
            
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                
                Posting p = new Posting(fid);
                p.positions.add(this.currentDocPos); // BONUS: Add exact position
                
                if (index.get(word).pList == null) {
                    index.get(word).pList = p;
                    index.get(word).last = p;
                } else {
                    index.get(word).last.next = p;
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
                index.get(word).last.positions.add(this.currentDocPos); // BONUS: Add additional position
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {
                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }
            
            this.currentDocPos++; // BONUS: Advance position for next word
        }
        return flen;
    }

    //----------------------------------------------------------------------------  
    /**
     * Checks if a given word is a stop word or too short to be indexed.
     * @param word The string word to check.
     * @return True if the word is a stop word, false otherwise.
     */
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------  
    /**
     * Reduces a given word to its stem root using the Stemmer class.
     * @param word The word to stem.
     * @return The stemmed version of the word.
     */
    String stemWord(String word) { 
       Stemmer s = new Stemmer();
       s.addString(word);
       s.stem();
       return s.toString();
    }

    //----------------------------------------------------------------------------  
    /**
     * Intersects two posting lists to find common document IDs using a boolean AND logic.
     * @param pL1 The first posting list.
     * @param pL2 The second posting list.
     * @return A new Posting list containing document IDs present in both lists.
     */
    Posting intersect(Posting pL1, Posting pL2) {
///**** -1-   complete after each comment ****
//   INTERSECT ( p1 , p2 )
//          1  answer ←      {}
        Posting answer = null;
        Posting last = null;
//      2 while p1  != NIL and p2  != NIL
        while (pL1 != null && pL2 != null) {
//          3 do if docID ( p 1 ) = docID ( p2 )
            if (pL1.docId == pL2.docId) {
//          4   then ADD ( answer, docID ( p1 ))
                Posting node = new Posting(pL1.docId);
                if (answer == null) {
                    answer = node;
                    last = answer;
                } else {
                    last.next = node;
                    last = last.next;
                }
//          5       p1 ← next ( p1 )
//          6       p2 ← next ( p2 )
                pL1 = pL1.next;
                pL2 = pL2.next;
 //          7   else if docID ( p1 ) < docID ( p2 )
            } else if (pL1.docId < pL2.docId) {
//          8        then p1 ← next ( p1 )
                pL1 = pL1.next;
//          9        else p2 ← next ( p2 )
            } else {
                pL2 = pL2.next;
            }
        }
//      10 return answer
        return answer;
    }

    /**
     * BONUS: Intersects two posting lists based on their exact positions in the text.
     * Ensures that the words appear in the correct sequence.
     * @param p1 The first posting list.
     * @param p2 The second posting list.
     * @param distance The exact word offset expected between the two words.
     * @return A new Posting list containing documents where the phrase is structurally intact.
     */
    Posting positionalIntersect(Posting p1, Posting p2, int distance) {
        Posting answer = null;
        Posting last = null;
        while (p1 != null && p2 != null) {
            if (p1.docId == p2.docId) {
                ArrayList<Integer> matchedPositions = new ArrayList<>();
                int i = 0, j = 0;
                
                // Compare the positions lists of the two words in the same document
                while (i < p1.positions.size() && j < p2.positions.size()) {
                    int pos1 = p1.positions.get(i);
                    int pos2 = p2.positions.get(j);
                    
                    if (pos2 == pos1 + distance) { // They are spaced exactly correctly!
                        matchedPositions.add(pos2);
                        i++; j++;
                    } else if (pos1 + distance < pos2) {
                        i++;
                    } else {
                        j++;
                    }
                }
                
                // If they appeared together properly, add to answer list
                if (!matchedPositions.isEmpty()) {
                    Posting node = new Posting(p1.docId);
                    node.positions = matchedPositions;
                    node.dtf = matchedPositions.size();
                    if (answer == null) {
                        answer = node;
                        last = answer;
                    } else {
                        last.next = node;
                        last = last.next;
                    }
                }
                p1 = p1.next;
                p2 = p2.next;
            } else if (p1.docId < p2.docId) {
                p1 = p1.next;
            } else {
                p2 = p2.next;
            }
        }
        return answer;
    }

    /**
     * Processes a multi-word phrase query. 
     * Calculates and formats both the Boolean Model AND the Positional Phrase Model results.
     * @param phrase The multi-word query string.
     * @return A formatted string detailing the matched documents for both models.
     */
    public String find_24_01(String phrase) { 
        String result = "";
        String[] words = phrase.split("\\W+");
        
        ArrayList<Posting> validPostings = new ArrayList<>();
        ArrayList<Integer> wordOffsets = new ArrayList<>(); // Tracks distances between words
        
        for (int i = 0; i < words.length; i++) {
            String w = words[i].toLowerCase();
            if (stopWord(w)) continue;
            w = stemWord(w);
            if (!index.containsKey(w)) return "Word '" + words[i] + "' not found in index.\n";
            validPostings.add(index.get(w).pList);
            wordOffsets.add(i); // Keep track of original position in the query
        }
        
        if (validPostings.isEmpty()) return "No valid words in query.\n";

        // 1. Boolean Intersect (Standard AND logic)
        Posting boolAns = validPostings.get(0);
        for (int i = 1; i < validPostings.size(); i++) {
            boolAns = intersect(boolAns, validPostings.get(i));
        }
        
        result += "Boolean Model result = \n";
        Posting temp = boolAns;
        if (temp == null) result += "None\n";
        while (temp != null) {
            result += "DocID: " + temp.docId + "\n";
            temp = temp.next;
        }

        // 2. Positional Intersect (Strict Phrase logic)
        Posting phraseAns = validPostings.get(0);
        for (int i = 1; i < validPostings.size(); i++) {
            // Distance handles edge cases if the user typed a stopword inside their query phrase
            int distance = wordOffsets.get(i) - wordOffsets.get(i-1); 
            phraseAns = positionalIntersect(phraseAns, validPostings.get(i), distance);
        }
        
        result += "\nPhrase = \n";
        temp = phraseAns;
        if (temp == null) result += "None\n";
        while (temp != null) {
            result += "DocID: " + temp.docId + "\n";
            temp = temp.next;
        }
        
        return result;
    }
    
    //---------------------------------
    /**
     * Sorts an array of string words alphabetically using bubble sort.
     * @param words The array of strings to sort.
     * @return The alphabetically sorted array.
     */
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

    //---------------------------------
    /**
     * Serializes and stores the indexed sources and inverted index to the disk.
     * Includes the exact array of positions for each posting.
     * @param storageName The filename suffix used for storage.
     */
    public void store(String storageName) {
        try {
            String pathToStorage ="D:/3rd_Year_2nd-Term/IR/test/IR_Assignment1/tmp11/rl"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); 
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    wr.write(p.docId + "," + p.dtf);
                    // BONUS: Write positions to disk so they survive a reboot
                    for (int pos : p.positions) {
                        wr.write("," + pos);
                    }
                    wr.write(":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=========================================    
    /**
     * Checks if the inverted index storage file already exists on the disk.
     * @param storageName The filename suffix to check.
     * @return True if the file exists, false otherwise.
     */
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("D:/3rd_Year_2nd-Term/IR/test/IR_Assignment1/tmp11/rl"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
    }

    //----------------------------------------------------    
    /**
     * Creates an empty storage file on the disk for the inverted index.
     * @param storageName The filename suffix to create.
     */
    public void createStore(String storageName) {
        try {
            String pathToStorage = "D:/3rd_Year_2nd-Term/IR/test/IR_Assignment1/tmp11/rl"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------      
    /**
     * Loads and deserializes the sources, inverted index, and word positions from the disk.
     * @param storageName The filename suffix to load from.
     * @return The populated inverted index HashMap.
     */
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "D:/3rd_Year_2nd-Term/IR/test/IR_Assignment1/tmp11/rl"+storageName;         
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                sources.put(fid, new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ',')));
            }
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                
                String[] ss1bx;   
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    Posting p = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                    
                    // BONUS: Read positions back from disk!
                    for(int k = 2; k < ss1bx.length; k++) {
                        p.positions.add(Integer.parseInt(ss1bx[k]));
                    }

                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = p;
                        index.get(ss1a[0]).last = p;
                    } else {
                        index.get(ss1a[0]).last.next = p;
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}