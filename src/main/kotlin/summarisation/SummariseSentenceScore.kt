package summarisation

import summarisation.parser.Sentence

import java.util.ArrayList


/**
 * sentence sorting by both index and scoring after algorithm completes
 *
 */
class SummariseSentenceScore(var sentence: Sentence  // the sentence held
                             , var sentenceIndex: Int  // original index of the sentence in the story
                             , var score: Float        // the summary score of this item
) : Comparable<SummariseSentenceScore> {
    private var sortKey: Int = 0        // sorting: 1 = by score (default), 2 = sentenceIndex

    init {
        this.sortKey = 1
    }

    // sort highest scores first
    override fun compareTo(other: SummariseSentenceScore): Int {
        if (sortKey == 1) {
            if (score < other.score) return 1
            if (score > other.score) return -1
        } else {
            if (sentenceIndex < other.sentenceIndex) return -1
            if (sentenceIndex > other.sentenceIndex) return 1
        }
        return 0
    }

    companion object {

        /**
         * generate the sorted summary
         *
         * @param sentenceList the sentences
         * @param scoreList the scores of these sentences
         * @param topN the top n items to return
         * @param sortBySentenceAfterTopN resort by story order after topN have been cut-off
         * @return the top n items
         */
        fun getTopN(sentenceList: ArrayList<Sentence>, scoreList: ArrayList<Float>, topN: Int,
                    sortBySentenceAfterTopN: Boolean): ArrayList<Sentence> {
            val results = ArrayList<SummariseSentenceScore>()
            for (i in sentenceList.indices) {
                results.add(SummariseSentenceScore(sentenceList[i], i, scoreList[i]))
            }
            results.sort()
            val summary = ArrayList<SummariseSentenceScore>()
            for (i in 0 until topN) {
                if (i < results.size) {
                    val sss = results[i]
                    sss.sortKey = 2  // change sort-key just in case we need resorting
                    summary.add(sss)
                }
            }
            if (sortBySentenceAfterTopN) {
                summary.sort()
            }
            val sentenceSummary = ArrayList<Sentence>()
            for (sss in summary) {
                sentenceSummary.add(sss.sentence)
            }
            return sentenceSummary
        }
    }

}

