
package com.tactical_foul.bootroom;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class Game extends Exportable {

    public static final String LOG_TAG = "Game";
    
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

    @Override
    protected String exportURL() {
        return extendURL("/games");
    }

    @Override
    protected List<NameValuePair> getPostParams() {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("game[home_team_id]", String.valueOf(HomeTeam_id)));
        postParams.add(new BasicNameValuePair("game[away_team_id]", String.valueOf(AwayTeam_id)));
        postParams.add(new BasicNameValuePair("game[home_final_score]", String.valueOf(HomeTeamFinalScore)));
        postParams.add(new BasicNameValuePair("game[away_final_score]", String.valueOf(AwayTeamFinalScore)));
        return postParams;
    }

    @Override
    protected String logTag() {
        return LOG_TAG;
    }

}
