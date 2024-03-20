package scrabble;

import scrabble.Model.Tile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The WordValidator class is responsible for checking the supplied words against a given wordlist which gets loaded on creation
 */
public class WordValidator {
    private final HashMap<String, String> wordDictionary = new HashMap<>();
    private final Map<Tile, Integer> tiles;

    /**
     * Constructs a new WordValidator object by extracting the words and descriptions from the given file and loading
     * them into the wordDictionary.
     *
     * @param wordListFile the file from which to load the words and descriptions
     * @throws IOException if there is an error reading the file
     */
    public WordValidator(File wordListFile, Map<Tile, Integer> tiles) throws IOException {
        extractWordsAndDescriptionFromFile(wordListFile);
        this.tiles = tiles;
    }


    /**
     * Checks whether the loaded wordDictionary contains the given word.
     *
     * @param word the word to check
     * @return true if the word is in the dictionary, false otherwise
     */
    public boolean containsWord(String word) {
        word = word.toLowerCase();
        if (word.contains(" ")){
            return controlBothJoker(word);
        }
        return wordDictionary.containsKey(word);
    }

    private boolean controlBothJoker(String word){
        String firstJoker;
        for (Tile tile1: tiles.keySet()){
            firstJoker = word.replaceFirst(" ", String.valueOf(tile1.letter()).toLowerCase());
            if(firstJoker.contains(" ")){
                String secondJoker;
                for (Tile tile2: tiles.keySet()){
                    secondJoker = firstJoker.replaceFirst(" ", String.valueOf(tile2.letter()).toLowerCase());
                    if(wordDictionary.containsKey(secondJoker)){
                        return true;
                    }
                }
            } else{
                if(wordDictionary.containsKey(word)){
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Returns the description for the given word if it exists in the loaded wordDictionary.
     *
     * @param word the word for which to retrieve the description
     * @return the description associated with the word in the wordDictionary
     * @throws IllegalArgumentException if the word is not in the dictionary
     */
    public String getDescriptionFromWord(String word) {
        if (containsWord(word)) {
            return wordDictionary.get(word.toLowerCase());
        } else {
            throw new IllegalArgumentException(word + " is not in the word list");
        }
    }

    private void extractWordsAndDescriptionFromFile(File wordListFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(wordListFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] wordDescriptionPair = line.split(";");
            if (wordDescriptionPair.length > 0 && !wordDictionary.containsKey(wordDescriptionPair[0].toLowerCase())) {
                wordDictionary.put(wordDescriptionPair[0].toLowerCase(), (wordDescriptionPair.length == 2) ? wordDescriptionPair[1].replaceAll("\"", "") : "No Description available.");
            }
        }
    }
}
