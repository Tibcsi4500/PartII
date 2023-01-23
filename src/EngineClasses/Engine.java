package EngineClasses;

import Console.ConsoleInteractions;
import MapEssentials.*;
import NLPParse.*;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    public static void run(){
        ConsoleInteractions.initalize();

        String gamePath = ConsoleInteractions.prompt("Give me the game's path!");
        Game game = Game.importGame(gamePath);
        Game.currentGame = game;
        GameState gameState = game.generateStartState();
        Parser parser = createParser(game);

        while(true){ ///main loop
            List<ContextAction> context = gameState.generateContext();
            String command = ConsoleInteractions.prompt("What would you like to do?");

            if(command.equals("exit")){
                ConsoleInteractions.display("Goodbye!");
                break;
            }

            if(command.equals("inventory")){
                String inventoryContent = "Your inventory contains the following:\n";
                for (Integer objectID : gameState.inventory) {
                    inventoryContent += game.allItems.get(objectID).toString() + " ";
                }

                ConsoleInteractions.display(inventoryContent);
            }

            ContextAction action = parser.parse(command, context);

            if(action == null){
                ConsoleInteractions.display("I didn't get that.");
                continue;
            }

            if(action.verb.word.string.equals("pick") && action.object.word.string.equals("up")){
                if (!action.extraObjects.isEmpty()){
                    for (ContextItem object : action.extraObjects) {
                        if(object.ID.equals(-1)){
                            ConsoleInteractions.display("Don't recognise object " + object);
                            continue;
                        }
                        if(
                                gameState.flags.get(object.ID.toString() + "pickup") &&
                                        gameState.objectLocations.get(gameState.position).contains(object.ID)
                        ){
                            gameState.objectLocations.get(gameState.position).remove(object.ID);
                            gameState.inventory.add(object.ID);
                            ConsoleInteractions.display("Picked up " + object);
                        }
                    }
                } else {
                    ConsoleInteractions.display("No such objects can be picked up here.");
                }

                continue;
            }

            if(action.verb.word.string.equals("put") && action.object.word.string.equals("down")){
                if(!action.extraObjects.isEmpty()){
                    for (ContextItem object : action.extraObjects) {
                        if(object.ID.equals(-1)){
                            ConsoleInteractions.display("Don't recognise object " + object);
                            continue;
                        }
                        if(gameState.inventory.contains(object.ID)){
                            gameState.objectLocations.get(gameState.position).add(object.ID);
                            gameState.inventory.remove(object.ID);
                            ConsoleInteractions.display("Put " + object + " down");
                        }
                    }
                } else {
                    ConsoleInteractions.display("No such objects in your inventory.");
                }
                continue;
            }

            if(action.verb.word.string.equals("save") && action.object.word.string.equals("game")){
                try{
                    GameState.Save(gameState);
                    ConsoleInteractions.display("Saved game!");
                } catch (Exception e){
                    ConsoleInteractions.error("Fíailed to load: " + e.toString());
                }
                continue;
            }

            if(action.verb.word.string.equals("load") && action.object.word.string.equals("game")){
                try{
                    gameState = GameState.Load(game);
                    ConsoleInteractions.display("Loaded game!");
                } catch (Exception e){
                    ConsoleInteractions.error("Fíailed to load: " + e.toString());
                }
                continue;
            }

            if(gameState.assertPieces(action.requirements)){
                if(gameState.execute(action.effects)){
                    break;
                }
            } else {
                ConsoleInteractions.display("Couldn't perform action.");
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
