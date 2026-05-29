package server.exception;

public class InsufficientGemsException extends GachaBaseException {
    private int required;
    private int available;

    public InsufficientGemsException(int required, int available) {
        super("Not enough gems! Required: " + required + ", Available: " + available, 1001);
        this.required = required;
        this.available = available;
    }

    @Override
    public String getErrorCategory() { return "ECONOMY"; }

    public int getRequired() { return required; }
    public int getAvailable() { return available; }
}
