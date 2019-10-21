package summarisation.parser

class Token : Comparable<Token> {

    var text = ""        // original text
    var lemma = ""       // stemmed text
    var tag = ""         // srl tag
    var index = 0        // token's index in the parse tree

    constructor() {}

    constructor(text: String, lemma: String, tag: String, index: Int) {
        this.text = text
        this.lemma = lemma
        this.tag = tag
        this.index = index
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(text)
        if (tag.isNotEmpty()) {
            sb.append(" /").append(tag)
        }
        if (lemma.isNotEmpty() && lemma != text) {
            sb.append(" /lemma:").append(lemma)
        }
        return sb.toString()
    }

    override fun compareTo(other: Token): Int {
        if (index < other.index) return -1
        return if (index > other.index) 1 else 0
    }

}

