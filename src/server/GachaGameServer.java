package server;

import api.protocol.GameRequest;
import api.protocol.GameResponse;
import api.router.CommandRouter;
import server.engine.GameEngine;
import server.model.player.Player;
import server.persistence.LocalGameDatabase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GachaGameServer {
    private static final int PORT = 8080;
    private GameEngine engine;
    private CommandRouter router;
    private LocalGameDatabase database;
    private boolean running;

    public GachaGameServer() {
        this.engine = new GameEngine();
        this.router = new CommandRouter();
        this.database = new LocalGameDatabase();
        loadDefaultPlayer();
        this.running = true;
    }

    private void loadDefaultPlayer() {
        try {
            database.initialize();
            Player savedPlayer = database.loadPlayer(engine.getPlayer().getName()).orElse(null);
            if (savedPlayer != null) {
                engine.setPlayer(savedPlayer);
                System.out.println("Loaded local player data: " + savedPlayer.getName());
            }
        } catch (Exception e) {
            System.err.println("Could not load local player data: " + e.getMessage());
        }
    }

    private void saveDefaultPlayer() {
        try {
            database.savePlayer(engine.getPlayer());
        } catch (IOException e) {
            System.err.println("Could not save local player data: " + e.getMessage());
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Gacha Game Server listening on port " + PORT + "...");
            System.out.println("Engine initialized. Player: " + engine.getPlayer());

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            out.flush();
            while (running && !clientSocket.isClosed()) {
                try {
                    Object obj = in.readObject();
                    if (obj instanceof GameRequest request) {
                        GameResponse response = router.route(request, engine);
                        saveDefaultPlayer();
                        out.writeObject(response);
                        out.flush();
                        System.out.println("Handled: " + request.getCommand() + " -> " + response);
                    }
                } catch (java.net.SocketException e) {
                    break;
                } catch (ClassNotFoundException e) {
                    out.writeObject(GameResponse.error("Invalid request format"));
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    public void stop() {
        saveDefaultPlayer();
        running = false;
    }

    public static void main(String[] args) {
        new GachaGameServer().start();
    }
}
