package summarisation.parser;

public class Token implements Comparable<Token> {

    private String text = "";        // original text
    private String lemma = "";       // stemmed text
    private String tag = "";         // srl tag
    private int index;               // token's index in the parse tree

    public Token() {
    }

    public Token(String text, String lemma, String tag, int index) {
        this.text = text;
        this.lemma = lemma;
        this.tag = tag;
        this.index = index;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(text);
        if (tag != null && tag.length() > 0) {
            sb.append(" /").append(tag);
        }
        if (lemma != null && lemma.length() > 0 && !lemma.equals(text)) {
            sb.append(" /lemma:").append(lemma);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Token o) {
        if (index < o.index) return -1;
        if (index > o.index) return 1;
        return 0;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}

