package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Test class - Main entry point for the Inverted Index application
 */
public class Test {

    public static void main(String args[]) throws IOException {

        Index5 index = new Index5();

        String files = "C:\\Users\\Habib\\Desktop\\SM8\\IR\\ass\\IR ASS\\tmp11\\rl\\collection\\";
        File file = new File(files);

        if (!file.exists()) {
            System.out.println("Folder not found");
            return;
        }

        String[] fileList = file.list();

        if (fileList == null) {
            System.out.println("Error: folder not found");
            return;
        }

        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }

        index.buildIndex(fileList);

        System.out.println("=== Initial Test ===");
        System.out.println(index.find_24_01("data should plain greatest comif"));

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String phrase;

        while (true) {
            System.out.println("\nPrint search phrase:");

            phrase = in.readLine();

            if (phrase == null || phrase.trim().isEmpty()) break;

            System.out.println("\n=== Boolean Result ===");
            System.out.println(index.find_24_01(phrase));

            System.out.println("=== Phrase Result ===");
            System.out.println(index.phraseSearch(phrase));
        }
    }
}