package NLPParse;

public class FaultySentenceException extends Exception{
    FaultType faultType;

    public FaultySentenceException(FaultType faultType) {
        this.faultType = faultType;
    }

    public FaultySentenceException(String message, FaultType faultType) {
        super(message);
        this.faultType = faultType;
    }

    public enum FaultType{
        fillerword,
        nonobject,
        unexpectedobjectend
    }
}
