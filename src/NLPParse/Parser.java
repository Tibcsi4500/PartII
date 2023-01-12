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
        System.out.println(words);

        List<String> sentenceTokens = Arrays.stream(sanitise(sentence).split(" ")).toList();
        List<List<Word>> possibleWords = new ArrayList<>();
        for (String token : sentenceTokens) {
            for (int i = 0; i < token.length(); i++) {
                System.out.print(token.charAt(i));
                System.out.print(' ');
            }
            System.out.println();
            possibleWords.add(Word.matchWords(words, token));
        }

        System.out.println(possibleWords);

        return null;
    }
    public ParseResult structuredParse(){return null;}
    public ParseResult unstructuredParse(){return null;}

    String sanitise(String sentence){
        String cut = sentence.toLowerCase().strip();
        String result = "";
        boolean space = false;
        for (int i = 0; i < cut.length(); i++) {
            char c = cut.charAt(i);
            if(c >= 'a' && c <= 'z'){
                result += c;
                space = false;
            }
            if(c == ' ' && !space){
                result += c;
                space = true;
            }
        }

        return result;
    }
}
