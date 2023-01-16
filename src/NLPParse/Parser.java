package NLPParse;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private boolean callbackEnabled, skipEnabled, structuredEnabled;
    private List<ContextAction> context;
    private List<Word> buzzwords;

    public Parser(boolean callbackEnabled, boolean skipEnabled, boolean structuredEnabled, List<ContextAction> context, List<Word> buzzwords) {
        this.callbackEnabled = callbackEnabled;
        this.skipEnabled = skipEnabled;
        this.structuredEnabled = structuredEnabled;
        this.context = context;
        this.buzzwords = buzzwords;
    }

    public ContextAction parse(String sentence){
        try{
            return innerParse(sentence);
        } catch (FaultySentenceException e){
            System.out.println("Error: " + e.faultType.toString());

            return null;
        }
    }

    private ContextAction innerParse(String sentence) throws FaultySentenceException{
        List<ContextItem> items = new ArrayList<>();
        for (ContextAction action : context) {
            items.addAll(action.getAllContextItems());
        }

        Set<Word> wordSet = new HashSet<>();
        for(ContextItem item : items){
            wordSet.addAll(item.getAllWords());
        }
        wordSet.addAll(buzzwords);

        List<Word> words = wordSet.stream().collect(Collectors.toList());
        System.out.println(words);

        List<String> sentenceTokens = Arrays.stream(sanitise(sentence).split(" ")).collect(Collectors.toList());
        List<List<Word>> possibleWords = new ArrayList<>();
        for (String token : sentenceTokens) {
            List<Word> possibilities = Word.matchWords(words, token);

            if(possibilities.isEmpty() && !skipEnabled){
                throw new FaultySentenceException(token, FaultySentenceException.FaultType.fillerword);
            }

            if(!possibilities.isEmpty() && !Word.mainType(possibilities).equals(Word.Type.BUZZWORD)){
                possibleWords.add(possibilities);
            }
        }

        ParseResult initialParseResult = structuredEnabled?structuredParse(possibleWords):unstructuredParse(possibleWords);
        System.out.println(initialParseResult);

        ParseResult matchedParseResult = new ParseResult();
        matchedParseResult.isStructured = initialParseResult.isStructured;
        matchedParseResult.verb = ContextItem.matchInList(initialParseResult.verb, items);
        matchedParseResult.object = ContextItem.matchInList(initialParseResult.object, items);
        for (ContextItem extraObject : initialParseResult.extraObjects) {
            ContextItem localMatchResultItem = ContextItem.matchInList(extraObject, items);
            if(localMatchResultItem != null){
                matchedParseResult.extraObjects.add(localMatchResultItem);
            }
        }
        System.out.println(matchedParseResult);



        return null;
    }
    public ParseResult structuredParse(List<List<Word>> possibleWords) throws FaultySentenceException{
        ParseResult result = new ParseResult();
        result.isStructured = true;
        if(possibleWords == null || possibleWords.isEmpty()){
            return result;
        }

        int currentIndex = 0;
        if(Word.hasType(possibleWords.get(currentIndex), Word.Type.VERB)){
            result.verb = new ContextItem(Word.getOfType(possibleWords.get(currentIndex), Word.Type.VERB), null);
            currentIndex++;
        }

        while(currentIndex < possibleWords.size()){
            Word parsedObject;
            List<Word> parsedAdjectives = new ArrayList<>();
            while(true){
                if(currentIndex >= possibleWords.size()){
                    throw new FaultySentenceException(FaultySentenceException.FaultType.unexpectedobjectend);
                }
                if(Word.hasType(possibleWords.get(currentIndex), Word.Type.OBJECT)){
                    parsedObject = Word.getOfType(possibleWords.get(currentIndex), Word.Type.OBJECT);
                    result.extraObjects.add(new ContextItem(parsedObject, parsedAdjectives, null));
                    currentIndex++;
                    break;
                } else if(Word.hasType(possibleWords.get(currentIndex), Word.Type.ADJECTIVE)){
                    parsedAdjectives.add(Word.getOfType(possibleWords.get(currentIndex), Word.Type.ADJECTIVE));
                    currentIndex++;
                } else{
                    throw new FaultySentenceException(
                            possibleWords.get(currentIndex).get(0).string,
                            FaultySentenceException.FaultType.nonobject
                    );
                }
            }
        }

        if(!result.extraObjects.isEmpty()){
            result.object = result.extraObjects.get(0);
            result.extraObjects.remove(0);
        }

        return result;
    }
    public ParseResult unstructuredParse(List<List<Word>> possibleWords) throws FaultySentenceException{
        ParseResult result = new ParseResult();
        result.isStructured = false;

        Word verb = null;
        int verbIndex = -1;
        for (int wordIndex = 0; wordIndex < possibleWords.size(); wordIndex++) {
            if(Word.hasType(possibleWords.get(wordIndex), Word.Type.VERB)){
                verb = Word.getOfType(possibleWords.get(wordIndex), Word.Type.VERB);
                verbIndex = wordIndex;
            }
        }

        result.verb = new ContextItem(verb, null);

        for (int currentIndex = 0; currentIndex < possibleWords.size(); currentIndex++) {
            if(currentIndex == verbIndex){
                continue;
            }

            Word parsedObject;
            List<Word> parsedAdjectives = new ArrayList<>();
            while(true){
                if(currentIndex >= possibleWords.size()){
                    throw new FaultySentenceException(FaultySentenceException.FaultType.unexpectedobjectend);
                }
                if(Word.hasType(possibleWords.get(currentIndex), Word.Type.OBJECT)){
                    parsedObject = Word.getOfType(possibleWords.get(currentIndex), Word.Type.OBJECT);
                    result.extraObjects.add(new ContextItem(parsedObject, parsedAdjectives, null));
                    break;
                } else if(Word.hasType(possibleWords.get(currentIndex), Word.Type.ADJECTIVE)){
                    parsedAdjectives.add(Word.getOfType(possibleWords.get(currentIndex), Word.Type.ADJECTIVE));
                    currentIndex++;
                } else{
                    throw new FaultySentenceException(
                            possibleWords.get(currentIndex).get(0).string,
                            FaultySentenceException.FaultType.nonobject
                    );
                }
            }
        }

        if(!result.extraObjects.isEmpty()){
            result.object = result.extraObjects.get(0);
            result.extraObjects.remove(0);
        }

        return result;
    }

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
