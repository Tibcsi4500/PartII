package NLPParse;

import java.util.*;

public class Word {
    public String string;
    public List<String> synonyms;
    public Type type;
    public boolean isInstance(String target){
        return (string.equals(target) || synonyms.contains(target));
    }

    public static List<Word> matchWords(List<Word> candidates, String target){
        if(candidates == null || target == null){
            return new ArrayList<>();
        }

        List<Word> results = new ArrayList<>();
        for (Word candidate : candidates) {
            if(target == "kick"){
                System.out.println("heyhey");
            }
            if(candidate.isInstance(target)){
                results.add(candidate);
            }
        }
        return results;
    }

    public static boolean hasType(List<Word> list, Type target){
        if(list == null)
            return false;

        for (Word word : list) {
            if(word.type.equals(target))
                return true;
        }
        return false;
    }

    public static Word.Type mainType(List<Word> list){
        if(list == null || list.isEmpty())
            return Type.NONE;
        Type resultType = list.get(0).type;
        for (Word word : list) {
            if(!word.type.equals(resultType)){
                return Type.NONE;
            }
        }
        return resultType;
    }

    public static Word getOfType(List<Word> list, Type target){
        if(list == null)
            return null;

        for (Word word : list) {
            if(word.type.equals(target))
                return word;
        }
        return null;
    }

    public Word(String string, Type type) {
        this.string = string;
        this.synonyms = new ArrayList<>();
        this.type = type;
    }

    public Word(String string, List<String> synonyms, Type type) {
        this.string = string;
        this.synonyms = synonyms;
        this.type = type;
    }

    @Override
    public String toString() {
        return string;
    }

    public enum Type{
        VERB,
        OBJECT,
        ADJECTIVE,
        BUZZWORD,
        NONE
    }
}
