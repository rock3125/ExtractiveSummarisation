package summarisation

import java.nio.file.Files
import java.nio.file.Paths

open class Main

/**
 * run the summariser using the cmd line
 * @param args the file-name passed in (see below)
 */
fun main(args: Array<String>) {

    if (args.size != 2) {
        println("usage: summarise /path/to/text-file.txt num_lines_to_return")
        return
    }

    val text = String(Files.readAllBytes(Paths.get(args[0])))
    val numLines = Integer.parseInt(args[1])
    val summarise = Summarise()
    val sentenceList = summarise.summarize(text, numLines, true)
    for (sentence in sentenceList) {
        println(sentence.toString())
    }
}
