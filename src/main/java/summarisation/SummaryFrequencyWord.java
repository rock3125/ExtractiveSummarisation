package summarisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SummaryFrequencyWord implements Comparable<SummaryFrequencyWord> {

    private String word;
    private int frequency;

    public SummaryFrequencyWord(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    /**
     * turn a map of frequency items into a list of highest ranking frequency words
     * @param map the map to process
     * @param topN how many items to return max (must be > 0)
     * @return the top N summary frequency word items in a list
     */
    public static List<String> getTopN(Map<String, Integer> map, int topN) {
        List<SummaryFrequencyWord> sfw = new ArrayList<>();
        for (String key : map.keySet()) {
            sfw.add(new SummaryFrequencyWord(key, map.get(key)));
        }
        Collections.sort(sfw);
        List<String> topWordList = new ArrayList<>();
        for (int i = 0; i < topN; i++) {
            if (i < sfw.size()) {
                topWordList.add(sfw.get(i).getWord());
            }
        }
        return topWordList;
    }

    // highest frequency first sort
    @Override
    public int compareTo(SummaryFrequencyWord o) {
        if (frequency < o.frequency) return 1;
        if (frequency > o.frequency) return -1;
        return word.compareToIgnoreCase(o.word);
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

}
