package server;

import api.protocol.GameRequest;
import api.protocol.GameResponse;
import api.router.CommandRouter;
import server.engine.GameEngine;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GachaGameServer {
    private static final int PORT = 8080;
    private GameEngine engine;
    private CommandRouter router;
    private boolean running;

    public GachaGameServer() {
        this.engine = new GameEngine();
        this.router = new CommandRouter();
        this.running = true;
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
        running = false;
    }

    public static void main(String[] args) {
        new GachaGameServer().start();
    }
}
