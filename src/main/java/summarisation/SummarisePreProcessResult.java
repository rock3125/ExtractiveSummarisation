package summarisation;

import java.util.List;
import java.util.Map;

public class SummarisePreProcessResult {

    // stop words removed and words stemmed
    private List<List<String>> tokenizedSentenceList;

    // word (no stop words) -> frequency of that word
    private Map<String, Integer> wordCount;

    // the longest sentence size
    private int longestSentence;

    public SummarisePreProcessResult() {
    }

    public List<List<String>> getTokenizedSentenceList() {
        return tokenizedSentenceList;
    }

    public void setTokenizedSentenceList(List<List<String>> tokenizedSentenceList) {
        this.tokenizedSentenceList = tokenizedSentenceList;
    }

    public Map<String, Integer> getWordCount() {
        return wordCount;
    }

    public void setWordCount(Map<String, Integer> wordCount) {
        this.wordCount = wordCount;
    }

    public int getLongestSentence() {
        return longestSentence;
    }

    public void setLongestSentence(int longestSentence) {
        this.longestSentence = longestSentence;
    }

}
