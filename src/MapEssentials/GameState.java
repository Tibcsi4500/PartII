package MapEssentials;

import Console.ConsoleInteractions;
import NLPParse.ContextAction;
import NLPParse.ContextItem;

import java.util.*;

public class GameState {
    public Room position;
    public List<ContextItem> inventory;
    public Map<String, Boolean> flags;
    public GameMode gameMode;

    public List<ContextAction> generateContext() {
        List<ContextAction> context = new ArrayList<>();
        context.addAll(position.actions.get(gameMode));
        context.addAll(gameMode.actions);
        return context;
    }

    public boolean execute(List<GameStatePiece> effects){
        boolean endGame = false;
        for (GameStatePiece effect : effects) {
            switch (effect.type) {
                case invget -> {
                    Integer ID = (Integer) effect.target;
                    inventory.add(Game.currentGame.allItems.get(ID));
                    break;
                }
                case invlose -> {
                    Integer ID = (Integer) effect.target;
                    inventory.remove(Game.currentGame.allItems.get(ID));
                    break;
                }
                case positionset -> {
                    Integer ID = (Integer) effect.target;
                    position = Game.currentGame.rooms.get(ID);
                    break;
                }
                case flagset -> {
                    String flagName = (String) effect.target;
                    flags.put(flagName, effect.positive);
                    break;
                }
                case modeset -> {
                    Integer ID = (Integer) effect.target;
                    gameMode = Game.currentGame.gameModes.get(ID);
                    break;
                }
                case display -> {
                    String text = (String) effect.target;
                    ConsoleInteractions.display(text);
                    break;
                }
                case gameend -> {
                    endGame = true;
                }
                default -> {
                    ConsoleInteractions.error("Wrong GameStatePiece.type at current action effect!");
                    break;
                }
            }
        }
        return endGame;
    }

    public boolean assertPieces(List<GameStatePiece> requirements){
        for (GameStatePiece requirement : requirements) {
            switch (requirement.type) {
                case invhas -> {
                    Integer ID = (Integer)requirement.target;
                    if(inventory.contains(Game.currentGame.allItems.get(ID)) != requirement.positive){
                        return false;
                    }
                    break;
                }
                case positionget -> {
                    Integer ID = (Integer)requirement.target;
                    if(position.ID.equals(ID) != requirement.positive){
                        return false;
                    }
                    break;
                }
                case flagget -> {
                    String flagName = (String)requirement.target;
                    if(flags.get(flagName) != requirement.positive){
                        return false;
                    }
                    break;
                }
                case modeget -> {
                    Integer ID = (Integer)requirement.target;
                    if(gameMode.ID.equals(ID) != requirement.positive){
                        return false;
                    }
                }
                default -> {
                    ConsoleInteractions.error("Wrong GameStatePiece.type at current action requirement!");
                    return false;
                }
            }
        }
        return true;
    }
}
