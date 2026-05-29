package client.network;

import api.protocol.CommandCode;
import api.protocol.GameRequest;
import api.protocol.GameResponse;
import api.router.CommandRouter;
import server.engine.GameEngine;

import java.io.*;
import java.net.Socket;

public class ServerConnector {
    private String host;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;
    private boolean offlineMode;
    private GameEngine offlineEngine;
    private CommandRouter offlineRouter;

    public ServerConnector() {
        this.connected = false;
        this.offlineMode = false;
    }

    public ServerConnector(String username) {
        this.connected = false;
        this.offlineMode = false;
        enableOfflineMode(username);
    }

    public void connect(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            offlineMode = false;
            System.out.println("Connected to server at " + host + ":" + port);
        } catch (IOException e) {
            System.out.println("Could not connect to server. Switching to offline mode.");
            enableOfflineMode();
        }
    }

    public void enableOfflineMode() {
        enableOfflineMode("Player");
    }

    public void enableOfflineMode(String username) {
        this.offlineMode = true;
        int startingGems = username.equalsIgnoreCase("mamdouh") ? 999999 : 10000;
        this.offlineEngine = new GameEngine(username, startingGems);
        this.offlineRouter = new CommandRouter();
        this.connected = true;
        System.out.println("Offline mode enabled. Using local GameEngine for " + username + ".");
    }

    public GameResponse sendRequest(GameRequest request) {
        if (offlineMode) {
            return offlineRouter.route(request, offlineEngine);
        }
        try {
            out.writeObject(request);
            out.flush();
            return (GameResponse) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return GameResponse.error("Connection error: " + e.getMessage());
        }
    }

    public GameResponse summonSingle() {
        return sendRequest(new GameRequest(CommandCode.SUMMON_SINGLE));
    }

    public GameResponse summonTen() {
        return sendRequest(new GameRequest(CommandCode.SUMMON_TEN));
    }

    public GameResponse viewInventory() {
        return sendRequest(new GameRequest(CommandCode.VIEW_INVENTORY));
    }

    public GameResponse viewPlayer() {
        return sendRequest(new GameRequest(CommandCode.VIEW_PLAYER));
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            connected = false;
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public boolean isConnected() { return connected; }
    public boolean isOfflineMode() { return offlineMode; }
    public GameEngine getOfflineEngine() { return offlineEngine; }
}