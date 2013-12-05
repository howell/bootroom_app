
package com.tactical_foul.bootroom;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Player extends JSONExportable {

    private static final String LOG_TAG = "BootroomPlayer";

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

    @Override
    protected String logTag() {
        return LOG_TAG;
    }

    @Override
    protected String exportURL() {
        return extendURL("/players");
    }

    @Override
    protected JSONObject toJSON() {
        try {
            JSONObject j = new JSONObject();
            j.put("first_name", FirstName);
            j.put("last_name", LastName);
            j.put("number", String.valueOf(Number));
            j.put("email", Email);
            j.put("team_id", String.valueOf(Team_id));
            return j;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected List<NameValuePair> getPostParams() {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("player[first_name]", String.valueOf(FirstName)));
        postParams.add(new BasicNameValuePair("player[last_name]", String.valueOf(LastName)));
        postParams.add(new BasicNameValuePair("player[number]", String.valueOf(Number)));
        postParams.add(new BasicNameValuePair("player[email]", String.valueOf(Email)));
        postParams.add(new BasicNameValuePair("player[team_id]", String.valueOf(Team_id)));
        return postParams;
    }

}
