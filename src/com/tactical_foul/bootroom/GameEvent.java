
package com.tactical_foul.bootroom;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GameEvent extends Exportable {
    private final static String LOG_TAG = "BootroomGameEvent";
    /* Event Types */

    /* Primary Types */
    public final static int PASS = 1;
    public final static int SHOT = 2;
    public final static int SUBSTITUTION = 3;
    public final static int SHOT_AGAINST = 4;
    public final static int TACKLE = 5;
    public final static int FOUL = 6;
    public final static int YELLOW_CARD = 7;
    public final static int RED_CARD = 8;
    public final static int INTERCEPTION = 9;
    public final static int CLEARANCE = 10;
    public final static int DRIBBLE = 11;
    public final static int GK_COLLECT = 12;
    public final static int PUNT = 13;
    public final static int GOAL_KICK = 14;

    /* Pass Sub-Types */
    public final static int PASS_COMPLETED = 1;
    public final static int PASS_INCOMPLETED = 2;
    public final static int PASS_KEY = 3;
    public final static int ASSIST = 4;

    /* Shot Sub-Types */
    public final static int SHOT_ON_TARGET = 1;
    public final static int SHOT_OFF_TARGET = 2;
    public final static int GOAL = 3;

    /* Substitution Sub-Types */
    public final static int SUBSTITUTION_ON = 1;
    public final static int SUBSTITUTION_OFF = 2;

    /* Shot-Against Sub-Types */
    public final static int SAVE = 1;
    public final static int CONCEDED = 2;

    /* Goalkeeper event Sub-Types */
    public final static int GK_SUCCESSFUL = 1;
    public final static int GK_UNSUCCESSFUL = 2;

    /* for events where there is no other player */
    public final static int NONE = 0;

    public int Timestamp; // in seconds
    public long Player_id;
    public long Game_id;
    public int EventType;
    // optional secondary information (e.g. pass completed)
    public int EventSubType;
    public long OtherPlayer_id; // if it exists
    public String Position;


    public GameEvent(int timestamp, long player_id, long game_id, int eventType,
            int eventSubType, long otherPlayer_id, String position) {
        Timestamp = timestamp;
        Player_id = player_id;
        Game_id = game_id;
        EventType = eventType;
        EventSubType = eventSubType;
        OtherPlayer_id = otherPlayer_id;
        Position = position;
    }

    @Override
    protected String exportURL() {
        return extendURL("/game_events");
    }

    /**
     * Check if an event represents a goal being conceded
     * @return true/false
     */
    public boolean is_goal_conceded() {
        return EventType == SHOT_AGAINST && EventSubType == CONCEDED;
    }

    /**
     * Check if an event represents a goal being scored
     * @return true/false
     */
    public boolean is_goal_scored() {
        return EventType == SHOT && EventSubType == GOAL;
    }

    @Override
    protected List<NameValuePair> getPostParams() {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("game_event[timestamp]", String.valueOf(Timestamp)));
        postParams.add(new BasicNameValuePair("game_event[player_id]", String.valueOf(Player_id)));
        postParams.add(new BasicNameValuePair("game_event[game_id]", String.valueOf(Game_id)));
        postParams.add(new BasicNameValuePair("game_event[event_type]", String.valueOf(EventType)));
        postParams.add(new BasicNameValuePair("game_event[event_subtype]", String.valueOf(EventSubType)));
        postParams.add(new BasicNameValuePair("game_event[other_player_id]", String.valueOf(OtherPlayer_id)));
        postParams.add(new BasicNameValuePair("game_event[position]", Position));
        return postParams;
    }

    @Override
    protected String logTag() {
        return LOG_TAG;
    }

}
