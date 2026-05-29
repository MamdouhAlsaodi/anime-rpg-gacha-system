package api.handler;

import api.protocol.CommandCode;
import api.protocol.GameRequest;
import api.protocol.GameResponse;
import server.engine.GameEngine;

public class PlayerHandler implements CommandHandler {

    @Override
    public boolean canHandle(CommandCode command) {
        return command == CommandCode.VIEW_PLAYER || command == CommandCode.LEVEL_UP || command == CommandCode.ENHANCE_ITEM;
    }

    @Override
    public GameResponse process(GameRequest request, GameEngine engine) {
        return switch (request.getCommand()) {
            case VIEW_PLAYER -> GameResponse.success(engine.getPlayer().toString(), engine.getPlayer());
            case LEVEL_UP -> {
                String id = (String) request.getParam("characterId");
                int levels = request.getParam("levels") != null ? (Integer) request.getParam("levels") : 1;
                engine.levelUpCharacter(id, levels);
                yield GameResponse.success("Character leveled up!", null);
            }
            case ENHANCE_ITEM -> {
                String id = (String) request.getParam("itemId");
                int levels = request.getParam("levels") != null ? (Integer) request.getParam("levels") : 1;
                engine.enhanceItem(id, levels);
                yield GameResponse.success("Item enhanced!", null);
            }
            default -> GameResponse.error("Unknown player command");
        };
    }
}
