package api.handler;

import api.protocol.CommandCode;
import api.protocol.GameRequest;
import api.protocol.GameResponse;
import server.engine.GameEngine;

public interface CommandHandler {
    boolean canHandle(CommandCode command);
    GameResponse process(GameRequest request, GameEngine engine);
}
