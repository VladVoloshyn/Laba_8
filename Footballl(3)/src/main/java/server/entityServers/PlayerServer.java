package server.entityServers;

import CRUD.PlayerCRUD;
import models.Player;
import models.Team;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class PlayerServer {
    private PlayerCRUD playerCRUD;
    private DataOutputStream output;
    private DataInputStream input;

    public PlayerServer(DataOutputStream output, DataInputStream input) throws SQLException {
        this.output = output;
        this.input = input;
        playerCRUD = new PlayerCRUD();
    }

    private void makeDBRequest(Player player, String request) throws SQLException, IOException {
        switch (request) {
            case "all":
                List<Player> players = playerCRUD.getAllPlayers();
                System.out.println(players);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(players); // Серіалізація цілого списку
                objectOutputStream.close();
                byte[] serializedTeams = byteArrayOutputStream.toByteArray();
                output.writeInt(serializedTeams.length); // Спочатку відправляємо довжину масиву
                output.write(serializedTeams); // Потім відправляємо серіалізований список
                output.flush();
                break;
            case "id":
                System.out.println("by id");
                System.out.println(player.getId());
                Player idPlayer = playerCRUD.getPlayerById(player.getId());
                System.out.println(idPlayer);
                output.writeUTF(idPlayer.toString());
                break;
            case "create":
                System.out.println("adding");
                playerCRUD.savePlayer(player);
                System.out.println("added");
                output.writeUTF("added");
                break;
            case "update":
                System.out.println("updating");
                playerCRUD.updatePlayer(player);
                System.out.println(player);
                output.writeUTF("updated");
                break;
            case "delete":
                playerCRUD.deletePlayer(player.getId());
                output.writeUTF("deleted");
                break;
            case "get":
                Player newPlayer = playerCRUD.getPlayerById(player.getId());
                output.writeUTF(newPlayer.toString());
                break;
        }
    }
    public static Object deserializeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        return obj;
    }
    public void run() throws IOException, ClassNotFoundException, SQLException {
        String requestType = input.readUTF();
        if (requestType.equals("exit")) { // Команда для виходу
            return;
        }
        int len = input.readInt();
        if (len > 0) {
            byte[] objectBytes = new byte[len];
            input.readFully(objectBytes);
            Player player = (Player)deserializeObject(objectBytes);
            System.out.println("Received object: " + player);
            makeDBRequest(player, requestType);
        }
    }
}
