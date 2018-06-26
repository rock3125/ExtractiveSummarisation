package summarisation;

import summarisation.parser.Sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SummariseSentenceScore implements Comparable<SummariseSentenceScore> {

    private Sentence sentence;
    private float score;

    public SummariseSentenceScore(Sentence sentence, float score) {
        this.sentence = sentence;
        this.score = score;
    }

    /**
     * generate the sorted summary
     * @param sentenceList the sentences
     * @param scoreList the scores of these sentences
     * @param topN the top n items to return
     * @return the top n items
     */
    public static List<Sentence> getTopN(List<Sentence> sentenceList, List<Float> scoreList, int topN) {
        List<SummariseSentenceScore> results = new ArrayList<>();
        for (int i = 0; i < sentenceList.size(); i++) {
            results.add(new SummariseSentenceScore(sentenceList.get(i), scoreList.get(i)));
        }
        Collections.sort(results);
        List<Sentence> summary = new ArrayList<>();
        for (int i = 0; i < topN; i++) {
            if (i < results.size()) {
                summary.add(results.get(i).getSentence());
            }
        }
        return summary;
    }

    @Override
    public int compareTo(SummariseSentenceScore o) {
        if (score < o.score) return -1;
        if (score > o.score) return 1;
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

}
