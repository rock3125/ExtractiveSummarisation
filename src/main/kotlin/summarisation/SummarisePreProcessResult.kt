package summarisation

import summarisation.parser.Sentence
import summarisation.parser.Token

/**
 * temporary results from the pre-processing stage
 *
 */
class SummarisePreProcessResult(// the original sentences
        var originalSentenceList: ArrayList<Sentence>,
        // stop words removed and words stemmed
        var tokenizedSentenceList: ArrayList<Sentence>, // word (no stop words) -> frequency of that word
        var wordCount: HashMap<String, Int>,
        // the longest sentence size
        var longestSentence: Int, // the title of this document
        var title: ArrayList<Token>)

