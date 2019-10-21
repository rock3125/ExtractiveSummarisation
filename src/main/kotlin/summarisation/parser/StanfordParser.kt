package summarisation.parser

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.util.PropertiesUtils

import java.io.IOException
import java.util.ArrayList


/**
 * use the stanford CoreNLP parser to parse documents / text
 *
 */
class StanfordParser {

    // the parser pipeline
    private var pipeline: StanfordCoreNLP? = null

    // setup the parser
    fun init() {
        // build pipeline
        pipeline = StanfordCoreNLP(PropertiesUtils.asProperties(
                "annotators", "tokenize,ssplit,pos,lemma",
                "parse.model", parserModelFilename,
                "pos.model", posModelFilename,
                "tokenize.language", "en"))
    }

    /**
     * test the parser after spring
     */
    fun test() {
        var connected: Boolean // always wait for the parser
        do {
            try {
                parse("We are fully operational.")
                connected = true

            } catch (ex: IOException) {
                connected = false
                Thread.sleep(5000)
            }

        } while (!connected)
    }

    /**
     * parse text to NLP structure using stanford pipeline
     * @param text the text to parse
     * @return a list of sentences that represent the document
     */
    fun parse(text: String): ArrayList<Sentence> {

        val sentenceList = ArrayList<Sentence>()
        if (text.trim().isEmpty())
            return sentenceList

        // create a document object
        val document = CoreDocument(text)

        // annotate the document
        pipeline!!.annotate(document)

        var documentIndex = 0
        for (cs in document.sentences()) {
            val tokenList = ArrayList<Token>()
            val labels = cs.tokens()
            for (label in labels) {
                tokenList.add(Token(label.originalText(), label.lemma(), label.tag(), documentIndex))
                documentIndex += 1
            }
            val sentence = Sentence(tokenList)
            sentenceList.add(sentence)
        }

        return sentenceList
    }

    companion object {
        private val parserModelFilename = "resources/stanford/englishSR.ser.gz"
        private val posModelFilename = "resources/stanford/english-left3words-distsim.tagger"
    }


}

