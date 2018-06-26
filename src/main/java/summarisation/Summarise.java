package summarisation;

import summarisation.parser.Sentence;
import summarisation.parser.StanfordParser;
import summarisation.parser.Token;
import summarisation.parser.Undesirables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Summarise {

    private Undesirables undesirables;  // stop words
    private StanfordParser parser; // parsing things


    public Summarise() {
        undesirables = new Undesirables();
        parser = new StanfordParser();
        parser.init();
    }

    /**
     * pre-process all the text - return a summary of the word frequencies and the parsed text itself
     *
     * @param text the text to process
     * @return the pre processing results for this text
     */
    public SummarisePreProcessResult preProcessText(String text) throws IOException {
        List<Sentence> sentenceList = parser.parse(text);
        Map<String, Integer> frequencyMap = new HashMap<>();
        List<Sentence> finalSentenceList = new ArrayList<>();
        int longestSentence = 0;
        for (Sentence sentence : sentenceList) {
            List<Token> newTokenList = new ArrayList<>();
            for (Token token :sentence.getTokenList()) {
                if (!undesirables.contains(token.getLemma())) {
                    newTokenList.add(token);
                }
            }
            if (newTokenList.size() > 0) {
                for (Token t : newTokenList) {
                    if (!frequencyMap.containsKey(t.getLemma())) {
                        frequencyMap.put(t.getLemma(), 1);
                    } else {
                        frequencyMap.put(t.getLemma(), frequencyMap.get(t.getLemma()) + 1);
                    }
                }
                finalSentenceList.add(new Sentence(newTokenList));
                if (newTokenList.size() > longestSentence) {
                    longestSentence = newTokenList.size();
                }
            }
        }
        return new SummarisePreProcessResult(finalSentenceList, frequencyMap, longestSentence);
    }


}

