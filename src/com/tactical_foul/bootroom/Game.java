
package com.tactical_foul.bootroom;

public class Game {
    
    public static final int NONE = 0;

    public long id;
    public long HomeTeam_id;
    public long AwayTeam_id;
    public int HomeTeamFinalScore;
    public int AwayTeamFinalScore;

    public Game(long id, long homeTeam_id, long awayTeam_id, int homeTeamFinalScore,
            int awayTeamFinalScore) {
        this.id = id;
        HomeTeam_id = homeTeam_id;
        AwayTeam_id = awayTeam_id;
        HomeTeamFinalScore = homeTeamFinalScore;
        AwayTeamFinalScore = awayTeamFinalScore;
    }

}
