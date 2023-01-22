package MapEssentials;

import NLPParse.*;
import java.util.*;

public class Room {
    public String name, description;
    public Integer ID;
    public ContextItem roomItem;
    public Map<GameMode, List<ContextAction>> actions;
    public Map<Direction, Integer> layout;
    public List<ContextItem> objects;

    public enum Direction{
        up,
        down,
        left,
        right,
        north,
        south,
        east,
        west,
        forward,
        back,
        around
    }
}
