package summarisation;

import summarisation.parser.Sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SummariseSentenceScore implements Comparable<SummariseSentenceScore> {

    private Sentence sentence;  // the sentence held
    private int sentenceIndex;  // original index of the sentence in the story
    private float score;        // the summary score of this item
    private int sortKey;        // sorting: 1 = by score (default), 2 = sentenceIndex

    public SummariseSentenceScore(Sentence sentence, int sentenceIndex, float score) {
        this.sentence = sentence;
        this.sentenceIndex = sentenceIndex;
        this.score = score;
        this.sortKey = 1;
    }

    /**
     * generate the sorted summary
     * @param sentenceList the sentences
     * @param scoreList the scores of these sentences
     * @param topN the top n items to return
     * @param sortBySentenceAfterTopN resort by story order after topN have been cut-off
     * @return the top n items
     */
    public static List<Sentence> getTopN(List<Sentence> sentenceList, List<Float> scoreList, int topN,
                                         boolean sortBySentenceAfterTopN) {
        List<SummariseSentenceScore> results = new ArrayList<>();
        for (int i = 0; i < sentenceList.size(); i++) {
            results.add(new SummariseSentenceScore(sentenceList.get(i), i, scoreList.get(i)));
        }
        Collections.sort(results);
        List<SummariseSentenceScore> summary = new ArrayList<>();
        for (int i = 0; i < topN; i++) {
            if (i < results.size()) {
                SummariseSentenceScore sss = results.get(i);
                sss.sortKey = 2;  // change sort-key just in case we need resorting
                summary.add(sss);
            }
        }
        if (sortBySentenceAfterTopN) {
            Collections.sort(summary);
        }
        List<Sentence> sentenceSummary = new ArrayList<>();
        for (SummariseSentenceScore sss : summary) {
            sentenceSummary.add(sss.getSentence());
        }
        return sentenceSummary;
    }

    // sort highest scores first
    @Override
    public int compareTo(SummariseSentenceScore o) {
        if (sortKey == 1) {
            if (score < o.score) return 1;
            if (score > o.score) return -1;
        } else {
            if (sentenceIndex < o.sentenceIndex) return -1;
            if (sentenceIndex > o.sentenceIndex) return 1;
        }
        return 0;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getSentenceIndex() {
        return sentenceIndex;
    }

    public void setSentenceIndex(int sentenceIndex) {
        this.sentenceIndex = sentenceIndex;
    }

}
