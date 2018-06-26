package summarisation;

import summarisation.parser.Sentence;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    /**
     * run the summariser using the cmd line
     * @param args the file-name passed in (see below)
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("usage: summarise /path/to/text-file.txt num_lines_to_return");
            System.exit(1);
        }

        String text = new String(Files.readAllBytes(Paths.get(args[0])));
        int numLines = Integer.parseInt(args[1]);
        Summarise summarise = new Summarise();
        List<Sentence> sentenceList = summarise.summarize(text, numLines, true);
        if (sentenceList != null) {
            for (Sentence sentence : sentenceList) {
                System.out.println(sentence.toString());
            }
        }
    }

}

