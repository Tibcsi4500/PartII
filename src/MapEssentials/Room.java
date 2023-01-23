package MapEssentials;

import NLPParse.*;
import java.util.*;

public class Room {
    public String name, description;
    public Integer ID;
    public Map<GameMode, List<ContextAction>> actions;
    public Map<Direction, Integer> layout;
    public List<ContextItem> objects;

    public Room(String name, String description,
                Map<GameMode, List<ContextAction>> actions,
                Map<Direction, Integer> layout, List<ContextItem> objects) {
        this.name = name;
        this.description = description;
        this.actions = actions;
        this.layout = layout;
        this.objects = objects;
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", actions=" + actions +
                ", layout=" + layout +
                ", objects=" + objects +
                '}';
    }

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
