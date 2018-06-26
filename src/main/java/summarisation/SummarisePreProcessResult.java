package summarisation;

import summarisation.parser.Sentence;

import java.util.List;
import java.util.Map;

public class SummarisePreProcessResult {

    // stop words removed and words stemmed
    private List<Sentence> tokenizedSentenceList;

    // word (no stop words) -> frequency of that word
    private Map<String, Integer> wordCount;

    // the longest sentence size
    private int longestSentence;

    public SummarisePreProcessResult(List<Sentence> tokenizedSentenceList, Map<String, Integer> wordCount, int longestSentence) {
        this.tokenizedSentenceList = tokenizedSentenceList;
        this.wordCount = wordCount;
        this.longestSentence = longestSentence;
    }

    public List<Sentence> getTokenizedSentenceList() {
        return tokenizedSentenceList;
    }

    public void setTokenizedSentenceList(List<Sentence> tokenizedSentenceList) {
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
