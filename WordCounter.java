import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class WordCounter {

    MapSet<String, Integer> storageMap;
    private String dataStructure;
    private int totalWordCount;

    public WordCounter(String data_structure) {

        if (data_structure.equals("bst")) {
            storageMap = new BSTMap<String, Integer>();
            dataStructure = "bst";
        } else if (data_structure.equals("hashmap")) {
            storageMap = new HashMap<String, Integer>();
            dataStructure = "hashmap";
        }

        totalWordCount = 0;
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
                storageMap.put(word, storageMap.get(word) + 1);
            } else {
                storageMap.put(word, 1);
            }
        }

        double end = System.currentTimeMillis();

        return end - start;
    }

    public void clearMap() {
        if (this.dataStructure == "bst") {
            storageMap = new BSTMap<>();
        } else if (this.dataStructure == "hashmap") {
            storageMap = new HashMap<>();
        }
        totalWordCount = 0;
    }

    public int totalWordCount() {
        return totalWordCount;
    }

    public int uniqueWordCount() {
        return storageMap.size();
    }

    public int getCount(String word) {
        return storageMap.get(word);
    }

    public double getFrequency(String word) {
        return (double) getCount(word) / totalWordCount;
    }

    public boolean writeWordCount(String filename) {
        createFile(filename);

        try {
            FileWriter writer = new FileWriter(filename);

            writer.write("Total number of words: " + totalWordCount() + "\n");

            for (MapSet.KeyValuePair<String, Integer> entry : storageMap.entrySet()) {
                System.out.println("Word: " + entry.getKey() + " Frequency: " + entry.getValue());
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

    public boolean readWordCount(String filename) {
        clearMap();
        ArrayList<String> output = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(filename);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

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

            buildMap(output);

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

    }
}
