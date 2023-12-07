package server;

import CRUD.TeamCRUD;
import models.Team;
import server.entityServers.PlayerServer;
import server.entityServers.TeamServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.List;

public class Server {
    private static TeamCRUD teamCRUD;

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Server started");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            try {
                DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
                while (true) {
                    System.out.println("Client connected");
                    String table = input.readUTF();
                    if (table.equals("exit")) {
                        break;
                    }
                    System.out.println("table: " + table);
                    if (table.equals("team")) {
                        TeamServer teamServer = new TeamServer(output, input);
                        teamServer.run();
                    } else if (table.equals("player")) {
                        PlayerServer playerServer = new PlayerServer(output, input);
                        playerServer.run();
                    }
                }
            } catch (Exception e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                System.out.println("Client disconnected");
                clientSocket.close();
            }
        }
    }
}
