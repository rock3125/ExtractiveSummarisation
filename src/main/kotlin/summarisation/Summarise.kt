package summarisation

import summarisation.parser.Sentence
import summarisation.parser.StanfordParser
import summarisation.parser.Token
import summarisation.parser.Undesirables

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Extractive Summarization of text - simple algorithm that scores texts
 * based on position in story, numbers, proper nouns, thematic relationships between title and sentences,
 * cosine relationships between sentences, and themes based on frequencies
 *
 */
class Summarise {

    private val undesirables = Undesirables()   // stop word detector
    private val parser = StanfordParser()       // stanford parser for tagging and sentence boundary detection

    init {
        parser.init()
    }

    /**
     * perform a text summarization - assumes first line of book is the "title" (And will add a full stop to this line)
     *
     * @param text the text to summary
     * @param topN the top scoring item count to return
     * @param sortBySentenceAfterTopN resort by story order after topN have been cut-off
     * @return if valid a top list of n sentences
     */
    fun summarize(text: String, topN: Int, sortBySentenceAfterTopN: Boolean): ArrayList<Sentence> {
        if (topN > 0) {
            val result = preProcessText(text)
            if (result != null) {
                val sentenceScoreList = scoreSentences(result)
                return SummariseSentenceScore.getTopN(result.originalSentenceList, sentenceScoreList, topN,
                                                      sortBySentenceAfterTopN)
            }
        }
        return ArrayList()
    }

    /**
     * pre-process all the text - return a summary of the word frequencies and the parsed text itself
     *
     * @param text the text to process
     * @return the pre processing results for this text
     */
    private fun preProcessText(_text: String): SummarisePreProcessResult? {
        // split the title "cleverly"
        var text = _text
        val parts = text.split("\n")
        val sb = StringBuilder()
        var counter = 0
        for (part in parts) {
            sb.append(part)
            if (counter == 0) {
                sb.append(".")
            }
            sb.append("\n")
            counter += 1
        }
        text = sb.toString()

        val sentenceList = parser.parse(text)
        val frequencyMap = HashMap<String, Int>()
        val finalSentenceList = ArrayList<Sentence>()
        var longestSentence = 0
        for (sentence in sentenceList) {
            val newTokenList = ArrayList<Token>()
            for (token in sentence.tokenList) {
                if (!undesirables.contains(token.lemma.toLowerCase())) {
                    newTokenList.add(token)
                }
            }
            if (newTokenList.isNotEmpty()) {
                for (t in newTokenList) {
                    val lemma = t.lemma.toLowerCase()
                    if (!frequencyMap.containsKey(lemma)) {
                        frequencyMap[lemma] = 1
                    } else {
                        frequencyMap[lemma] = frequencyMap[lemma]!! + 1
                    }
                }
            }
            // allow empty sentences to make tokenized match original
            finalSentenceList.add(Sentence(newTokenList))
            if (newTokenList.size > longestSentence) {
                longestSentence = newTokenList.size
            }
        }
        if (finalSentenceList.isNotEmpty()) {
            val title = finalSentenceList[0].tokenList
            return SummarisePreProcessResult(sentenceList, finalSentenceList, frequencyMap, longestSentence, title)
        }
        return null
    }

    /**
     * given the processed results (parsed data) - apply the scoring algorithms across all sentences
     *
     * @param results the pre-processing results ready to be processed
     * @return a list of scores, one for each sentence - additive between the different algorithms applied,
     * the highest score is assumed to be the most relevant / representative sentence
     */
    private fun scoreSentences(results: SummarisePreProcessResult?): ArrayList<Float> {
        if (results == null)
            return ArrayList()

        val sentenceList = results.tokenizedSentenceList
        val title = results.title
        val longestSentence = results.longestSentence
        val wordCount = results.wordCount

        val resultMap = HashMap<Int, Float>()
        for (i in sentenceList.indices)
            resultMap[i] = 0.0f

        val titleFeatures = getTitleFeatures(sentenceList, title)
        for (i in sentenceList.indices)
            resultMap[i] = resultMap[i]!! + titleFeatures[i]

        val sentenceLength = getSentenceLengthFeatures(sentenceList, longestSentence)
        for (i in sentenceList.indices)
            resultMap[i] = resultMap[i]!! + sentenceLength[i]

        val tfifs = getTfIsf(sentenceList, wordCount)
        for (i in sentenceList.indices)
            resultMap.put(i, resultMap[i]!! + tfifs[i])

        val sentencePos = getSentencePositionRating(sentenceList, sentenceList.size / 4)
        for (i in sentenceList.indices)
            resultMap.put(i, resultMap[i]!! + sentencePos[i])

        // O(n2) - very expensive to run
        //        List<Float> sentenceSim = getSentenceSimilarity(sentenceList);
        //        for (int i = 0; i < sentenceList.size(); i++)
        //            resultMap.put(i, resultMap[i]!! + sentenceSim.get(i));

        val properNouns = getProperNounFeatures(sentenceList)
        for (i in sentenceList.indices)
            resultMap.put(i, resultMap[i]!! + properNouns[i])

        val thematicWords = getThematicFeatures(sentenceList, wordCount, 10)
        for (i in sentenceList.indices)
            resultMap.put(i, resultMap[i]!! + thematicWords[i])

        val numericData = getNumericalFeatures(sentenceList)
        for (i in sentenceList.indices)
            resultMap.put(i, resultMap[i]!! + numericData[i])

        val sentenceRatingList = ArrayList<Float>()
        for (i in sentenceList.indices)
            sentenceRatingList.add(resultMap[i]!!)

        return sentenceRatingList
    }

    /**
     * return a score for each sentence vis a vie title scoring
     * @param sentenceList the list of sentences to check
     * @param title the title's tokens
     * @return a list of floats, one for each sentence on how it scores
     */
    private fun getTitleFeatures(sentenceList: List<Sentence>, title: List<Token>): List<Float> {
        // setup a faster lookup
        val titleLookup = HashSet<String>()
        for (token in title) {
            titleLookup.add(token.lemma.toLowerCase())
        }
        val sentenceTitleFeatures = ArrayList<Float>()
        for (sentence in sentenceList) {
            var count = 0.0f
            if (title.isNotEmpty()) {
                for (token in sentence.tokenList) {
                    if (titleLookup.contains(token.lemma.toLowerCase())) {
                        count += 1.0f
                    }
                }
            }
            sentenceTitleFeatures.add(count / title.size.toFloat())
        }
        return sentenceTitleFeatures
    }

    /**
     * return a list of how the sentences correspond to the longest sentence from [0.0, 1.0]
     * @param sentenceList the list of sentences to check
     * @param longestSentence the size of the longest sentence
     * @return a list of scores for each sentence one
     */
    private fun getSentenceLengthFeatures(sentenceList: List<Sentence>, longestSentence: Int): List<Float> {
        val sentenceLengthFeatures = ArrayList<Float>()
        val longestS = longestSentence.toFloat()
        for (sentence in sentenceList) {
            if (longestS > 0.0f) {
                sentenceLengthFeatures.add(sentence.tokenList.size.toFloat() / longestS)
            } else {
                sentenceLengthFeatures.add(0.0f)
            }
        }
        return sentenceLengthFeatures
    }

    /**
     * return a count of the number of sentences to token appears in
     * @param sentenceList the set of sentences
     * @param token the token to check
     * @return the count of the number of sentences token appears in
     */
    private fun getWordSentenceCount(sentenceList: List<Sentence>, token: Token, cache: HashMap<String, Int>): Int {
        if (cache.containsKey(token.lemma.toLowerCase())) {
            return cache[token.lemma.toLowerCase()]!!
        } else {
            var numSentences = 0
            for (sentence in sentenceList) {
                for (t in sentence.tokenList) {
                    if (t.lemma.compareTo(token.lemma, ignoreCase = true) == 0) {
                        numSentences += 1
                        break
                    }
                }
            }
            cache[token.lemma.toLowerCase()] = numSentences
            return numSentences
        }
    }

    /**
     * return a value for each sentence on how it scores with the term/frequency - inverse sentence frequency
     * @param sentenceList the sentences to score
     * @param wordCount the frequency map of all words
     * @return the scores for the sentences
     */
    private fun getTfIsf(sentenceList: List<Sentence>, wordCount: HashMap<String, Int>): List<Float> {
        val sentenceFeatures = ArrayList<Float>()
        val cache = HashMap<String, Int>()
        var largest = 0.0f
        for (sentence in sentenceList) {
            var w = 0.0
            for (token in sentence.tokenList) {
                val n = getWordSentenceCount(sentenceList, token, cache).toDouble()
                w += wordCount[token.lemma.toLowerCase()]!!.toDouble() * ln(sentenceList.size.toDouble() / n)
            }
            sentenceFeatures.add(w.toFloat())
            if (w > largest) {
                largest = w.toFloat()
            }
        }
        return normalize(sentenceFeatures, largest)
    }

    /**
     * normalize list if largest > 0.0 using largest
     * @param list the list to normalize
     * @param largest the normalization max value
     * @return a new list or the old list if largest == 0.0
     */
    private fun normalize(list: List<Float>, largest: Float): List<Float> {
        if (largest > 0.0f) {
            val finalSentenceFeatures = ArrayList<Float>()
            for (value in list) {
                finalSentenceFeatures.add(value / largest)
            }
            return finalSentenceFeatures
        }
        return list
    }


    /**
     * rank the numToRank sentence with a score decending from 1.0 to 0.0
     * @param sentenceList the list of sentences
     * @param numToRank how many to "rank" (default was 5)
     * @return the list of features for these sentences
     */
    private fun getSentencePositionRating(sentenceList: List<Sentence>, numToRank: Int): List<Float> {
        val sentenceFeatures = ArrayList<Float>()
        var n = numToRank.toFloat()
        for (sentence in sentenceList) {
            if (n > 0.0f) {
                sentenceFeatures.add(n / numToRank)
                n -= 1.0f
            } else {
                sentenceFeatures.add(0.0f)
            }
        }
        return sentenceFeatures
    }

    /**
     * map a sentence into frequency items
     * @param sentence the sentence to map
     * @return the words and their frequencies
     */
    private fun map(sentence: Sentence): Map<String, Int> {
        val result = HashMap<String, Int>()
        for (token in sentence.tokenList) {
            val lemma = token.lemma.toLowerCase()
            if (!result.containsKey(lemma)) {
                result[lemma] = 1
            } else {
                result[lemma] = result[lemma]!! + 1
            }
        }
        return result
    }

    /**
     * perform a frequency cosine map between s1 and s2
     * @param s1 sentence set 1 of words with frequencies
     * @param s2 sentence set 2 of words with frequencies
     * @return the cosine similarity between s1 and s2
     */
    private fun getCosine(s1: Map<String, Int>, s2: Map<String, Int>): Float {
        var numerator = 0.0f
        for (key1 in s1.keys) {
            if (s2.containsKey(key1)) {
                numerator += s1[key1]!! * s2[key1]!!
            }
        }
        var sum1 = 0.0f
        for (v1 in s1.values) {
            sum1 += v1 * v1
        }
        var sum2 = 0.0f
        for (v2 in s2.values) {
            sum2 += v2 * v2
        }
        val denominator = (sqrt(sum1) + sqrt(sum2))
        return if (denominator > 0.0f) {
            numerator / denominator
        } else 0.0f
    }

    /**
     * this is a cosine similarity measurement between sentences among each other
     * O(n2) - careful
     *
     * @param sentenceList the list of sentences to apply the algorithm to
     * @return a list of values identifying similarities between other sentences overall
     */
    private fun getSentenceSimilarity(sentenceList: List<Sentence>): List<Float> {
        val sentenceFeatures = ArrayList<Float>()
        var maxS = 0.0f
        for (i in sentenceList.indices) {
            var s = 0.0f
            for (j in sentenceList.indices) {
                if (i != j) {
                    s += getCosine(map(sentenceList[i]), map(sentenceList[j]))
                }
            }
            sentenceFeatures.add(s)
            if (s > maxS) {
                maxS = s
            }
        }
        // normalize?
        return normalize(sentenceFeatures, maxS)
    }


    /**
     * get frequencies for the proper nouns in each sentence
     * @param sentenceList the sentences to check
     * @return a list of proper noun weightings
     */
    private fun getProperNounFeatures(sentenceList: List<Sentence>): List<Float> {
        val sentenceFeatures = ArrayList<Float>()
        for (sentence in sentenceList) {
            var count = 0.0f
            for (token in sentence.tokenList) {
                if (token.tag.compareTo("NNP", ignoreCase = true) == 0 || token.tag.compareTo("NNPS", ignoreCase = true) == 0) {
                    count += 1.0f
                }
            }
            if (sentence.tokenList.isNotEmpty()) {
                sentenceFeatures.add(count / sentence.tokenList.size)
            } else {
                sentenceFeatures.add(0.0f)
            }
        }
        return sentenceFeatures
    }


    /**
     * get thematic (i.e. high frequency) features judgements for sentences
     * @param sentenceList the sentences to consider
     * @param wordCount the existing frequency map of all items
     * @param top the top-n items to score on (a count)
     * @return a list of values for each sentence
     */
    private fun getThematicFeatures(sentenceList: List<Sentence>, wordCount: HashMap<String, Int>, top: Int): List<Float> {
        // grab the top x thematic words
        val thematicWords = HashSet(SummaryFrequencyWord.getTopN(wordCount, top))

        var maxCount = 0.0f
        val sentenceFeatures = ArrayList<Float>()
        for (sentence in sentenceList) {
            var count = 0.0f
            for (token in sentence.tokenList) {
                if (thematicWords.contains(token.lemma.toLowerCase())) {
                    count += 1.0f
                }
            }
            sentenceFeatures.add(count)
            if (count > maxCount) {
                maxCount = count
            }
        }
        return normalize(sentenceFeatures, maxCount)
    }

    // helper: is str a number (1231244) but not a float
    private fun isNumber(str: String): Boolean {
        if (str.trim().isNotEmpty()) {
            val data = str.toCharArray()
            for (ch in data) {
                if (ch !in '0'..'9') {
                    return false
                }
            }
            return true
        }
        return false
    }

    /**
     * get sentences that have numbers in them
     * @param sentenceList the list
     * @return a feature set for this sentence
     */
    private fun getNumericalFeatures(sentenceList: List<Sentence>): List<Float> {
        val sentenceFeatures = ArrayList<Float>()
        for (sentence in sentenceList) {
            var count = 0.0f
            for (token in sentence.tokenList) {
                if (isNumber(token.lemma)) {
                    count += 1.0f
                }
            }
            if (sentence.tokenList.isNotEmpty()) {
                sentenceFeatures.add(count)
            } else {
                sentenceFeatures.add(0.0f)
            }
        }
        return sentenceFeatures
    }


}

