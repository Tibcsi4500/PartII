package EngineClasses;

import Console.ConsoleInteractions;
import MapEssentials.*;
import NLPParse.*;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    public static void run(){
        String gamePath = ConsoleInteractions.prompt("Give me the game's path!");
        Game game = Game.importGame(gamePath);
        Game.currentGame = game;
        GameState gameState = game.generateStartState();
        Parser parser = createParser(game);

        while(true){ ///main loop
            List<ContextAction> context = gameState.generateContext();
            ContextAction action = parser.parse(ConsoleInteractions.prompt("What would you like to do?"), context);

            if(gameState.assertPieces(action.requirements)){
                ///Special cases for pick up and drop
                /// go ... command is generated for each room so it can be executed as any other command
                if(gameState.execute(action.effects)){
                    break;
                }
            }
        }
    }

    private static Parser createParser(Game game){
        List<Word> buzzwords = new ArrayList<>();
        for (Word word : game.allWords) {
            if(word.type.equals(Word.Type.BUZZWORD)){
                buzzwords.add(word);
            }
        }
        return new Parser(game.callbackEnabled, game.skipEnabled, game.structuredEnabled, buzzwords);
    }
}
