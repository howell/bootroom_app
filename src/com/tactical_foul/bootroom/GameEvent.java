
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

public class GameEvent {
    private final static String LOG_TAG = "GameEvent";
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

    /* for events where there is no other player */
    public final static int NONE = 0;

    public long id;
    public int Timestamp; // in seconds
    public long Player_id;
    public long Game_id;
    public int EventType;
    public int EventSubType; // optional secondary information (e.g. pass
                             // completed)
    public long OtherPlayer_id; // if it exists

    private static int Id_Count = 1;

    public GameEvent(long id, int timestamp, long player_id, long game_id, int eventType,
            int eventSubType, long otherPlayer_id) {
        this.id = id;
        Timestamp = timestamp;
        Player_id = player_id;
        Game_id = game_id;
        EventType = eventType;
        EventSubType = eventSubType;
        OtherPlayer_id = otherPlayer_id;
    }

    public GameEvent(int timestamp, long player_id, long game_id, int eventType, int eventSubType,
            long otherPlayer_id) {
        this(Id_Count, timestamp, player_id, game_id, eventType, eventSubType, otherPlayer_id);
        ++Id_Count;
    }

    public void export() {
        ExportTask et = new ExportTask();
        et.execute(this);
    }

    protected class ExportTask extends AsyncTask<GameEvent, Void, Void> {
        private final static String EXPORT_URL = "http://beams.herokuapp.com/game_events/";
        private final static String TIMESTAMP_KEY = "game_event[timestamp]";
        private final static String PLAYER_ID_KEY = "game_event[player_id]";
        private final static String GAME_ID_KEY = "game_event[game_id]";
        private final static String EVENT_TYPE_KEY = "game_event[event_type]";
        private final static String EVENT_SUBTYPE_KEY = "game_event[event_subtype]";
        private final static String OTHER_PLAYER_ID_KEY = "game_event[other_player_id";

        @Override
        protected Void doInBackground(GameEvent... args) {
            if (args.length == 0)
                return null;
            GameEvent ge = args[0];
            String contentAsString = "";
            try {
                HttpURLConnection conn = Connectivity.openURL(EXPORT_URL, "POST");
                List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair(TIMESTAMP_KEY, String.valueOf(ge.Timestamp)));
                postParams.add(new BasicNameValuePair(PLAYER_ID_KEY, String.valueOf(ge.Player_id)));
                postParams.add(new BasicNameValuePair(GAME_ID_KEY, String.valueOf(ge.Game_id)));
                postParams.add(new BasicNameValuePair(EVENT_TYPE_KEY, String.valueOf(ge.EventType)));
                postParams.add(new BasicNameValuePair(EVENT_SUBTYPE_KEY, String.valueOf(ge.EventSubType)));
                postParams.add(new BasicNameValuePair(OTHER_PLAYER_ID_KEY, String.valueOf(ge.OtherPlayer_id)));
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

        @Override
        protected void onPostExecute(Void arg) {
            Log.d(LOG_TAG, "finished export");
        }
    }

}
