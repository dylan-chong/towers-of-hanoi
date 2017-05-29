package assignment5;

/**
 * Created by Dylan on 30/05/17.
 */

public interface SearchableString {
    int NO_MATCH_FOUND = -1;

    int search(String pattern, String text);
}
