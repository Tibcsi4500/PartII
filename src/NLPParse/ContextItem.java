package NLPParse;

import java.util.ArrayList;
import java.util.List;

public class ContextItem {
    public Word word;
    public List<Word> adjectives;
    public Object reference;

    public List<Word> getAllWords(){
        List<Word> result = new ArrayList<>();
        result.add(word);
        result.addAll(adjectives);

        return result;
    }

    public ContextItem(Word word, Object reference) {
        this.word = word;
        this.adjectives = new ArrayList<Word>();
        this.reference = reference;
    }
    public ContextItem(Word word, List<Word> adjectives, Object reference) {
        this.word = word;
        this.adjectives = adjectives;
        this.reference = reference;
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
}
