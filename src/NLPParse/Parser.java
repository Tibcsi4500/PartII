package NLPParse;

import Console.ConsoleInteractions;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private boolean callbackEnabled, skipEnabled, structuredEnabled;
    private List<ContextAction> context;
    private List<Word> buzzwords;

    public Parser(boolean callbackEnabled, boolean skipEnabled, boolean structuredEnabled, List<Word> buzzwords) {
        this.callbackEnabled = callbackEnabled;
        this.skipEnabled = skipEnabled;
        this.structuredEnabled = structuredEnabled;
        this.buzzwords = buzzwords;
    }

    public ContextAction parse(String sentence, List<ContextAction> context){
        this.context = context;
        try{
            return innerParse(sentence);
        } catch (FaultySentenceException e){
            ConsoleInteractions.error(e.faultType.toString());

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

        List<Word> words = new ArrayList<>(wordSet);

        List<List<Word>> possibleWords = getPossibleWords(words, sentence);

        ParseResult initialParseResult = structuredEnabled?structuredParse(possibleWords):unstructuredParse(possibleWords);

        ParseResult matchedParseResult = new ParseResult();
        matchedParseResult.isStructured = initialParseResult.isStructured;
        if(initialParseResult.verb != null){
            matchedParseResult.verb = ContextItem.matchInList(initialParseResult.verb, items);
        }
        if(initialParseResult.object != null){
            matchedParseResult.object = ContextItem.matchInList(initialParseResult.object, items);
        }
        for (ContextItem extraObject : initialParseResult.extraObjects) {
            ContextItem localMatchResultItem = ContextItem.matchInList(extraObject, items);
            if(localMatchResultItem != null){
                matchedParseResult.extraObjects.add(localMatchResultItem);
            }
        }

        List<ContextAction> possibleMatchingActions = new ArrayList<>();
        List<ContextItem> possibleMatchingItems = new ArrayList<>();
        ContextAction.MatchType possibleMatchingType = ContextAction.MatchType.fullmiss;
        for (ContextAction possibleAction : context) {
            ContextAction.MatchType type = possibleAction.match(matchedParseResult);
            switch (type) {
                case complete -> {
                    return new ContextAction(possibleAction, matchedParseResult);
                }
                case noverb -> {
                    possibleMatchingActions.add(possibleAction);
                    possibleMatchingItems.add(possibleAction.verb);
                    possibleMatchingType = ContextAction.MatchType.noverb;
                    break;
                }
                case noobj -> {
                    if(possibleAction.object.couldMatch(initialParseResult.object)){
                        possibleMatchingActions.add(possibleAction);
                        possibleMatchingItems.add(possibleAction.object);
                    }
                    possibleMatchingType = ContextAction.MatchType.noobj;
                    break;
                }
                case noext -> {
                    initialParseResult.extraObjects.add(null);
                    if(possibleAction.extraObjects.get(0).couldMatch(initialParseResult.extraObjects.get(0))){
                        possibleMatchingActions.add(possibleAction);
                        possibleMatchingItems.add(possibleAction.extraObjects.get(0));
                    }
                    possibleMatchingType = ContextAction.MatchType.noext;
                    break;
                }
            }
        }

        if(!callbackEnabled){
            return null;
        }

        Set<Word> callbackWordSet = new HashSet<>();
        for (ContextItem item : possibleMatchingItems) {
            callbackWordSet.add(item.word);
            callbackWordSet.addAll(item.adjectives);
        }
        List<Word> callbackWords = new ArrayList<>(callbackWordSet);

        switch (possibleMatchingType) {
            case noverb -> {
                String promptedVerb = ConsoleInteractions.prompt("What did you mean to do?");
                ParseResult newVerb = structuredParse(getPossibleWords(callbackWords, promptedVerb));
                matchedParseResult.verb = ContextItem.matchInList(newVerb.verb, possibleMatchingItems);
            }
            case noobj -> {
                String possibleMatchingActionString = "";
                for(ContextAction action : possibleMatchingActions){
                    possibleMatchingActionString += action.object + ", ";
                }

                String promptedObject = ConsoleInteractions.prompt(
                        "Which object did you mean to " + matchedParseResult.verb.word.string + "?\n" +
                                possibleMatchingActionString.substring(0, possibleMatchingActionString.length()-2)
                );
                List<List<Word>> tempPoss = getPossibleWords(callbackWords, promptedObject);
                ParseResult newObject = structuredParse(tempPoss);
                matchedParseResult.object = ContextItem.matchInList(newObject.object, possibleMatchingItems);
            }
            case noext -> {
                String possibleMatchingActionString = "";
                for(ContextAction action : possibleMatchingActions){
                    possibleMatchingActionString += action.extraObjects.get(0) + ", ";
                }

                String promptedObject = ConsoleInteractions.prompt(
                        "Which object did you mean to use to " + matchedParseResult.verb.word.string + "?\n" +
                                possibleMatchingActionString.substring(0, possibleMatchingActionString.length()-2)
                );
                List<List<Word>> tempPoss = getPossibleWords(callbackWords, promptedObject);
                ParseResult newObject = structuredParse(tempPoss);
                matchedParseResult.extraObjects.add(ContextItem.matchInList(newObject.object, possibleMatchingItems));
            }
            case fullmiss -> {
                return null;
            }
        }

        for (ContextAction possibleAction : possibleMatchingActions) {
            ContextAction.MatchType type = possibleAction.match(matchedParseResult);
            if(type.equals(ContextAction.MatchType.complete)){
                return new ContextAction(possibleAction, matchedParseResult);
            }
        }

        return null;
    }

    List<List<Word>> getPossibleWords(List<Word> words, String sentence) throws FaultySentenceException{
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

        return possibleWords;
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
                    result.extraObjects.add(new ContextItem(parsedObject, parsedAdjectives));
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
                    result.extraObjects.add(new ContextItem(parsedObject, parsedAdjectives));
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
