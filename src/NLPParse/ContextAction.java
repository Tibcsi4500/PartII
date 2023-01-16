package NLPParse;

import java.util.ArrayList;
import java.util.List;

public class ContextAction {
    public ContextItem verb, object;
    public List<ContextItem> extraObjects;

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

    public static MatchType match(ParseResult target){

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

    public enum MatchType{
        full,
        noverb,
        verbmiss,
        noobj,
        objmiss,
        extmiss,
        fullmiss
    }
}
