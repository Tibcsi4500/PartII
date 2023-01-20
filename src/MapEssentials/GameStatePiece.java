package MapEssentials;

public class GameStatePiece {
    public PieceType type;
    public boolean positive;
    public Object target;

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
