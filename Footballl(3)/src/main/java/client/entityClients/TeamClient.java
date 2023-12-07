package client.entityClients;

import models.Team;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class TeamClient {
    private DataOutputStream output;
    private DataInputStream input;
    private BufferedReader reader;
    private static Team team = new Team("1", "", "");


    public TeamClient(DataOutputStream output, DataInputStream input, BufferedReader reader) {
        this.output = output;
        this.input = input;
        this.reader = reader;
    }
    private void fillTeam(BufferedReader reader) throws IOException {
        System.out.println("Enter the name of the city");
        team.setCity(reader.readLine());
        System.out.println("Enter the name of the team");
        team.setName(reader.readLine());
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
                team.setId(reader.readLine());
                break;
            case "3":
                operation = "create";
                fillTeam(reader);
                break;
            case "4":
                operation = "update";
                System.out.println("enter id");
                team.setId(reader.readLine());
                fillTeam(reader);
                break;
            case "5":
                operation = "delete";
                System.out.println("enter id");
                team.setId(reader.readLine());
                break;
            case "6":
                operation = "exit";
                break;
        }
        return operation;
    }
    private static byte[] serialize(Team team) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(team);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
    public void run() throws IOException, ClassNotFoundException {
        while (true) {
            output.writeUTF("team");
            String operation = sendMenu(reader); // Оновлено для передачі reader як аргумент
            if (operation.equals("exit")) {
                output.writeUTF(operation);
                break;
            }
            byte[] serializedObject = serialize(team);
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

                    List<Team> receivedTeams = (List<Team>) objectInputStream.readObject(); // Десеріалізація
                    for (Team receivedTeam : receivedTeams) {
                        System.out.println(receivedTeam);
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
