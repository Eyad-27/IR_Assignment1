package invertedIndex;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 * SourceRecord class - Metadata for each document in the collection
 * Stores information about document ID, URL, title, content, normalization factor, and document length
 * @author ehab
 */
public class SourceRecord {
    
    /**
     * fid - File/Document identifier
     */
    public int fid;
    
    /**
     * URL - Web address or file path of the document
     */
    public String URL;
    
    /**
     * title - Title or name of the document
     */
    public String title;
    
    /**
     * text - Content of the document
     */
    public String text;
    
    /**
     * norm - Normalization factor for vector length (used in TF-IDF calculations)
     */
    public Double norm;
    
    /**
     * length - Total number of terms in the document
     */
    public int length;
    
    /**
     * Get the URL of the document
     * @return the document URL
     */
    public String getURL(){
        return URL;
    }
    
    /**
     * Constructor with all parameters including normalization and length
     * @param f file/document ID
     * @param u URL of the document
     * @param tt title of the document
     * @param ln length of the document
     * @param n normalization factor
     * @param tx text content of the document
     */
    public SourceRecord(int f,String u, String tt,int ln, Double n, String tx){
        fid=f; URL=u; title=tt; text=tx;
        norm=n;
        length=ln;
    }
    
    /**
     * Constructor without normalization and length (defaults to 0.0 and 0)
     * @param f file/document ID
     * @param u URL of the document
     * @param tt title of the document
     * @param tx text content of the document
     */
    public SourceRecord(int f,String u, String tt, String tx){
        fid=f; URL=u; title=tt; text=tx;
        norm=0.0;
        length=0;
    }
}