package MapEssentials;

import NLPParse.*;

import java.util.*;

public class Game {
    public String name, description;
    public boolean callbackEnabled, skipEnabled, structuredEnabled;
    public List<String> flags;
    public List<Word> allWords;
    public List<ContextItem> allItems;
    public List<Room> rooms;
    public Room startingRoom;
    public List<GameMode> gameModes;

    public static Game currentGame;

    public static Game importGame(String filePath){
        return null;
    }

    public GameState generateStartState(){
        return null;
    }
}
