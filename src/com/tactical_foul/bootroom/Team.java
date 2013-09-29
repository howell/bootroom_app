
package com.tactical_foul.bootroom;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class Team extends Exportable {
    private static final String LOG_TAG = "BootroomTeam";
    
    public static final int NONE = 0;

    public long id;
    public String Name;
    public String League;
    public Player[] Roster;

    public Team(long id, String name, String league, Player[] roster) {
        this.id = id;
        Name = name;
        League = league;
        Roster = roster;
    }

    public Team(int id, String name, String league) {
        this(id, name, league, new Player[0]);
    }

    @Override
    public void export() {
        super.export();
        for (Player p : Roster)
            p.export();
    }

    @Override
    protected String exportURL() {
        return extendURL("/teams");
    }

    @Override
    protected List<NameValuePair> getPostParams() {
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("team[name]", String.valueOf(Name)));
        postParams.add(new BasicNameValuePair("team[league]", String.valueOf(League)));
        return postParams;
    }

    @Override
    protected String logTag() {
        return LOG_TAG;
    }

    public static Team createTheBeams() {
        long beams_id = 7;
        Set<Player> roster = new HashSet<Player>();
        Player zach = new Player(1, "Zach", "Smith", 31, "zps5000@gmail.com", beams_id);
        roster.add(zach);
        Player jorge = new Player(2, "Jorge", "Vazquez", 40, "jorgevazquez@utexas.edu", beams_id);
        roster.add(jorge);
        Player rudy = new Player(3, "Rudresh", "Ghosh", 0, "rudreshcalls@gmail.com", beams_id);
        roster.add(rudy);
        Player joey = new Player(4, "Joseph", "Ripley", 0, "luckyschmuck@yahoo.com", beams_id);
        roster.add(joey);
        Player brandon = new Player(5, "Brandon", "DeKosky", 28, "dekosky@che.utexas.edu", beams_id);
        roster.add(brandon);
        Player alexP = new Player(6, "Alex", "Pak", 2, "alexander.jin.pak@gmail.com", beams_id);
        roster.add(alexP);
        Player ross = new Player(7, "Ross", "Orlando", 12, "rossee028@yahoo.com", beams_id);
        roster.add(ross);
        Player jon = new Player(8, "Jon", "Laurent", 51, "jonmlaurent@gmail.com", beams_id);
        roster.add(jon);
        Player jordan = new Player(9, "Jordan", "Piedt", 26, "jordanpiedt@yahoo.com", beams_id);
        roster.add(jordan);
        Player will = new Player(10, "Will", "Kelton", 10, "wjkelton@gmail.com", beams_id);
        roster.add(will);
        Player adrian = new Player(11, "Adrian", "Lopez", 4, "Ballack13atw@yahoo.com", beams_id);
        roster.add(adrian);
        Player chrisC = new Player(12, "Chris", "Cameorn", 11, "c.cameron@utexas.edu", beams_id);
        roster.add(chrisC);
        Player greg = new Player(13, "Greg", "Mullen", 0, "gregory.m.mullen@gmail.com", beams_id);
        roster.add(greg);
        Player chrisL = new Player(14, "Chris", "Longe", 48, "chrislonge@utexas.edu", beams_id);
        roster.add(chrisL);
        Player cameron = new Player(15, "Cameron", "Faxon", 21, "cfaxon1@gmail.com", beams_id);
        roster.add(cameron);
        Player pedro = new Player(16, "Pedro", "Miquel", 50, "pedromquintal@gmail.com", beams_id);
        roster.add(pedro);
        Player alexE = new Player(17, "Alex", "Espinoza", 0, "espinoza.alex@utexas.edu", beams_id);
        roster.add(alexE);
        Player doug = new Player(18, "Doug", "Pernik", 0, "dougpernik@gmail.com", beams_id);
        roster.add(doug);
        Player jacob = new Player(19, "Jacob", "Heiser", 0, "jacobheiser@utexas.edu", beams_id);
        roster.add(jacob);
        Player alexv = new Player(20, "Alex", "Voice", 0, "alexkvoice@gmail.com", beams_id);
        roster.add(alexv);
        Player logan = new Player(21, "Logan", "Cummins", 18, "cumminslogan@gmail.com", beams_id);
        roster.add(logan);
        Team beams = new Team(beams_id, "Effusive Beams", "AMSA D4", roster.toArray(new Player[roster.size()]));
        return beams;
    }

}
