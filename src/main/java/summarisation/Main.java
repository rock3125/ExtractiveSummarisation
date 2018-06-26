package summarisation;

import summarisation.parser.Sentence;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        String text = new String(Files.readAllBytes(Paths.get("romeo_and_juliet.txt")));
        Summarise summarise = new Summarise();
        List<Sentence> sentenceList = summarise.summarize(text, 50);
        if (sentenceList != null) {
            for (Sentence sentence : sentenceList) {
                System.out.println(sentence.toString());
            }
        }


    }

}
