package server.exception;

import java.time.LocalDateTime;

public abstract class GachaBaseException extends Exception {
    private int errorCode;
    private LocalDateTime errorTime;

    public GachaBaseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.errorTime = LocalDateTime.now();
    }

    public abstract String getErrorCategory();

    public int getErrorCode() { return errorCode; }
    public LocalDateTime getErrorTime() { return errorTime; }

    @Override
    public String toString() {
        return "[" + getErrorCategory() + ":" + errorCode + "] " + getMessage() + " (" + errorTime + ")";
    }
}
