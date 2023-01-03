import NLPParse.*;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<ContextAction> context = constructContext();
        Parser parser = new Parser(false, false, false, context);
        parser.parse("grid");
    }

    private static List<ContextAction> constructContext(){
        Word red = new Word("red", Word.Type.ADJECTIVE);
        List<Word> redlist = new ArrayList<>();
        redlist.add(red);
        Word ball = new Word("ball", Word.Type.OBJECT);
        Word grid = new Word("grid", Word.Type.OBJECT);
        Word kick = new Word("kick", Word.Type.VERB);
        ContextItem redballitem = new ContextItem(ball, redlist, null);
        ContextItem redgriditem = new ContextItem(grid, redlist, null);
        ContextItem kickitem = new ContextItem(kick, null);
        ContextAction kickballaction = new ContextAction(kickitem, redballitem);
        ContextAction kickgridaction = new ContextAction(kickitem, redgriditem);
        List<ContextAction> actionList = new ArrayList<>();
        actionList.add(kickballaction);
        actionList.add(kickgridaction);
        return actionList;
    }
}
