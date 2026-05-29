package api.router;

import api.handler.CommandHandler;
import api.handler.InventoryHandler;
import api.handler.PlayerHandler;
import api.handler.SummonHandler;
import api.protocol.CommandCode;
import api.protocol.GameRequest;
import api.protocol.GameResponse;
import server.engine.GameEngine;

import java.util.ArrayList;
import java.util.List;

public class CommandRouter {
    private List<CommandHandler> handlers;

    public CommandRouter() {
        this.handlers = new ArrayList<>();
        handlers.add(new SummonHandler());
        handlers.add(new InventoryHandler());
        handlers.add(new PlayerHandler());
    }

    public GameResponse route(GameRequest request, GameEngine engine) {
        for (CommandHandler handler : handlers) {
            if (handler.canHandle(request.getCommand())) {
                GameResponse response = handler.process(request, engine);
                response.setRequestId(request.getRequestId());
                return response;
            }
        }
        if (request.getCommand() == CommandCode.HELP) {
            return GameResponse.success(
                "Available commands: SUMMON_SINGLE, SUMMON_TEN, VIEW_INVENTORY, VIEW_PLAYER, LEVEL_UP, ENHANCE_ITEM, HELP, EXIT",
                null);
        }
        return GameResponse.error("Unknown command: " + request.getCommand());
    }
}
