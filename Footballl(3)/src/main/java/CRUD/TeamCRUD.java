package CRUD;

import DAO.TeamDAO;
import database.DatabaseConnection;
import models.Player;
import models.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamCRUD implements TeamDAO {
    private Connection connection;

    public TeamCRUD() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM Teams";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                 teams.add(new Team(
                        String.valueOf(resultSet.getInt("id")),
                        resultSet.getString("name"),
                        resultSet.getString("city")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(teams);
        return teams;
    }

    @Override
    public Team getTeamById(String id) {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM Teams WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Integer.valueOf(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Team(
                        String.valueOf(resultSet.getInt("id")),
                        resultSet.getString("name"),
                        resultSet.getString("city")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveTeam(Team team) {
        String sql = "INSERT INTO Teams (name, city) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, team.getName());
            statement.setString(2, team.getCity());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTeam(Team team) {
        String sql = "DELETE FROM Teams WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Integer.valueOf(team.getId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTeam(Team team) {
        String sql = "UPDATE Teams SET name = ?, city = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, team.getName());
            statement.setString(2, team.getCity());
            statement.setInt(3, Integer.valueOf(team.getId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Team getTeamByName(String name) {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM Teams WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Team(
                        String.valueOf(resultSet.getInt("id")),
                        resultSet.getString("name"),
                        resultSet.getString("city")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Player> getPlayersByTeamId(int teamId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM Players WHERE team_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teamId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                players.add(new Player(
                        resultSet.getString("id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getInt("age"),
                        resultSet.getInt("team_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }
}
