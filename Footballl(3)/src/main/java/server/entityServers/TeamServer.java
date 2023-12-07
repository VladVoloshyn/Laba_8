package server.entityServers;

import CRUD.TeamCRUD;
import models.Team;

import javax.swing.text.TabExpander;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class TeamServer {
    private TeamCRUD teamCRUD;
    private DataOutputStream output;
    private DataInputStream input;

    public TeamServer(DataOutputStream output, DataInputStream input) throws SQLException {
        this.output = output;
        this.input = input;
        this.teamCRUD = new TeamCRUD();
    }

    private void makeDBRequest(Team team, String request) throws SQLException, IOException {
        teamCRUD = new TeamCRUD();
        switch (request) {
            case "all":
                List<Team> teams = teamCRUD.getAllTeams();
                System.out.println(teams);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(teams); // Серіалізація цілого списку
                objectOutputStream.close();
                byte[] serializedTeams = byteArrayOutputStream.toByteArray();
                output.writeInt(serializedTeams.length); // Спочатку відправляємо довжину масиву
                output.write(serializedTeams); // Потім відправляємо серіалізований список
                output.flush();
                break;
            case "id":
                System.out.println("by id");
                System.out.println(team.getId());
                Team idTeam = teamCRUD.getTeamById(team.getId());
                System.out.println(idTeam);
                output.writeUTF("id " + idTeam.getId() + " name " + idTeam.getName() + " city " + idTeam.getCity());
                break;
            case "create":
                System.out.println("adding");
                teamCRUD.saveTeam(team);
                System.out.println("added");
                output.writeUTF("added");
                System.out.println(teamCRUD.getAllTeams());
                break;
            case "update":
                System.out.println("updating");
                teamCRUD.updateTeam(team);
                System.out.println(team);
                output.writeUTF("updated");
                break;
            case "delete":
                teamCRUD.deleteTeam(team);
                output.writeUTF("deleted");
                break;
            case "get":
                Team newTeam = teamCRUD.getTeamById(team.getId());
                output.writeUTF("id " + newTeam.getId() + " name " + newTeam.getName() + " city " + newTeam.getCity());
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
            Team team = (Team) deserializeObject(objectBytes);
            System.out.println("Received object: " + team);
            makeDBRequest(team, requestType);
        }
    }
}
