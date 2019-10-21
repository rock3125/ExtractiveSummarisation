package summarisation

import java.util.ArrayList

/**
 * Simple holder of a word and its frequency
 *
 */
class SummaryFrequencyWord(val word: String, val frequency: Int) : Comparable<SummaryFrequencyWord> {

    // highest frequency first sort
    override fun compareTo(other: SummaryFrequencyWord): Int {
        if (frequency < other.frequency) return 1
        return if (frequency > other.frequency) -1 else word.compareTo(other.word, ignoreCase = true)
    }

    companion object {

        /**
         * turn a map of frequency items into a list of highest ranking frequency words
         *
         * @param map the map to process
         * @param topN how many items to return max (must be > 0)
         * @return the top N summary frequency word items in a list
         */
        fun getTopN(map: HashMap<String, Int>, topN: Int): List<String> {
            val sfw = ArrayList<SummaryFrequencyWord>()
            for (key in map.keys) {
                sfw.add(SummaryFrequencyWord(key, map[key]!!))
            }
            sfw.sort()
            val topWordList = ArrayList<String>()
            for (i in 0 until topN) {
                if (i < sfw.size) {
                    topWordList.add(sfw[i].word)
                }
            }
            return topWordList
        }
    }

}

