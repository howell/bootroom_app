
package com.tactical_foul.bootroom;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class Player {
    
    private static final String LOG_TAG = "Player";

    public static final int NONE = 0;

    public long id;
    public String FirstName;
    public String LastName;
    public int Number;
    public String Email;
    public long Team_id;

    public Player(long id, String firstName, String lastName, int number, String email, long team_id) {
        this.id = id;
        FirstName = firstName;
        LastName = lastName;
        Number = number;
        Email = email;
        Team_id = team_id;
    }

    public String fullName() {
        return FirstName + " " + LastName;
    }
    
    public void export() {
        ExportTask et = new ExportTask();
        et.execute(this);
    }
    
    protected class ExportTask extends AsyncTask<Player, Void, Void> {
        private final static String EXPORT_URL = "http://beams.herokuapp.com/players/";
        private final static String FIRST_NAME_KEY = "player[first_name]";
        private final static String LAST_NAME_KEY = "player[last_name]";
        private final static String NUMBER_KEY = "player[number]";
        private final static String EMAIL_KEY = "player[email]";
        private final static String TEAM_ID_KEY = "player[team_id]";

        @Override
        protected Void doInBackground(Player... args) {
            if (args.length == 0)
                return null;
            Player p = args[0];
            String contentAsString = "";
            try {
                HttpURLConnection conn = Connectivity.openURL(EXPORT_URL, "POST");
                List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair(FIRST_NAME_KEY, String.valueOf(p.FirstName)));
                postParams.add(new BasicNameValuePair(LAST_NAME_KEY, String.valueOf(p.LastName)));
                postParams.add(new BasicNameValuePair(NUMBER_KEY, String.valueOf(p.Number)));
                postParams.add(new BasicNameValuePair(EMAIL_KEY, String.valueOf(p.Email)));
                postParams.add(new BasicNameValuePair(TEAM_ID_KEY, String.valueOf(p.Team_id)));
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
