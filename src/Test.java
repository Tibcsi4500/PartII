import Console.ConsoleInteractions;
import NLPParse.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        ConsoleInteractions.initalize();

        List<ContextAction> context = constructContext();
        List<Word> buzzwords = constructBuzzwords();
        Parser parser = new Parser(true, true, true, buzzwords);
        ContextAction finalAction = parser.parse(
                ConsoleInteractions.prompt("Give me a sentence to parse!"),
                context
        );
        System.out.println("The action is: " + finalAction);
    }

    private static List<Word> constructBuzzwords(){
        return Arrays.asList(
                new Word("with", Word.Type.BUZZWORD),
                new Word("on", Word.Type.BUZZWORD),
                new Word("the", Word.Type.BUZZWORD),
                new Word("below", Word.Type.BUZZWORD)
        );
    }

    private static List<ContextAction> constructContext(){
        Word red = new Word("red", Word.Type.ADJECTIVE);
        List<Word> redlist = new ArrayList<>();
        redlist.add(red);
        Word ball = new Word("ball", Word.Type.OBJECT);
        Word grid = new Word("grid", Word.Type.OBJECT);
        Word kick = new Word("kick", Word.Type.VERB);
        ContextItem redballitem = new ContextItem(ball, redlist);
        ContextItem redgriditem = new ContextItem(grid, redlist);
        ContextItem kickitem = new ContextItem(kick);
        ContextAction kickballaction = new ContextAction(kickitem, redballitem);
        ContextAction kickgridaction = new ContextAction(kickitem, redgriditem, Arrays.asList(redballitem));
        ContextAction kickgridaction2 = new ContextAction(kickitem, redgriditem, Arrays.asList(redgriditem));
        List<ContextAction> actionList = new ArrayList<>();
        actionList.add(kickballaction);
        actionList.add(kickgridaction);
        actionList.add(kickgridaction2);

        /*
        Word gridAdj = new Word("grid", Word.Type.ADJECTIVE);
        List<Word> gridlist = new ArrayList<>();
        gridlist.add(gridAdj);
        ContextItem gridballItem = new ContextItem(ball, gridlist, null);
        ContextAction kickGridballAction = new ContextAction(kickitem, gridballItem);
        actionList.add(kickGridballAction);
        */

        return actionList;
    }
}
