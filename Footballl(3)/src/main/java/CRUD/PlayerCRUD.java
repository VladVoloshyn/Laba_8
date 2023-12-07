package CRUD;
import DAO.PlayerDAO;
import database.DatabaseConnection;
import models.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerCRUD implements PlayerDAO {
    private Connection connection;

    public PlayerCRUD() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    // Додавання нового гравця
    @Override
    public void savePlayer(Player player) {
        String sql = "INSERT INTO Players (firstName, lastName, age, team_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getFirstName());
            statement.setString(2, player.getLastName());
            statement.setInt(3, player.getAge());
            statement.setInt(4, player.getTeamId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Player> getAllPlayers() {
        String sql = "SELECT * FROM Players";
        List<Player> players = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

    @Override
    public Player getPlayerById(String id) {
        String sql = "SELECT * FROM Players WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Integer.valueOf(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Player(
                        resultSet.getString("id"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getInt("age"),
                        resultSet.getInt("team_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deletePlayer(String id) {
        String sql = "DELETE FROM Players WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Integer.parseInt(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    // Оновлення даних гравця
    public void updatePlayer(Player player) {
        String sql = "UPDATE Players SET firstName = ?, lastName = ?, age = ?, team_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getFirstName());
            statement.setString(2, player.getLastName());
            statement.setInt(3, player.getAge());
            statement.setInt(4, player.getTeamId());
            statement.setInt(5, Integer.parseInt(player.getId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}