package MapEssentials;

import NLPParse.*;
import java.util.*;

public class GameMode {
    public String name;
    public Integer ID;
    public List<ContextAction> actions;

    public GameMode(String name, List<ContextAction> actions) {
        this.name = name;
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "GameMode{" +
                "name='" + name + '\'' +
                ", actions=" + actions +
                '}';
    }
}
