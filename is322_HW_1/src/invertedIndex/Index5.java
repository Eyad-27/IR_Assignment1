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
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;

/**
 *
 * @author ehab
 */

/**
 * This class builds and manages an inverted index for a collection of
 * documents.
 * It supports building the index, intersecting posting lists,
 * and searching queries.
 */

public class Index5 {

    // --------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources; // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    // --------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }

    // ---------------------------------------------

    /**
     * Prints the posting list in a readable format [docId, docId, ...].
     * It also ensures proper formatting without trailing commas.
     *
     * @param p head of the posting list
     */

    public void printPostingList(Posting p) {
        System.out.print("[");

        boolean first = true;
        while (p != null) {
            if (!first) {
                System.out.print(",");
            }
            System.out.print(p.docId);
            first = false;
            p = p.next;
        }
        System.out.println("]");
    }

    // ---------------------------------------------

    /**
     * Prints the entire inverted index (dictionary).
     * For each term, it displays:
     * - The term (word)
     * - Its document frequency (number of documents it appears in)
     * - Its posting list (list of document IDs)
     *
     * It also prints the total number of indexed terms at the end.
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

    // -----------------------------------------------

    /**
     * Builds the inverted index from a list of text files.
     * It reads each file, extracts words, and stores them in a dictionary
     * where each word maps to a posting list of document IDs.
     *
     * @param files array of file names to be indexed
     */

    public void buildIndex(String[] files) { // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here **** **** hint flen += ________________(ln, fid);
                    flen += indexOneLine(ln, fid);
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        // printDictionary();
    }

    // ----------------------------------------------------------------------------

    /**
     * Processes a single line of text and updates the inverted index.
     * It splits the line into words, removes stop words, applies stemming,
     * and updates posting lists, document frequency, and term frequency.
     *
     * @param ln  the input line of text
     * @param fid the document ID
     * @return number of words processed in the line
     */

    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
        // String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", "
        // ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
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
                index.get(word).doc_freq += 1; // set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            // set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

    // ----------------------------------------------------------------------------

    /**
     * Checks if a word is a stop word or too short to be indexed.
     *
     * @param word the input word
     * @return true if the word should be ignored, false otherwise
     */

    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from")
                || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or")
                || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }

    // ----------------------------------------------------------------------------
    /**
     * Applies stemming to a word (currently returns the word as-is).
     *
     * @param word the input word
     * @return the stemmed version of the word
     */
    String stemWord(String word) { // skip for now
        return word;
        // Stemmer s = new Stemmer();
        // s.addString(word);
        // s.stem();
        // return s.toString();
    }

    // ----------------------------------------------------------------------------

    /**
     * Computes the intersection of two posting lists.
     * It returns a new posting list containing document IDs
     * that are present in both input lists.
     *
     * @param pL1 first posting list
     * @param pL2 second posting list
     * @return posting list of common document IDs
     */

    Posting intersect(Posting pL1, Posting pL2) {
        /// **** -1- complete after each comment ****
        // INTERSECT ( p1 , p2 )
        // 1 answer ← {}
        Posting answer = null;
        Posting last = null;
        // 2 while p1 != NIL and p2 != NIL
        while (pL1 != null && pL2 != null) {

            // 3 do if docID ( p 1 ) = docID ( p2 )
            if (pL1.docId == pL2.docId) {

                // 4 then ADD ( answer, docID ( p1 ))
                // answer.add(pL1.docId);
                Posting node = new Posting(pL1.docId);
                if (answer == null) {
                    answer = node;
                    last = answer;
                } else {
                    last.next = node;
                    last = last.next;
                }

                // 5 p1 ← next ( p1 )
                // 6 p2 ← next ( p2 )
                pL1 = pL1.next;
                pL2 = pL2.next;

                // 7 else if docID ( p1 ) < docID ( p2 )
            } else if (pL1.docId < pL2.docId) {

                // 8 then p1 ← next ( p1 )
                pL1 = pL1.next;
                // 9 else p2 ← next ( p2 )
            } else {
                pL2 = pL2.next;
            }
        }

        // 10 return answer
        return answer;
    }

    /**
     * Searches for a phrase (multiple terms) using the inverted index.
     * It intersects posting lists of all words to find common documents,
     * then returns their details as a formatted string.
     *
     * @param phrase input search query
     * @return string containing matching document information
     */

    public String find_24_01(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        Posting posting = null;

        // find first valid word
        int i = 0;
        while (i < len) {
            String w = words[i].toLowerCase();
            DictEntry entry = index.get(w);

            if (entry != null) {
                posting = entry.pList;
                break;
            }
            i++;
        }

        // if no valid words found
        if (posting == null)
            return "";

        i++;

        // intersect remaining words
        while (i < len) {
            String w = words[i].toLowerCase();
            DictEntry entry = index.get(w);

            if (entry != null) {
                posting = intersect(posting, entry.pList);
            }
            i++;
        }

        // build result
        while (posting != null) {
            result += "\t" + posting.docId + " - " +
                    sources.get(posting.docId).title + " - " +
                    sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }

        return result;
    }

    // ---------------------------------
    /**
     * Sorts an array of words in ascending order using bubble sort.
     *
     * @param words array of words to be sorted
     * @return sorted array of words
     */
    String[] sort(String[] words) { // bubble sort
        boolean sorted = false;
        String sTmp;
        // -------------------------------------------------------
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

    // ---------------------------------

    /**
     * Stores the indexed data (sources and inverted index) into a file.
     * It writes document details first, then the dictionary with posting lists.
     *
     * @param storageName name of the output file
     */

    public void store(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/rl/" + storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = "
                        + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); // String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                // System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" +
                // dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    // System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
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

    // =========================================

    /**
     * Checks if the storage file exists in the specified path.
     *
     * @param storageName name of the file
     * @return true if the file exists, false otherwise
     */

    public boolean storageFileExists(String storageName) {
        java.io.File f = new java.io.File("/home/ehab/tmp11/rl/" + storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;

    }

    // ----------------------------------------------------

    /**
     * Creates a new storage file and initializes it with a basic structure.
     *
     * @param storageName name of the file to be created
     */

    public void createStore(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/" + storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // load index from hard disk into memory

    /**
     * Loads the inverted index and document sources from a storage file.
     * It reconstructs the sources table first, then rebuilds the dictionary
     * and posting lists from the saved file format.
     *
     * @param storageName name of the storage file
     * @return reconstructed inverted index (dictionary)
     */

    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/rl/" + storageName;
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " ["
                            + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]),
                            Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    // System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+
                    // Double.parseDouble(ss[4])+ "] \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                // System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx; // posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]),
                                Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            // printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

// =====================================================================
