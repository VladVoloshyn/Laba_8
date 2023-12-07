package DAO;

import models.Player;

import java.util.List;

public interface PlayerDAO {
    List<Player> getAllPlayers();
    Player getPlayerById(String id);
    void savePlayer(Player player);
    void deletePlayer(String id);
    void updatePlayer(Player player);
}
