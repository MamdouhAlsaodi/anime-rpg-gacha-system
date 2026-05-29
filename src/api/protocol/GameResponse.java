package api.protocol;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GameResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Object data;
    private String requestId;
    private LocalDateTime timestamp;

    private GameResponse(boolean success, String message, Object data, String requestId) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now();
    }

    public static GameResponse success(String message, Object data) {
        return new GameResponse(true, message, data, null);
    }

    public static GameResponse error(String message) {
        return new GameResponse(false, message, null, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public String getRequestId() { return requestId; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setRequestId(String requestId) { this.requestId = requestId; }

    @Override
    public String toString() {
        return "[" + (success ? "OK" : "ERROR") + "] " + message;
    }
}
