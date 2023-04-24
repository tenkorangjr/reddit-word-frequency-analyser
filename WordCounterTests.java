/*
 * Name: Michael Tenkorang
 * Class Purpose: Working with the HashMaps and Binary Search Trees
 */

import java.util.ArrayList;

public class WordCounterTests {

    public static void main(String[] args) {

        /*
         * Test constructor
         */
        {
            // Setup
            WordCounter map = new WordCounter("hashmap");

            // Verify
            System.out.println(map.totalWordCount() + " == 0");

            // test
            assert map.totalWordCount() == 0 : " constructor faulty";

        }

        // tests the readWords()
        {
            // Setup
            WordCounter map = new WordCounter("hashmap");
            ArrayList<String> file = map.readWords("reddit_comments_2008.txt");

            // Verify
            System.out.println((file.size() != 0) + " == true");

            // test
            assert file.size() != 0 : "readWords() faulty";
        }

        /*
         * Test readWords(), buildmap() and uniqueWordCount()
         */
        {
            // Setup
            WordCounter map = new WordCounter("hashmap");
            ArrayList<String> file = map.readWords("reddit_comments_2008.txt");
            map.buildMap(file);

            // Verify
            System.out.println((map.uniqueWordCount() != 0) + " == true");

            // Test
            assert map.uniqueWordCount() != 0 : " Error in buildMap()";
        }

        /*
         * Test readWordCount
         */
        {
            // Setup
            WordCounter map = new WordCounter("hashmap");
            boolean input = map.readWordCount("hashmap_test_2012.txt");

            // Verify
            System.out.println((input) + " == true");

            // test
            assert input == true : "Error in readWords()";
        }

    }
}