package GameObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WinnerKeys {
    private int keySize, winners, losers;
    private List<Team> rankedTeams;

    public WinnerKeys(int size) {
        this.keySize = size;
        this.winners = 0;
        this.losers = 0;
        this.rankedTeams = new ArrayList<>();
    }

    public void rankTeam(Team team, boolean won, String message) {
        if (!rankedTeams.contains(team)) {
            if (won) {
                // Insert winning team at the beginning of the list
                rankedTeams.add(winners, team);
            } else {
                // Add losing team to the end of the list
                rankedTeams.add(team);
            }
            updateRanks();
            team.addFinalMessage("\nTeam " + team.getName() + (won ? " won " : " lost ") + message);
        }
    }

    private void updateRanks() {
        for (int i = 0; i < rankedTeams.size(); i++) {
            rankedTeams.get(i).setRank(i + 1);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Team team : rankedTeams) {
            sb.append(team.getFinalMessage());
        }
        return sb.toString();
    }
}