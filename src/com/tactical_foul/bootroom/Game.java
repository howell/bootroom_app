
package com.tactical_foul.bootroom;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

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
    
    public void export() {
        ExportTask et = new ExportTask();
        et.execute(this);
    }
    
    protected class ExportTask extends AsyncTask<Game, Void, Void> {
        private final static String EXPORT_URL = "http://beams.herokuapp.com/games/";
        private final static String HOME_TEAM_ID_KEY = "game[home_team_id]";
        private final static String AWAY_TEAM_ID_KEY = "game[away_team_id]";
        private final static String HOME_FINAL_SCORE_KEY = "game[home_final_score]";
        private final static String AWAY_FINAL_SCORE_KEY = "game[away_final_score]";

        @Override
        protected Void doInBackground(Game... args) {
            if (args.length == 0)
                return null;
            Game g = args[0];
            String contentAsString = "";
            try {
                HttpURLConnection conn = Connectivity.openURL(EXPORT_URL, "POST");
                List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair(HOME_TEAM_ID_KEY, String.valueOf(g.HomeTeam_id)));
                postParams.add(new BasicNameValuePair(AWAY_TEAM_ID_KEY, String.valueOf(g.AwayTeam_id)));
                postParams.add(new BasicNameValuePair(HOME_FINAL_SCORE_KEY, String.valueOf(g.HomeTeamFinalScore)));
                postParams.add(new BasicNameValuePair(AWAY_FINAL_SCORE_KEY, String.valueOf(g.AwayTeamFinalScore)));
                Connectivity.postContent(conn, postParams);
                conn.connect();
                // read the response
                contentAsString = Connectivity.readResponse(conn);
            } catch (MalformedURLException e) {
                contentAsString = "Malformed URL error";
                e.printStackTrace();
            } catch (IOException e) {
                contentAsString = "IO error";
                e.printStackTrace();
            }
            return null;
        }
    }

}
