package MapEssentials;

import Console.ConsoleInteractions;
import NLPParse.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    public String name, description;
    public boolean callbackEnabled, skipEnabled, structuredEnabled;
    public List<Word> allWords;
    public List<ContextItem> allItems;
    public Map<String, Boolean> flags;
    public List<GameMode> gameModes;
    public List<Room> rooms;
    public Integer startingRoom, staringGameMode;

    public static Game currentGame;

    public static Game importGame(String filePath){
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(filePath + ".t1"));
        } catch (Exception e){
            ConsoleInteractions.error("Failed to open file: " + e.toString());
            return null;
        }

        Game game = new Game();
        try{
            game.name = reader.readLine();
            game.description = reader.readLine();
            String[] options = reader.readLine().split(";", -1);
            game.callbackEnabled = Boolean.parseBoolean(options[0]);
            game.skipEnabled = Boolean.parseBoolean(options[1]);
            game.structuredEnabled = Boolean.parseBoolean(options[2]);
            // get all words ----------------------------------------------------------------
            Integer wordCount = Integer.parseInt(reader.readLine());
            game.allWords = new ArrayList<>();
            for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
                Word tempWord = parseWord(reader.readLine());
                tempWord.ID = wordIndex;
                game.allWords.add(tempWord);
            }

            // get all items ----------------------------------------------------------------
            Integer itemCount = Integer.parseInt(reader.readLine());
            game.allItems = new ArrayList<>();
            for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
                ContextItem tempItem = parseItem(reader.readLine(), game.allWords);
                tempItem.ID = itemIndex;
                game.allItems.add(tempItem);
            }

            // get all flags ----------------------------------------------------------------
            Integer flagCount = Integer.parseInt(reader.readLine());
            game.flags = new HashMap<>();
            for (int flagIndex = 0; flagIndex < flagCount; flagIndex++) {
                String[] flagSections = reader.readLine().split(":", -1);
                game.flags.put(flagSections[0], Boolean.parseBoolean(flagSections[1]));
            }

            // get all gamemodes ----------------------------------------------------------------
            Integer gameModeCount = Integer.parseInt(reader.readLine());
            game.gameModes = new ArrayList<>();
            for (int gameModeIndex = 0; gameModeIndex < gameModeCount; gameModeIndex++) {
                GameMode tempGameMode = parseGameMode(reader.readLine(), game.allItems);
                tempGameMode.ID = gameModeIndex;
                game.gameModes.add(tempGameMode);
            }

            // get all rooms ----------------------------------------------------------------
            Integer roomCount = Integer.parseInt(reader.readLine());
            game.rooms = new ArrayList<>();
            for (int roomIndex = 0; roomIndex < roomCount; roomIndex++) {
                Room tempRoom = readRoom(reader.readLine(), game.allItems, game.gameModes);
                tempRoom.ID = roomIndex;
                game.rooms.add(tempRoom);
            }

            game.staringGameMode = Integer.parseInt(reader.readLine());
            game.startingRoom = Integer.parseInt(reader.readLine());
        } catch (Exception e){
            ConsoleInteractions.error("File format is wrong: " + e.toString());
            e.printStackTrace();
        }

        return game;
    }

    private static Word parseWord(String string){
        String[] sections = string.split(":", -1);
        Word.Type type = Word.Type.valueOf(sections[0]);
        String word = sections[2];
        if(sections[1].length() < 1){
            return new Word(word, type);
        }

        List<String> synonyms = Arrays.asList(sections[1].split(";", -1));
        return new Word(word, synonyms, type);
    }

    private static ContextItem parseItem(String string, List<Word> allWords){
        String[] sections = string.split(":", -1);
        Word word = allWords.get(Integer.parseInt(sections[1]));
        if(sections[0].length() < 1){
            return new ContextItem(word);
        }
        List<String> adjectiveIDs = Arrays.asList(sections[0].split(";", -1));
        List<Word> adjectives = new ArrayList<>();
        for (String adjectiveID : adjectiveIDs) {
            adjectives.add(allWords.get(Integer.parseInt(adjectiveID)));
        }
        return new ContextItem(word, adjectives);
    }

    private static GameMode parseGameMode(String string, List<ContextItem> allItems){
        String[] sections = string.split(":", -1);
        String name = sections[0];
        List<ContextAction> actions = new ArrayList<>();
        if(sections[1].length() > 0){
            List<String> actionStrings = Arrays.asList(sections[1].split(";", -1));
            for (String actionString : actionStrings) {
                actions.add(parseAction(actionString, allItems));
            }
        }
        return new GameMode(name, actions);
    }

    private static Room readRoom(String string, List<ContextItem> allItems, List<GameMode> gameModes){
        String[] sections = string.split(":", -1);
        String name = sections[0], description = sections[1];
        Map<GameMode, List<ContextAction>> actions = new HashMap<>();
        if(sections[2].length() > 0){
            List<String> actionStrings = Arrays.asList(sections[2].split(";", -1));
            for (String actionString : actionStrings) {
                String[] actionStringSections = actionString.split("\\$", -1);
                GameMode key = gameModes.get(Integer.parseInt(actionStringSections[0]));
                ContextAction value = parseAction(actionStringSections[1], allItems);
                if(!actions.containsKey(key)){
                    actions.put(key, new ArrayList<>());
                }
                actions.get(key).add(value);
            }
        }
        Map<Room.Direction, Integer> layout = new HashMap<>();
        if(sections[3].length() > 0){
            List<String> layoutStrings = Arrays.asList(sections[3].split(";", -1));
            for (String layoutString : layoutStrings) {
                String[] layoutStringSections = layoutString.split("$", -1);
                Room.Direction direction = Room.Direction.valueOf(layoutStringSections[0]);
                Integer roomID = Integer.parseInt(layoutStringSections[1]);
                layout.put(direction, roomID);
            }
        }

        List<ContextItem> objects = new ArrayList<>();
        if(sections[4].length() > 0){
            List<String> objectStrings = Arrays.asList(sections[4].split(";", -1));
            for (String objectString : objectStrings) {
                objects.add(allItems.get(Integer.parseInt(objectString)));
            }
        }

        return new Room(name, description, actions, layout, objects);
    }

    private static ContextAction parseAction(String string, List<ContextItem> allItems){
        String sections[] = string.split("\\&", -1);
        ContextItem verb = allItems.get(Integer.parseInt(sections[0]));
        ContextItem object = allItems.get(Integer.parseInt(sections[1]));

        List<ContextItem> extraObjects = new ArrayList<>();
        if(sections[2].length() > 0){
            List<String> extraIDs = Arrays.asList(sections[2].split("%", -1));
            for (String extraIDstring : extraIDs) {
                extraObjects.add(allItems.get(Integer.parseInt(extraIDstring)));
            }
        }

        List<GameStatePiece> requirements = new ArrayList<>(), effects = new ArrayList<>();
        if(sections[3].length() > 0){
            List<String> reqStrings = Arrays.asList(sections[3].split("%", -1));
            for (String reqString : reqStrings) {
                requirements.add(parseStatePiece(reqString));
            }
        }
        if(sections[4].length() > 0){
            List<String> effStrings = Arrays.asList(sections[4].split("%", -1));
            for (String effString : effStrings){
                effects.add(parseStatePiece(effString));
            }
        }

        return new ContextAction(verb, object, extraObjects, requirements, effects);
    }

    private static GameStatePiece parseStatePiece(String string){
        String[] sections = string.split("@", -1);
        GameStatePiece.PieceType type = GameStatePiece.PieceType.valueOf(sections[0]);
        Boolean positive = Boolean.parseBoolean(sections[2]);
        Object target;
        switch (type) {
            case flagset:
            case flagget:
            case display:
                target = sections[1];
                break;
            default:
                target = Integer.parseInt(sections[1]);
                break;
        }
        return new GameStatePiece(type, positive, target);
    }

    public GameState generateStartState(){
        List<Integer> stateInventory = new ArrayList<>();
        Map<String, Boolean> stateFlags = new HashMap(flags);
        List<List<Integer>> objectLocations = new ArrayList<>();
        for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
            Room currentRoom = rooms.get(roomIndex);
            List<Integer> currentRoomObjects = new ArrayList<>();
            for (ContextItem object : currentRoom.objects) {
                currentRoomObjects.add(object.ID);
            }
            objectLocations.add(currentRoomObjects);
        }

        return new GameState(startingRoom, stateInventory, stateFlags, staringGameMode, objectLocations, this);
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", callbackEnabled=" + callbackEnabled +
                ", skipEnabled=" + skipEnabled +
                ", structuredEnabled=" + structuredEnabled +
                ", allWords=" + allWords +
                ", allItems=" + allItems +
                ", flags=" + flags +
                ", gameModes=" + gameModes +
                ", rooms=" + rooms +
                ", startingRoom=" + startingRoom +
                ", staringGameMode=" + staringGameMode +
                '}';
    }

    public Object writeReplace() throws ObjectStreamException {
        return null;
    }
}
