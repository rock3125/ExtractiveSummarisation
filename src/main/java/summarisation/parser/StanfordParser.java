package summarisation.parser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * use the stanford CoreNLP parser to parse documents / text
 *
 */
public class StanfordParser {

    private final static String parserModelFilename = "resources/stanford/englishSR.ser.gz";

    private final static String posModelFilename = "resources/stanford/english-left3words-distsim.tagger";

    // the parser pipeline
    private StanfordCoreNLP pipeline;


    public StanfordParser() {
    }

    // setup the parser
    public void init() {
        // build pipeline
        pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(
                "annotators", "tokenize,ssplit,pos,lemma",
                "parse.model", parserModelFilename,
                "pos.model", posModelFilename,
                "tokenize.language", "en"));
    }

    /**
     * test the parser after spring
     */
    public void test() throws InterruptedException {
        boolean connected; // always wait for the parser
        do {
            try {
                parse("We are fully operational.");
                connected = true;

            } catch (IOException ex) {
                connected = false;
                Thread.sleep(5000);
            }

        } while (!connected);
    }

    /**
     * parse text to NLP structure using stanford pipeline
     * @param text the text to parse
     * @return a list of sentences that represent the document
     */
    public List<Sentence> parse(String text) throws IOException {

        List<Sentence> sentenceList = new ArrayList<>();
        if (text.trim().length() == 0)
            return sentenceList;

        // create a document object
        CoreDocument document = new CoreDocument(text);

        // annotate the document
        pipeline.annotate(document);

        int documentIndex = 0;
        for (CoreSentence cs : document.sentences()) {
            List<Token> tokenList = new ArrayList<>();
            List<CoreLabel> labels = cs.tokens();
            for (CoreLabel label : labels) {
                tokenList.add(new Token(label.originalText(), label.lemma(), label.tag(), documentIndex));
                documentIndex += 1;
            }
            Sentence sentence = new Sentence(tokenList);
            sentenceList.add(sentence);
        }

        return sentenceList;
    }


}

