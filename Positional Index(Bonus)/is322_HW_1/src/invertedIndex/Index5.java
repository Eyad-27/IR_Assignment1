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
public class Index5 {

    int N = 0;
    public Map<Integer, SourceRecord> sources;
    public HashMap<String, DictEntry> index;

    /**
     * Constructor - Initialize the index and sources maps
     */
    public Index5() {
        sources = new HashMap<>();
        index = new HashMap<>();
    }

    /**
     * Print the posting list in format [docId1,docId2,...]
     * Removes the last comma and displays the list
     */
    public void printPostingList(Posting p) {
        System.out.print("[");
       while (p != null) {
    System.out.print("" + p.docId);
    if (p.next != null) System.out.print(",");
    p = p.next;
}
        System.out.println("]");
    }

    /**
     * Build the inverted index from a collection of files
     * Read each file line by line and index its content
     */
    public void buildIndex(String[] files) {
        int fid = 0;

        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {

                sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));

                String ln;
                int flen = 0;

                while ((ln = file.readLine()) != null) {
    flen += indexOneLine(ln, fid);
}
                sources.get(fid).length = flen;

            } catch (Exception e) {
                System.out.println("Error reading file");
            }
            fid++;
        }
    }

    /**
     * Index a single line of text
     * Split the line into words, filter stop words, and add to index
     */
    public int indexOneLine(String ln, int fid) {

        String[] words = ln.split("\\W+");
        int flen = words.length;

        int position = 0;

        for (String word : words) {

            word = word.toLowerCase();

            if (stopWord(word)) {
                position++;
                continue;
            }

            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }

            DictEntry entry = index.get(word);

            if (!entry.postingListContains(fid)) {

                Posting p = new Posting(fid);
                p.addPosition(position);

                if (entry.pList == null) {
                    entry.pList = p;
                    entry.last = p;
                } else {
                    entry.last.next = p;
                    entry.last = p;
                }

                entry.doc_freq++;

            } else {
                entry.last.dtf++;
                entry.last.addPosition(position);
            }

            entry.term_freq++;
            position++;
        }

        return flen;
    }

    /**
     * Check if a word is a stop word
     * Stop words are common words that are not indexed
     */
    boolean stopWord(String word) {
        return word.length() < 2 ||
               word.equals("the") || word.equals("to") ||
               word.equals("be") || word.equals("for");
    }

    /**
     * Intersect method - Find common documents between two posting lists
     * Returns posting list containing only documents that have both terms
     */
    Posting intersect(Posting pL1, Posting pL2) {
    Posting answer = null;
    Posting last = null;

    while (pL1 != null && pL2 != null) {
        if (pL1.docId == pL2.docId) {
            if (answer == null) {
                answer = new Posting(pL1.docId);
                last = answer;
            } else {
                last.next = new Posting(pL1.docId);
                last = last.next;
            }
            pL1 = pL1.next;
            pL2 = pL2.next;
        } else if (pL1.docId < pL2.docId) {
            pL1 = pL1.next;
        } else {
            pL2 = pL2.next;
        }
    }
    return answer;
}

    /**
     * Positional intersect - Find documents where two terms appear consecutively
     * Returns posting list of documents with matching positions (pos2 - pos1 == 1)
     */
    Posting positionalIntersect(Posting p1, Posting p2) {

        Posting answer = null;
        Posting last = null;

        while (p1 != null && p2 != null) {

            if (p1.docId == p2.docId) {

                for (int pos1 : p1.positions) {
                    for (int pos2 : p2.positions) {

                        if (pos2 - pos1 == 1) {

                            if (answer == null) {
                                answer = new Posting(p1.docId);
                                answer.addPosition(pos2);
                                last = answer;
                            } else {
                                last.next = new Posting(p1.docId);
                                last = last.next;
                                last.addPosition(pos2);
                            }
                        }
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
     * Phrase search - Find all documents containing a complete phrase
     * Uses positional intersection to match consecutive terms
     */
     public String find_24_01(String phrase) {

        String[] words = phrase.split("\\W+");

        if (!index.containsKey(words[0].toLowerCase())) {
            return "No results";
        }

        Posting result = index.get(words[0].toLowerCase()).pList;

        for (int i = 1; i < words.length; i++) {

            if (!index.containsKey(words[i].toLowerCase())) {
                return "No results";
            }

            result = intersect(result, index.get(words[i].toLowerCase()).pList);
        }

        String out = "";

        while (result != null) {
          SourceRecord s = sources.get(result.docId);
out += result.docId + " - " + s.URL + " - " + s.length + "\n";
            result = result.next;
        }

        if (out.isEmpty()) return "No results";

        return out;
    }

    public String phraseSearch(String phrase) {

        String[] words = phrase.toLowerCase().split("\\W+");

        if (!index.containsKey(words[0])) {
            return "No results";
        }

        Posting result = index.get(words[0]).pList;

        for (int i = 1; i < words.length; i++) {

            if (!index.containsKey(words[i])) {
                return "No results";
            }

            result = positionalIntersect(result, index.get(words[i]).pList);
        }

        String out = "";

        while (result != null) {
           SourceRecord s = sources.get(result.docId);
out += result.docId + " - " + s.URL + " - " + s.length + "\n";
            result = result.next;
        }

        if (out.isEmpty()) return "No results";

        return out;
    }
}