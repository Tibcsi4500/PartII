package MapEssentials;

import Console.ConsoleInteractions;
import NLPParse.ContextAction;
import NLPParse.ContextItem;
import NLPParse.Word;

import java.io.*;
import java.util.*;

public class GameState implements Serializable {
    public Integer position;
    public List<Integer> inventory;
    public Map<String, Boolean> flags;
    public Integer gameMode;
    public List<List<Integer>> objectLocations;

    public Game game;

    public List<ContextAction> generateContext() {
        List<ContextAction> context = new ArrayList<>();
        context.addAll(game.rooms.get(position).actions.get(game.gameModes.get(gameMode)));
        context.addAll(game.gameModes.get(gameMode).actions);
        context.addAll(generateExceptionalContext());
        return context;
    }

    private List<ContextAction> generateExceptionalContext(){
        ContextItem game = new ContextItem(new Word("game", Word.Type.OBJECT));

        ContextAction save = new ContextAction(
                new ContextItem(new Word("save", Word.Type.VERB)),
                game
        );

        ContextAction load = new ContextAction(
                new ContextItem(new Word("load", Word.Type.VERB)),
                game
        );

        return Arrays.asList(save, load);
    }

    public boolean execute(List<GameStatePiece> effects){
        boolean endGame = false;
        for (GameStatePiece effect : effects) {
            switch (effect.type) {
                case invget -> {
                    Integer ID = (Integer) effect.target;
                    inventory.add(ID);
                    break;
                }
                case invlose -> {
                    Integer ID = (Integer) effect.target;
                    inventory.remove(ID);
                    break;
                }
                case positionset -> {
                    Integer ID = (Integer) effect.target;
                    position = ID;
                    break;
                }
                case flagset -> {
                    String flagName = (String) effect.target;
                    flags.put(flagName, effect.positive);
                    break;
                }
                case modeset -> {
                    Integer ID = (Integer) effect.target;
                    gameMode = ID;
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
                    if(inventory.contains(ID) != requirement.positive){
                        return false;
                    }
                    break;
                }
                case positionget -> {
                    Integer ID = (Integer)requirement.target;
                    if(position.equals(ID) != requirement.positive){
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
                    if(gameMode.equals(ID) != requirement.positive){
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

    public GameState(Integer position, List<Integer> inventory,
                     Map<String, Boolean> flags, Integer gameMode,
                     List<List<Integer>> objectLocations, Game game) {
        this.position = position;
        this.inventory = inventory;
        this.flags = flags;
        this.gameMode = gameMode;
        this.objectLocations = objectLocations;
        this.game = game;
    }

    public static void Save(GameState state) throws Exception {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(state.game.name + ".save"));
        stream.writeObject(state);
        stream.close();
    }

    public static GameState Load(Game game) throws Exception {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(game.name + ".save"));
        GameState result = (GameState) stream.readObject();
        result.game = game;
        stream.close();
        return result;
    }
}
