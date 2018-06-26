package summarisation;

import summarisation.parser.Sentence;
import summarisation.parser.StanfordParser;
import summarisation.parser.Token;
import summarisation.parser.Undesirables;

import java.io.IOException;
import java.util.*;

public class Summarise {

    private Undesirables undesirables;  // stop words
    private StanfordParser parser; // parsing things


    public Summarise() {
        undesirables = new Undesirables();
        parser = new StanfordParser();
        parser.init();
    }

    /**
     * perform a text summarization
     *
     * @param text the text to summary
     * @param topN the top scoring item count to return
     * @return if valid a top list of n sentences
     */
    public List<Sentence> summarize(String text, int topN) throws IOException {
        SummarisePreProcessResult result = preProcessText(text);
        if (result != null) {
            List<Sentence> sentenceList = result.getTokenizedSentenceList();
            List<Float> sentenceScoreList = scoreSentences(result);
            return SummariseSentenceScore.getTopN(sentenceList, sentenceScoreList, topN);
        }
        return null;
    }

    /**
     * pre-process all the text - return a summary of the word frequencies and the parsed text itself
     *
     * @param text the text to process
     * @return the pre processing results for this text
     */
    private SummarisePreProcessResult preProcessText(String text) throws IOException {
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
        if (finalSentenceList.size() > 0) {
            List<Token> title = finalSentenceList.get(0).getTokenList();
            return new SummarisePreProcessResult(finalSentenceList, frequencyMap, longestSentence, title);
        }
        return null;
    }

    private List<Float> scoreSentences(SummarisePreProcessResult results) throws IOException {

        if (results == null)
            return null;

        List<Sentence> sentenceList = results.getTokenizedSentenceList();
        List<Token> title = results.getTitle();
        int longestSentence = results.getLongestSentence();
        Map<String, Integer> wordCount = results.getWordCount();

        Map<Integer, Float> resultMap = new HashMap<>();
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, 0.0f);

        List<Float> titleFeatures = getTitleFeatures(sentenceList, title);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + titleFeatures.get(i));

        List<Float> sentenceLength = getSentenceLengthFeatures(sentenceList, longestSentence);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + sentenceLength.get(i));

        List<Float> tfifs = getTfIsf(sentenceList, wordCount);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + tfifs.get(i));

        List<Float> sentencePos = getSentencePositionRating(sentenceList, sentenceList.size() / 4);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + sentencePos.get(i));

        List<Float> sentenceSim = getSentenceSimilarity(sentenceList);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + sentenceSim.get(i));

        List<Float> properNouns = getProperNounFeatures(sentenceList);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + properNouns.get(i));

        List<Float> thematicWords = getThematicFeatures(sentenceList, wordCount, 10);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + thematicWords.get(i));

        List<Float> numericData = getNumericalFeatures(sentenceList);
        for (int i = 0; i < sentenceList.size(); i++)
            resultMap.put(i, resultMap.get(i) + numericData.get(i));

        List<Float> sentenceRatingList = new ArrayList<>();
        for (int i = 0; i < sentenceList.size(); i++) {
            sentenceRatingList.add(resultMap.get(i));
        }
        return sentenceRatingList;
    }

    /**
     * return a score for each sentence vis a vie title scoring
     * @param sentenceList the list of sentences to check
     * @param title the title's tokens
     * @return a list of floats, one for each sentence on how it scores
     */
    private List<Float> getTitleFeatures(List<Sentence> sentenceList, List<Token> title) {
        // setup a faster lookup
        Set<String> titleLookup = new HashSet<>();
        for (Token token : title) {
            titleLookup.add(token.getLemma());
        }
        List<Float> sentenceTitleFeatures = new ArrayList<>();
        for (Sentence sentence : sentenceList) {
            float count = 0.0f;
            if (title.size() > 0) {
                for (Token token : sentence.getTokenList()) {
                    if (titleLookup.contains(token.getLemma())) {
                        count += 1.0f;
                    }
                }
            }
            sentenceTitleFeatures.add(count / (float)title.size());
        }
        return sentenceTitleFeatures;
    }

    /**
     * return a list of how the sentences correspond to the longest sentence from [0.0, 1.0]
     * @param sentenceList the list of sentences to check
     * @param longestSentence the size of the longest sentence
     * @return a list of scores for each sentence one
     */
    private List<Float> getSentenceLengthFeatures(List<Sentence> sentenceList, int longestSentence) {
        List<Float> sentenceLengthFeatures = new ArrayList<>();
        float longestS = (float)longestSentence;
        for (Sentence sentence : sentenceList) {
            if (longestS > 0.0f) {
                sentenceLengthFeatures.add((float)sentence.getTokenList().size() / longestS);
            } else {
                sentenceLengthFeatures.add(0.0f);
            }
        }
        return sentenceLengthFeatures;
    }

    /**
     * return a count of the number of sentences to token appears in
     * @param sentenceList the set of sentences
     * @param token the token to check
     * @return the count of the number of sentences token appears in
     */
    private int getWordSentenceCount(List<Sentence> sentenceList, Token token, Map<String, Integer> cache) {
        if (cache.containsKey(token.getLemma())) {
            return cache.get(token.getLemma());
        } else {
            int numSentences = 0;
            for (Sentence sentence : sentenceList) {
                for (Token t : sentence.getTokenList()) {
                    if (t.getLemma().compareToIgnoreCase(token.getLemma()) == 0) {
                        numSentences += 1;
                        break;
                    }
                }
            }
            cache.put(token.getLemma(), numSentences);
            return numSentences;
        }
    }

    /**
     * return a value for each sentence on how it scores with the term/frequency - inverse sentence frequency
     * @param sentenceList the sentences to score
     * @param wordCount the frequency map of all words
     * @return the scores for the sentences
     */
    private List<Float> getTfIsf(List<Sentence> sentenceList, Map<String, Integer> wordCount) {
        List<Float> sentenceFeatures = new ArrayList<>();
        Map<String, Integer> cache = new HashMap<>();
        float largest = 0.0f;
        for (Sentence sentence : sentenceList) {
            float w = 0.0f;
            for (Token token : sentence.getTokenList()) {
                double n = getWordSentenceCount(sentenceList, token, cache);
                w += (double)wordCount.get(token.getLemma()) * Math.log(sentenceList.size() / n);
            }
            sentenceFeatures.add(w);
            if (w > largest) {
                largest = w;
            }
        }
        return normalize(sentenceFeatures, largest);
    }

    /**
     * normalize list if largest > 0.0 using largest
     * @param list the list to normalize
     * @param largest the normalization max value
     * @return a new list or the old list if largest == 0.0
     */
    private List<Float> normalize(List<Float> list, float largest) {
        if (largest > 0.0f) {
            List<Float> finalSentenceFeatures = new ArrayList<>();
            for (float value : list) {
                finalSentenceFeatures.add(value / largest);
            }
            return finalSentenceFeatures;
        }
        return list;
    }


    /**
     * rank the numToRank sentence with a score decending from 1.0 to 0.0
     * @param sentenceList the list of sentences
     * @param numToRank how many to "rank" (default was 5)
     * @return the list of features for these sentences
     */
    private List<Float> getSentencePositionRating(List<Sentence> sentenceList, int numToRank) {
        List<Float> sentenceFeatures = new ArrayList<>();
        float n = numToRank;
        for (Sentence sentence : sentenceList) {
            if (n > 0.0f) {
                sentenceFeatures.add(n / numToRank);
                n -= 1.0f;
            } else {
                sentenceFeatures.add(0.0f);
            }
        }
        return sentenceFeatures;
    }

    /**
     * map a sentence into frequency items
     * @param sentence the sentence to map
     * @return the words and their frequencies
     */
    private Map<String, Integer> map(Sentence sentence) {
        Map<String, Integer> result = new HashMap<>();
        for (Token token : sentence.getTokenList()) {
            String lemma = token.getLemma();
            if (!result.containsKey(lemma)) {
                result.put(lemma, 1);
            } else {
                result.put(lemma, result.get(lemma) + 1);
            }
        }
        return result;
    }

    /**
     * perform a frequency cosine map between s1 and s2
     * @param s1 sentence set 1 of words with frequencies
     * @param s2 sentence set 2 of words with frequencies
     * @return the cosine similarity between s1 and s2
     */
    private float getCosine(Map<String, Integer> s1, Map<String, Integer> s2) {
        float numerator = 0.0f;
        for (String key1 : s1.keySet()) {
            if (s2.containsKey(key1)) {
                numerator += s1.get(key1) * s2.get(key1);
            }
        }
        float sum1 = 0.0f;
        for (float v1 : s1.values()) {
            sum1 += v1 * v1;
        }
        float sum2 = 0.0f;
        for (float v2 : s2.values()) {
            sum2 += v2 * v2;
        }
        float denominator = (float)(Math.sqrt(sum1) + Math.sqrt(sum2));
        if (denominator > 0.0f) {
            return numerator / denominator;
        }
        return 0.0f;
    }

    private List<Float> getSentenceSimilarity(List<Sentence> sentenceList) {
        List<Float> sentenceFeatures = new ArrayList<>();
        float maxS = 0.0f;
        for (int i = 0; i < sentenceList.size(); i++) {
            float s = 0.0f;
            for (int j = 0; j < sentenceList.size(); j++) {
                if (i != j) {
                    s += getCosine(map(sentenceList.get(i)), map(sentenceList.get(j)));
                }
            }
            sentenceFeatures.add(s);
            if (s > maxS) {
                maxS = s;
            }
        }
        // normalize?
        return normalize(sentenceFeatures, maxS);
    }


    /**
     * get frequencies for the proper nouns in each sentence
     * @param sentenceList the sentences to check
     * @return a list of proper noun weightings
     */
    private List<Float> getProperNounFeatures(List<Sentence> sentenceList) {
        List<Float> sentenceFeatures = new ArrayList<>();
        for (Sentence sentence : sentenceList) {
            float count = 0.0f;
            for (Token token : sentence.getTokenList()) {
                if (token.getTag().compareToIgnoreCase("NNP") == 0 ||
                        token.getTag().compareToIgnoreCase("NNPS") == 0) {
                    count += 1.0f;
                }
            }
            if (sentence.getTokenList().size() > 0) {
                sentenceFeatures.add(count / (float)sentence.getTokenList().size());
            } else {
                sentenceFeatures.add(0.0f);
            }
        }
        return sentenceFeatures;
    }


    /**
     * get thematic (i.e. high frequency) features judgements for sentences
     * @param sentenceList the sentences to consider
     * @param wordCount the existing frequency map of all items
     * @param top the top-n items to score on (a count)
     * @return a list of values for each sentence
     */
    private List<Float> getThematicFeatures(List<Sentence> sentenceList, Map<String, Integer> wordCount, int top) {
        // grab the top x thematic words
        Set<String> thematicWords = new HashSet<>(SummaryFrequencyWord.getTopN(wordCount, top));

        float maxCount = 0.0f;
        List<Float> sentenceFeatures = new ArrayList<>();
        for (Sentence sentence : sentenceList) {
            float count = 0.0f;
            for (Token token : sentence.getTokenList()) {
                if (thematicWords.contains(token.getLemma())) {
                    count += 1.0f;
                }
            }
            sentenceFeatures.add(count);
            if (count > maxCount) {
                maxCount = count;
            }
        }
        return normalize(sentenceFeatures, maxCount);
    }

    // helper: is str a number (1231244) but not a float
    private boolean isNumber(String str) {
        if (str.trim().length() > 0) {
            char[] data = str.toCharArray();
            for (char ch : data) {
                if (!(ch >= '0' && ch <= '9')) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * get sentences that have numbers in them
     * @param sentenceList the list
     * @return a feature set for this sentence
     */
    private List<Float> getNumericalFeatures(List<Sentence> sentenceList) {
        List<Float> sentenceFeatures = new ArrayList<>();
        for (Sentence sentence : sentenceList) {
            float count = 0.0f;
            for (Token token : sentence.getTokenList()) {
                if (isNumber(token.getLemma())) {
                    count += 1.0f;
                }
            }
            if (sentence.getTokenList().size() > 0) {
                sentenceFeatures.add(count);
            } else {
                sentenceFeatures.add(0.0f);
            }
        }
        return sentenceFeatures;
    }


}

