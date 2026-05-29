package api.handler;

import api.protocol.CommandCode;
import api.protocol.GameRequest;
import api.protocol.GameResponse;
import server.engine.GameEngine;

import java.util.List;

public class SummonHandler implements CommandHandler {

    @Override
    public boolean canHandle(CommandCode command) {
        return command == CommandCode.SUMMON_SINGLE || command == CommandCode.SUMMON_TEN;
    }

    @Override
    public GameResponse process(GameRequest request, GameEngine engine) {
        try {
            int count = request.getCommand() == CommandCode.SUMMON_SINGLE ? 1 : 10;
            List<Object> results = engine.performSummon(count);
            StringBuilder msg = new StringBuilder();
            msg.append(String.format("Summoned %d item(s)!%n", results.size()));
            for (int i = 0; i < results.size(); i++) {
                msg.append(String.format("  %d. %s%n", i + 1, results.get(i)));
            }
            return GameResponse.success(msg.toString(), results);
        } catch (Exception e) {
            return GameResponse.error(e.getMessage());
        }
    }
}
