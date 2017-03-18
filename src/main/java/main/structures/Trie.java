package main.structures;

import java.util.*;

/**
 * Created by Dylan on 17/03/17.
 * <p>
 * Represents a Node in a Trie structure,
 *
 * @param <DataT> The type of data to store in this {@link Trie}
 */
public class Trie<DataT> {

    private final boolean isRoot;
    private final Character character;
    /**
     * Should not be empty if this {@link Trie} is the last character in a word
     */
    private Collection<DataT> datas = new ArrayList<>();
    private NavigableMap<Character, Trie<DataT>> nextCharacters = new TreeMap<>();

    /**
     * @param data Can be null
     */
    public Trie(char character, DataT data) {
        this.character = character;
        if (data != null) this.datas.add(data);
        this.isRoot = false;
    }

    /**
     * Used to make a root Trie
     */
    public Trie() {
        this.datas = null;
        this.character = null;
        this.isRoot = true;
    }

    public char getCharacter() {
        throwIfRoot();
        if (character == null) throw new AssertionError();
        return character;
    }

    public SortedSet<Character> getNextCharacters() {
        return Collections.unmodifiableSortedSet(nextCharacters.navigableKeySet());
    }

    /**
     * Gets the datas for the current object
     */
    public Collection<DataT> getDatas() {
        throwIfRoot();
        return Collections.unmodifiableCollection(datas);
    }

    /**
     * @return The {@link Trie} that represents the next character, or null
     * if none exists
     */
    public Trie<DataT> getTrieForNextChar(char c) {
        return nextCharacters.get(c);
    }

    public void addNextChar(char character, DataT data) {
        Trie<DataT> nextCharTrie = nextCharacters.get(character);
        if (nextCharTrie != null) {
            // Next character already exists
            if (data != null) nextCharTrie.datas.add(data);
            return;
        }

        nextCharTrie = new Trie<>(character, data);
        nextCharacters.put(character, nextCharTrie);
    }

    /**
     * @param charSequence Chars where the first char represents the next
     *                     {@link Trie}
     * @return null if no match found
     */
    public Trie<DataT> getSubTrieForChars(CharSequence charSequence) {
        if (charSequence.length() == 0) return this;

        Trie<DataT> trie = nextCharacters.get(charSequence.charAt(0));
        if (trie == null) return null;

        return trie.getSubTrieForChars(charSequence.subSequence(
                1,
                charSequence.length()
        ));
    }

    /**
     * Finds DataT objects that are inside this {@link Trie} and all sub-Tries
     * (this assumes that this is the correct Trie for a search term).
     */
    public List<DataT> getDatasRecursive(int maxMatches) {
        List<DataT> matches = new ArrayList<>();

        for (DataT data : datas) {
            if (matches.size() >= maxMatches) return matches;
            matches.add(data);
        }

        nextCharacters.forEach((character, trie) -> {
            if (matches.size() >= maxMatches) return;
            List<DataT> subMatches =
                    trie.getDatasRecursive(maxMatches - matches.size());
            matches.addAll(subMatches);
        });

        return matches;
    }

    private void throwIfRoot() {
        if (isRoot) {
            throw new UnsupportedOperationException("This cannot be done by a root");
        }
    }
}
