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

import java.util.ArrayList;
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

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }


    //---------------------------------------------
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
       System.out.print(p.docId + p.positions.toString()); // Shows [0, 5, 12] etc.
        if (p.next != null) System.out.print(", ");
        p = p.next;
        }
        System.out.println("]");
    }

    //---------------------------------------------
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
// In Index5.java
public void buildIndex(String[] files) {
    int fid = 0;
    for (String fileName : files) {
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            if (!sources.containsKey(fid)) {
                sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
            }
            String ln;
            int currentPos = 0; // NEW: Track the position of words in the document
            while ((ln = file.readLine()) != null) {
                // Pass currentPos to indexOneLine and update it
                currentPos = indexOneLine(ln, fid, currentPos); 
            }
            sources.get(fid).length = currentPos;
        } catch (IOException e) {
            System.out.println("File " + fileName + " not found. Skip it");
        }
        fid++;
    }
}

    //----------------------------------------------------------------------------  
 // In Index5.java
public int indexOneLine(String ln, int fid, int pos) {
    String[] words = ln.split("\\W+");
    for (String word : words) {
        word = word.toLowerCase();
        if (stopWord(word) || word.isEmpty()) continue;
        word = stemWord(word);

        if (!index.containsKey(word)) {
            index.put(word, new DictEntry());
        }

        // Use the new positional addPosting method
        index.get(word).addPosting(fid, pos);
        
        // Update document frequency ONLY if it's the first time this word appears in this doc
        if (index.get(word).last.dtf == 1) {
            index.get(word).doc_freq += 1;
        }
        
        index.get(word).term_freq += 1;
        pos++; // Increment position for every valid word
    }
    return pos; // Return the next available position
}

//----------------------------------------------------------------------------  
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
String stemWord(String word) {
    Stemmer s = new Stemmer();
    s.addString(word);
    s.stem();
    return s.toString();
}

    //----------------------------------------------------------------------------  
// In Index5.java
public Posting intersect(Posting p1, Posting p2) {
    Posting answer = null;
    Posting last = null;

    while (p1 != null && p2 != null) {
        if (p1.docId == p2.docId) {
            ArrayList<Integer> resultPos = new ArrayList<>();
            // Check every position of word1 against every position of word2
            for (int pos1 : p1.positions) {
                for (int pos2 : p2.positions) {
                    if (pos2 - pos1 == 1) { // Adjacency check
                        resultPos.add(pos2);
                    } else if (pos2 > pos1) {
                        break; 
                    }
                }
            }

            if (!resultPos.isEmpty()) {
                // Create a result posting node with the found positions
                Posting node = new Posting(p1.docId, resultPos.size(), resultPos);
                if (answer == null) { answer = node; last = answer; }
                else { last.next = node; last = last.next; }
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

 public String find_24_01(String phrase) { 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        
        // 1. Check if the first word exists in the index
        DictEntry firstTerm = index.get(words[0].toLowerCase());
        if (firstTerm == null) {
            return "Word '" + words[0] + "' not found in index.";
        }
        
        Posting posting = firstTerm.pList;
        int i = 1;
        while (i < len) {
            // 2. Check if subsequent words exist before trying to intersect
            DictEntry nextTerm = index.get(words[i].toLowerCase());
            if (nextTerm == null) {
                return "Word '" + words[i] + "' not found in index.";
            }
            
            posting = intersect(posting, nextTerm.pList);
            i++;
        }
        
        // 3. Format the results
        while (posting != null) {
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }
        return result;
    }
    
    
    //---------------------------------
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
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
public void store(String storageName) {
    try {
        String pathToStorage = "D:/3rd_Year_2nd-Term/IR/test/IR_Assignment1/tmp11/rl" + storageName;
        Writer wr = new FileWriter(pathToStorage);
        
        // Section 1: Store Sources
        for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
            SourceRecord s = entry.getValue();
            wr.write(entry.getKey() + "," + s.URL + "," + 
                     s.title.replace(',', '~') + "," + 
                     s.length + "," + 
                     String.format("%4.4f", s.norm) + "," + 
                     s.text.replace(',', '~') + "\n");
        }
        wr.write("section2\n");

        // Section 2: Store Index with Positions
        for (Map.Entry<String, DictEntry> entry : index.entrySet()) {
            DictEntry dd = entry.getValue();
            // Write Term, DocFreq, TermFreq
            wr.write(entry.getKey() + "," + dd.doc_freq + "," + dd.term_freq + ";");
            
            Posting p = dd.pList;
            while (p != null) {
                // Format: docId,dtf:pos1-pos2-pos3...:
                wr.write(p.docId + "," + p.dtf + ":");
                for (int i = 0; i < p.positions.size(); i++) {
                    wr.write(p.positions.get(i) + (i == p.positions.size() - 1 ? "" : "-"));
                }
                wr.write(":"); // Separator between postings
                p = p.next;
            }
            wr.write("\n");
        }
        wr.write("end\n");
        wr.close();
        System.out.println("============= END STORE =============");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
//=========================================    
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("D:/3rd_Year_2nd-Term/IR/test/IR_Assignment1/tmp11/rl"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
//----------------------------------------------------    
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
     //load index from hard disk into memory
public HashMap<String, DictEntry> load(String storageName) {
    try {
        String pathToStorage = "D:/3rd_Year_2nd-Term/IR/test/IR_Assignment1/tmp11/rl" + storageName;
        sources = new HashMap<>();
        index = new HashMap<>();
        BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
        String ln;

        // Section 1: Loading Sources
        while ((ln = file.readLine()) != null) {
            if (ln.equalsIgnoreCase("section2")) break;
            String[] ss = ln.split(",");
            int fid = Integer.parseInt(ss[0]);
            SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), 
                                             Integer.parseInt(ss[3]), 
                                             Double.parseDouble(ss[4]), 
                                             ss[5].replace('~', ','));
            sources.put(fid, sr);
        }

        // Section 2: Loading Index with Positions
        while ((ln = file.readLine()) != null) {
            if (ln.equalsIgnoreCase("end")) break;
            
            String[] parts = ln.split(";"); // parts[0] is header, parts[1] is postings data
            String[] header = parts[0].split(","); // term, docFreq, termFreq
            String term = header[0];
            DictEntry entry = new DictEntry(Integer.parseInt(header[1]), Integer.parseInt(header[2]));
            
            if (parts.length > 1) {
                String[] postingsData = parts[1].split(":");
                // i increments by 2 because docInfo and positions alternate in the file
                for (int i = 0; i < postingsData.length; i += 2) {
                    String[] docInfo = postingsData[i].split(",");
                    int docId = Integer.parseInt(docInfo[0]);
                    int dtf = Integer.parseInt(docInfo[1]);
                    
                    String[] posData = postingsData[i+1].split("-");
                    ArrayList<Integer> positions = new ArrayList<>();
                    for (String p : posData) {
                        positions.add(Integer.parseInt(p));
                    }
                    
                    // Reconstruct posting using the positional constructor
                    Posting newP = new Posting(docId, dtf, positions);
                    if (entry.pList == null) {
                        entry.pList = newP;
                        entry.last = newP;
                    } else {
                        entry.last.next = newP;
                        entry.last = newP;
                    }
                }
            }
            index.put(term, entry);
        }
        file.close();
        System.out.println("============= END LOAD =============");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return index;
}
}
//=====================================================================
