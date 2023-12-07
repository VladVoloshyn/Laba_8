package client.entityClients;

import CRUD.TeamCRUD;
import models.Player;
import models.Team;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class PlayerClient {
    private DataOutputStream output;
    private DataInputStream input;
    private BufferedReader reader;
    private TeamCRUD teamCRUD;
    private static Player player = new Player("1", "", "", 1, 1);


    public PlayerClient(DataOutputStream output, DataInputStream input, BufferedReader reader) throws SQLException {
        this.output = output;
        this.input = input;
        this.reader = reader;
        this.teamCRUD = new TeamCRUD();
    }
    private void fillTeam(BufferedReader reader) throws IOException {
        System.out.println("Enter the first name");
        player.setFirstName(reader.readLine());
        System.out.println("Enter the last name");
        player.setLastName(reader.readLine());
        System.out.println("Enter age");
        player.setAge(Integer.valueOf(reader.readLine()));
        System.out.println("enter the name of team");
        String name = reader.readLine();
        Team foundedTeam = teamCRUD.getTeamByName(name);
        System.out.println(foundedTeam);
        player.setTeamId(Integer.parseInt(foundedTeam.getId()));
    }
    private String sendMenu(BufferedReader reader) throws IOException {
        String operation = new String();
        System.out.println("choose operation:");
        System.out.println("1. get all objects");
        System.out.println("2. get object by id");
        System.out.println("3. create new object");
        System.out.println("4. update object");
        System.out.println("5. delete object");
        System.out.println("6. exit");
        switch (reader.readLine()) {
            case "1":
                operation = "all";
                break;
            case "2":
                operation = "id";
                System.out.println("enter id");
                player.setId(reader.readLine());
                break;
            case "3":
                operation = "create";
                fillTeam(reader);
                break;
            case "4":
                operation = "update";
                System.out.println("enter id");
                player.setId(reader.readLine());
                fillTeam(reader);
                break;
            case "5":
                operation = "delete";
                System.out.println("enter id");
                player.setId(reader.readLine());
                break;
            case "6":
                operation = "exit";
                break;
        }
        return operation;
    }
    private static byte[] serialize(Player serializedPlayer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(serializedPlayer);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    public void run() throws IOException, ClassNotFoundException {
        while (true) {
            output.writeUTF("player");
            String operation = sendMenu(reader); // Оновлено для передачі reader як аргумент
            if (operation.equals("exit")) {
                output.writeUTF(operation);
                break;
            }
            byte[] serializedObject = serialize(player);
            output.writeUTF(operation);
            output.writeInt(serializedObject.length);
            output.write(serializedObject);
            output.flush();
            if(operation.equals("all")) {
                int length = input.readInt(); // Читаємо довжину масиву
                if (length > 0) {
                    byte[] receivedBytes = new byte[length];
                    input.readFully(receivedBytes);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivedBytes);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    List<Player> receivedPlayers = (List<Player>) objectInputStream.readObject(); // Десеріалізація
                    for (Player receivedPlayer : receivedPlayers) {
                        System.out.println(receivedPlayer);
                    }
                    objectInputStream.close();

                    // Тепер ви маєте список об'єктів
                }
            } else {
                String response = input.readUTF();
                System.out.println("Response from server: " + response);
            }
        }
    }
}
