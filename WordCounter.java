import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class WordCounter {

    private class Node<K, V> {
        private K key;
        private V value;

        public Node() {
            key = null;
            value = null;
        }

        public V getValue() {
            return value;
        }

        public K getKey() {
            return key;
        }

        public void setValue(V val) {
            this.value = val;
        }

        public void setKey(K key) {
            this.key = key;
        }
    }

    MapSet<String, Integer> storageMap;
    private int totalWordCount;
    private HashMap<String, Integer> ignoreHashMap;
    Node<String, Integer> maxNode;
    ArrayList<MapSet.KeyValuePair<String, Integer>> maxTracker;

    public WordCounter(String data_structure) {

        maxNode = new Node<>();
        maxTracker = new ArrayList<>();

        if (data_structure.equals("bst")) {
            storageMap = new BSTMap<String, Integer>();
        } else if (data_structure.equals("hashmap")) {
            storageMap = new HashMap<String, Integer>();
        }

        totalWordCount = 0;
        this.ignoreHashMap = readIgnore();
    }

    private HashMap<String, Integer> readIgnore() {

        HashMap<String, Integer> output = new HashMap<>();

        try {
            FileReader reader = new FileReader("ignoreWords.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = bufferedReader.readLine();

            while (line != null) {
                output.put(line, 0);

                line = bufferedReader.readLine();
            }

            bufferedReader.close();

            totalWordCount = output.size();

            return output;

        } catch (FileNotFoundException e) {

            System.out.println("WordCounter.readWords():: unable to open file " + "ignoreWords.txt");
        } catch (IOException e) {

            System.out.println("WordCounter.readWords():: error reading file " + "ignoreWords.txt");
        }

        return null;
    }

    public ArrayList<String> readWords(String filename) {

        ArrayList<String> output = new ArrayList<>();

        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = bufferedReader.readLine();

            while (line != null) {
                String[] newArr = line.split("[ ]+");

                for (String newString : newArr) {
                    if (Pattern.matches("[a-zA-Z]+", newString)) {
                        output.add(newString);
                    }
                }

                line = bufferedReader.readLine();
            }

            bufferedReader.close();

            totalWordCount = output.size();

            return output;

        } catch (FileNotFoundException e) {

            System.out.println("WordCounter.readWords():: unable to open file " + filename);
        } catch (IOException e) {

            System.out.println("WordCounter.readWords():: error reading file " + filename);
        }

        return null;
    }

    public double buildMap(ArrayList<String> words) {
        double start = System.currentTimeMillis();

        for (String word : words) {
            if (storageMap.containsKey(word)) {
                int wordValue = storageMap.get(word);
                storageMap.put(word, ++wordValue);
                if (maxNode.getKey() == null || maxNode.getValue() < wordValue) {
                    maxNode.setKey(word);
                    maxNode.setValue(wordValue);
                }
            } else {
                if (ignoreHashMap.get(word.toLowerCase()) == null) {
                    storageMap.put(word, 1);
                }
            }
        }

        double end = System.currentTimeMillis();

        return end - start;
    }

    public void clearMap() {
        storageMap.clear();
        totalWordCount = 0;
    }

    public int totalWordCount() {
        return totalWordCount;
    }

    public int uniqueWordCount() {
        return storageMap.size();
    }

    public int getCount(String word) {
        return storageMap.containsKey(word) ? storageMap.get(word) : 0;
    }

    public double getFrequency(String word) {
        return (double) getCount(word) / (double) totalWordCount;
    }

    public boolean writeWordCount(String filename) {
        createFile(filename);

        try {
            FileWriter writer = new FileWriter(filename);

            writer.write("Total number of words: " + totalWordCount() + "\n");

            for (MapSet.KeyValuePair<String, Integer> entry : storageMap.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");

            }
            writer.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void createFile(String filename) {

        try {
            File file = new File(filename);
            if (!file.createNewFile()) {
                System.out.println("Already exists");
            }
        } catch (IOException e) {
            System.out.println("Error occured.");
            e.printStackTrace();
        }
    }

    public ArrayList<String> mostFrequentWords() {
        int minVal = 0;
        MapSet.KeyValuePair<String, Integer> minEntry = new MapSet.KeyValuePair<String, Integer>(null, null);
        LinkedList<MapSet.KeyValuePair<String, Integer>> mostFrequent = new LinkedList<>();
        ArrayList<String> words = new ArrayList<>();

        for (MapSet.KeyValuePair<String, Integer> entry : storageMap.entrySet()) {

            if (mostFrequent.size() <= 10) {
                mostFrequent.add(entry);

                if (entry.getValue() > minVal) {
                    minVal = entry.getValue();
                    minEntry = entry;
                }
            } else {
                if (entry.getValue() > minVal) {
                    mostFrequent.remove(minEntry);
                    mostFrequent.add(entry);
                    minVal = entry.getValue();

                    for (MapSet.KeyValuePair<String, Integer> item : mostFrequent) {
                        if (item.getValue() < minVal) {
                            minVal = item.getValue();
                            minEntry = item;
                        }
                    }
                }
            }

        }
        for (MapSet.KeyValuePair<String, Integer> item : mostFrequent) {
            words.add(item.getKey());
        }

        return words;

    }

    public boolean readWordCount(String filename) {
        clearMap();

        try {
            FileReader fileReader = new FileReader(filename);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while (line != null) {
                String[] newArr = line.split("[ ]+");

                storageMap.put(newArr[0], Integer.parseInt(newArr[1]));

                line = bufferedReader.readLine();
            }

            bufferedReader.close();

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void main(String[] args) {
        WordCounter testBST = new WordCounter("bst");
        String word = "peace";
        // WordCounter testHashmap = new WordCounter("hashmap");

        // String finalString = "\n\nTOTAL TIME\nBST:\n\n";
        int start = 2008;
        System.out.println("MOST FREQUENT WORDS:");
        for (int i = 0; i < 8; i++) {
            int year = start + i;
            System.out.print(year);
            ArrayList<String> fileBST = testBST.readWords("reddit_comments_" + year + ".txt");
            testBST.buildMap(fileBST);
            System.out.print(" | " + word + ": " + testBST.getCount(word) + "\n");
            // int maxDepth = testBST.storageMap.maxDepth();
            // testBST.writeWordCount("bst_test_" + year + ".txt");
            // System.out.println("Done: " + timeTaken + "ms");

            // finalString += year + testBST.mostFrequentWords().toString() + "\n";
        }

        // finalString += "\nHashMap:\n\n";
        // start = 2008;
        // for (int i = 0; i < 8; i++) {
        // int year = start + i;
        // System.out.println(year);
        // ArrayList<String> fileHashMap = testHashmap.readWords("reddit_comments_" +
        // year + ".txt");
        // double timeTaken = testHashmap.buildMap(fileHashMap);
        // int maxDepth = testHashmap.storageMap.maxDepth();
        // testHashmap.writeWordCount("hashmap_test_" + year + ".txt");
        // System.out.println("Done: " + timeTaken + "ms");

        // finalString += year + " Time Taken: " + timeTaken + " Depth: " + maxDepth + "
        // Frequent word: "
        // + testHashmap.maxNode.getKey() + "\n";
        // }

        // System.out.println(finalString);

    }
}
