package MapEssentials;

import Console.ConsoleInteractions;
import NLPParse.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Game {
    public String name, description;
    public boolean callbackEnabled, skipEnabled, structuredEnabled;
    public Map<String, Boolean> flags;
    public List<Word> allWords;
    public List<ContextItem> allItems;
    public List<Room> rooms;
    public List<GameMode> gameModes;
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
            String[] options = reader.readLine().split(";");
            game.callbackEnabled = Boolean.parseBoolean(options[0]);
            game.skipEnabled = Boolean.parseBoolean(options[1]);
            game.structuredEnabled = Boolean.parseBoolean(options[2]);
            // get all words ----------------------------------------------------------------
            Integer wordCount = Integer.parseInt(reader.readLine());
            game.allWords = new ArrayList<>();
            for (int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
                Word tempWord = readWord(reader);
                tempWord.ID = wordIndex;
                game.allWords.add(tempWord);
            }

            // get all items ----------------------------------------------------------------
            Integer itemCount = Integer.parseInt(reader.readLine());
            game.allItems = new ArrayList<>();
            for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
                ContextItem tempItem = readItem(reader, game.allWords);
                tempItem.ID = itemIndex;
                game.allItems.add(tempItem);
            }

            // get all flags ----------------------------------------------------------------
            Integer flagCount = Integer.parseInt(reader.readLine());
            game.flags = new HashMap<>();
            for (int flagIndex = 0; flagIndex < flagCount; flagIndex++) {
                String[] flagSections = reader.readLine().split(":");
                game.flags.put(flagSections[0], Boolean.parseBoolean(flagSections[1]));
            }

            // get all gamemodes ----------------------------------------------------------------
            Integer gameModeCount = Integer.parseInt(reader.readLine());
            game.gameModes = new ArrayList<>();
            for (int gameModeIndex = 0; gameModeIndex < gameModeCount; gameModeIndex++) {
                GameMode tempGameMode = readGameMode(reader, game.allItems);
                tempGameMode.ID = gameModeIndex;
                game.gameModes.add(tempGameMode);
            }


        } catch (Exception e){
            ConsoleInteractions.error("File format is wrong: " + e.toString());
        }

        return game;
    }

    private static Word readWord(BufferedReader reader) throws Exception {
        String[] sections = reader.readLine().split(":");
        Word.Type type = Word.Type.valueOf(sections[0]);
        String word = sections[2];
        if(sections[1].length() < 1){
            return new Word(word, type);
        }

        List<String> synonyms = Arrays.asList(sections[1].split(";"));
        return new Word(word, synonyms, type);
    }

    private static ContextItem readItem(BufferedReader reader, List<Word> allWords) throws  Exception {
        String[] sections = reader.readLine().split(":");
        Word word = allWords.get(Integer.parseInt(sections[1]));
        if(sections[0].length() < 1){
            return new ContextItem(word);
        }
        List<String> adjectiveIDs = Arrays.asList(sections[0].split(";"));
        List<Word> adjectives = new ArrayList<>();
        for (String adjectiveID : adjectiveIDs) {
            adjectives.add(allWords.get(Integer.parseInt(adjectiveID)));
        }
        return new ContextItem(word, adjectives);
    }

    private static GameMode readGameMode(BufferedReader reader, List<ContextItem> allItems) throws Exception{
        String[] sections = reader.readLine().split(":");
        String name = sections[0];
        List<ContextAction> actions = new ArrayList<>();
        if(sections[1].length() > 0){
            List<String> actionStrings = Arrays.asList(sections[1].split(";"));
            for (String actionString : actionStrings) {
                actions.add(parseAction(actionString, allItems));
            }
        }
        return new GameMode(name, actions);
    }

    private static ContextAction parseAction(String string, List<ContextItem> allItems){
        String sections[] = string.split("&");
        ContextItem verb = allItems.get(Integer.parseInt(sections[0]));
        ContextItem object = allItems.get(Integer.parseInt(sections[1]));

        List<ContextItem> extraObjects = new ArrayList<>();
        if(sections[2].length() > 0){
            List<String> extraIDs = Arrays.asList(sections[2].split("%"));
            for (String extraIDstring : extraIDs) {
                extraObjects.add(allItems.get(Integer.parseInt(extraIDstring)));
            }
        }

        List<GameStatePiece> requirements = new ArrayList<>(), effects = new ArrayList<>();
        if(sections[3].length() > 0){
            List<String> reqStrings = Arrays.asList(sections[3].split("%"));
            for (String reqString : reqStrings) {
                requirements.add(parseStatePiece(reqString));
            }
        }
        if(sections[4].length() > 0){
            List<String> effStrings = Arrays.asList(sections[4].split("%"));
            for (String effString : effStrings){
                requirements.add(parseStatePiece(effString));
            }
        }

        return new ContextAction(verb, object, extraObjects, requirements, effects);
    }

    private static GameStatePiece parseStatePiece(String string){
        String[] sections = string.split("@");
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
        return null;
    }
}
