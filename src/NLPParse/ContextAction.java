package NLPParse;

import MapEssentials.GameStatePiece;

import java.util.ArrayList;
import java.util.List;

public class ContextAction {
    public ContextItem verb, object;
    public List<ContextItem> extraObjects;

    public List<GameStatePiece> requirements, effects;

    public List<ContextItem> getAllContextItems(){
        if(verb == null || object == null || extraObjects == null){
            return new ArrayList<ContextItem>();
        }

        ArrayList<ContextItem> result = new ArrayList<>();
        result.add(verb);
        result.add(object);
        result.addAll(extraObjects);

        return result;
    }

    public MatchType match(ParseResult target){
        int score = 0, miss = 0;
        MatchType type = MatchType.complete;

        if(target.verb != null){
            if(target.verb == verb){
                score++;
            } else {
                return MatchType.fullmiss;
            }
        } else {
            type = MatchType.noverb;
            miss++;
        }

        if(target.object != null){
            if(target.object == object){
                score++;
            } else {
                return MatchType.fullmiss;
            }
        } else {
            type = MatchType.noobj;
            miss++;
        }

        if(extraObjects.isEmpty()){
            score++;
        } else {
            if(target.extraObjects.isEmpty()){
                miss++;
                type = MatchType.noext;
            } else {
                if(extraObjects.get(0) == target.extraObjects.get(0)){
                    score++;
                } else {
                    return MatchType.fullmiss;
                }
            }
        }

        if(miss < 2){
            return type;
        } else {
            return MatchType.fullmiss;
        }
    }

    public ContextAction(ContextItem verb, ContextItem object) {
        this.verb = verb;
        this.object = object;
        this.extraObjects = new ArrayList<>();
    }

    public ContextAction(ContextItem verb, ContextItem object, List<ContextItem> extraObjects) {
        this.verb = verb;
        this.object = object;
        this.extraObjects = extraObjects;
    }

    @Override
    public String toString() {
        return "ContextAction{" +
                "verb=" + verb +
                ", object=" + object +
                ", extraObjects=" + extraObjects +
                '}';
    }

    public enum MatchType{
        complete,
        noverb,
        noobj,
        noext,
        fullmiss
    }
}
