package GameObjects;

public class RankList {
    Team[] array;
    int size, winners, losers;

    public RankList(int size) {
        this.array = new Team[size];
        this.size = size;
        this.winners = 0;
        this.losers = size - 1;
    }

    public void win(Team team){
        array[winners] = team;
        team.addFinalMessage("\nTeam" + team.getName() + " - Place " + winners);
        ++winners;

    }

    public void lose(Team team){
        array[losers] = team;
        team.addFinalMessage("\nTeam" + team.getName() + " - Place " + losers);
        --losers;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if(winners + losers == size){
            for(int i = 0; i < size; i++){
                res.append("\n" + array[i]);
            }
        }
        return res.toString();
    }
}
