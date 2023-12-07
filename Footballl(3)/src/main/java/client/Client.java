package client;

import client.entityClients.PlayerClient;
import client.entityClients.TeamClient;
import models.Team;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

public class Client {
    private static Socket socket;
    private static DataOutputStream output;
    private static DataInputStream input;
    private static TeamClient teamClient;
    private static PlayerClient playerClient;
    private static Team team = new Team("1", "", "");

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    private static byte[] serialize(Team team) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(team);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    private static Object deserializeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        return obj;
    }
    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 8081);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            teamClient = new TeamClient(output, input, reader);
            playerClient = new PlayerClient(output, input, reader);
            System.out.println("Choose table to work with");
            System.out.println("1. Team");
            System.out.println("2. Player");
            String table = reader.readLine();
            if (table.equals("1")) {
                teamClient.run();
            } else {
                playerClient.run();
            }
        } catch (IOException  e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
