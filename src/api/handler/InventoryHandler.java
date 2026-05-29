package api.handler;

import api.protocol.CommandCode;
import api.protocol.GameRequest;
import api.protocol.GameResponse;
import server.engine.GameEngine;

public class InventoryHandler implements CommandHandler {

    @Override
    public boolean canHandle(CommandCode command) {
        return command == CommandCode.VIEW_INVENTORY;
    }

    @Override
    public GameResponse process(GameRequest request, GameEngine engine) {
        String summary = engine.getInventoryManager().getInventorySummary(engine.getInventory());
        return GameResponse.success(summary, engine.getInventory());
    }
}
