package MapEssentials;

public class GameStatePiece {
    public PieceType type;
    public boolean positive;
    public Object target;

    public GameStatePiece(PieceType type, boolean positive, Object target) {
        this.type = type;
        this.positive = positive;
        this.target = target;
    }

    @Override
    public String toString() {
        return "GameStatePiece{" +
                "type=" + type +
                ", positive=" + positive +
                ", target=" + target +
                '}';
    }

    public enum PieceType{
        invget,
        invlose,
        invhas,
        positionset,
        positionget,
        flagset,
        flagget,
        modeset,
        modeget,
        display,
        gameend
    }
}
