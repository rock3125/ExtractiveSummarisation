package summarisation;

import summarisation.parser.Sentence;
import summarisation.parser.Token;

import java.util.List;
import java.util.Map;

public class SummarisePreProcessResult {

    // the original sentences
    private List<Sentence> originalSentenceList;

    // stop words removed and words stemmed
    private List<Sentence> tokenizedSentenceList;

    // word (no stop words) -> frequency of that word
    private Map<String, Integer> wordCount;

    // the longest sentence size
    private int longestSentence;

    // the title of this document
    private List<Token> title;


    public SummarisePreProcessResult(List<Sentence> originalSentenceList,
                                     List<Sentence> tokenizedSentenceList, Map<String, Integer> wordCount,
                                     int longestSentence, List<Token> title) {
        this.originalSentenceList = originalSentenceList;
        this.tokenizedSentenceList = tokenizedSentenceList;
        this.wordCount = wordCount;
        this.longestSentence = longestSentence;
        this.title = title;
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

    public List<Token> getTitle() {
        return title;
    }

    public void setTitle(List<Token> title) {
        this.title = title;
    }

    public List<Sentence> getOriginalSentenceList() {
        return originalSentenceList;
    }

    public void setOriginalSentenceList(List<Sentence> originalSentenceList) {
        this.originalSentenceList = originalSentenceList;
    }
}
