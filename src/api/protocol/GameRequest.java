package api.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private CommandCode command;
    private Map<String, Object> params;
    private String requestId;

    public GameRequest(CommandCode command) {
        this.command = command;
        this.params = new HashMap<>();
        this.requestId = UUID.randomUUID().toString();
    }

    public void addParam(String key, Object value) {
        params.put(key, value);
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public CommandCode getCommand() { return command; }
    public Map<String, Object> getParams() { return params; }
    public String getRequestId() { return requestId; }
}
