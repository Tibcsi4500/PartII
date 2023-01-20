package NLPParse;

import java.util.ArrayList;
import java.util.List;

public class ContextItem {
    public Word word;
    public List<Word> adjectives;

    public Integer ID;

    public List<Word> getAllWords(){
        List<Word> result = new ArrayList<>();
        result.add(word);
        result.addAll(adjectives);

        return result;
    }

    public static ContextItem matchInList(ContextItem target, List<ContextItem> list){
        if(target.word.type.equals(Word.Type.VERB)){
            for (ContextItem possibleMatchingItem : list) {
                if(possibleMatchingItem.word.equals(target.word)){
                    return possibleMatchingItem;
                }
            }
        } else {
            int bestmatchScore = -1;
            ContextItem bestmatchItem = null;
            for (ContextItem possibleMatchingItem : list) {
                int currentscore = possibleMatchingItem.matchObject(target);

                if(bestmatchScore < currentscore){
                    bestmatchScore = currentscore;
                    bestmatchItem = possibleMatchingItem;
                }
            }
            return bestmatchItem;
        }

        return null;
    }

    public int matchObject(ContextItem target){
        if(!word.equals(target.word)){
            return -1;
        }

        int matchScore = 0;
        if(target.adjectives.size() != adjectives.size()){
            return -1;
        }

        for (Word adjective : adjectives) {
            if(target.adjectives.contains(adjective)){
                matchScore++;
            } else {
                return -1;
            }
        }

        return matchScore;
    }

    public boolean couldMatch(ContextItem target){
        if(target == null){
            return true;
        }

        if(!target.word.equals(word)){
            return false;
        }

        for (Word adjective : target.adjectives) {
            if(!adjectives.contains(adjective)){
                return false;
            }
        }

        return true;
    }

    public ContextItem(Word word, Object reference) {
        this.word = word;
        this.adjectives = new ArrayList<Word>();
    }
    public ContextItem(Word word, List<Word> adjectives, Object reference) {
        this.word = word;
        this.adjectives = adjectives;
    }

    public Integer match(List<Word> targetAdjectives, Word targetWord){
        if(word != targetWord)
            return -1;

        int matchCount = 0;
        for(; matchCount < adjectives.size(); matchCount++){
            if(targetAdjectives.contains(adjectives.get(matchCount)))
                matchCount++;
            else
                return -1;
        }
        return matchCount;
    }

    @Override
    public String toString() {
        String result = "";
        for (Word adjective : adjectives) {
            result += adjective.string + " ";
        }
        return result + word.string;
    }
}
