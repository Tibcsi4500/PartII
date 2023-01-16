package NLPParse;

import java.util.*;

public class ParseResult {
    public ContextItem verb, object;
    public List<ContextItem> extraObjects;
    public boolean isStructured;
    public ParseResult() {
        extraObjects = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "verb=" + verb +
                ", object=" + object +
                ", extraObjects=" + extraObjects +
                '}';
    }
}
