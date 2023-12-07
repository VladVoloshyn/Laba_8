package DAO;

import models.Player;
import models.Team;

import java.util.List;

public interface TeamDAO {
    List<Team> getAllTeams();
    Team getTeamById(String id);
    void saveTeam(Team team);
    void deleteTeam(Team book);
    void updateTeam(Team book);
    Team getTeamByName(String name);
    List<Player> getPlayersByTeamId(int teamId);
}
