package assignment5;

/**
 * Created by Dylan on 30/05/17.
 */

public interface StringSearcher {
    int NO_MATCH_FOUND = -1;

    SearchResult search(String pattern, String text);

    class SearchResult {
        public final int matchIndex;
        public final long numChecks;

        public SearchResult(int matchIndex, long numChecks) {
            this.matchIndex = matchIndex;
            this.numChecks = numChecks;
        }
    }
}
