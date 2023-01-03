package NLPParse;

import java.util.*;

public class Parser {
    boolean callbackEnabled, skipEnabled, structuredEnabled;
    List<ContextAction> context;

    public Parser(boolean callbackEnabled, boolean skipEnabled, boolean structuredEnabled, List<ContextAction> context) {
        this.callbackEnabled = callbackEnabled;
        this.skipEnabled = skipEnabled;
        this.structuredEnabled = structuredEnabled;
        this.context = context;
    }

    public ContextAction parse(String sentence){
        List<ContextItem> items = new ArrayList<>();
        for (ContextAction action : context) {
            items.addAll(action.getAllContextItems());
        }

        Set<Word> wordSet = new HashSet<>();
        for(ContextItem item : items){
            wordSet.addAll(item.getAllWords());
        }
        List<Word> words = wordSet.stream().toList();

        

        return null;
    }
    public ParseResult structuredParse(){return null;}
    public ParseResult unstructuredParse(){return null;}
}
