package summarisation;

import org.junit.Assert;
import org.junit.Test;
import summarisation.parser.Sentence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SummariseTest {

    // test summarize Romeo and Juliet in 10 sentences
    @Test
    public void testSummarisation1() throws IOException {
        String text = new String(Files.readAllBytes(Paths.get("resources/test/romeo_and_juliet.txt")));
        Summarise summarise = new Summarise();
        // A Summary of Romeo and Juliet in 10 lines
        List<Sentence> sentenceList = summarise.summarize(text, 10, true);
        Assert.assertTrue(sentenceList != null && sentenceList.size() == 10);
        for (Sentence sentence : sentenceList) {
            Assert.assertTrue(sentence != null && sentence.getTokenList().size() > 0);
            System.out.println(sentence.toString());
        }
    }


}

