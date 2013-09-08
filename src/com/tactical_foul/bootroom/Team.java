
package com.tactical_foul.bootroom;

import java.util.HashSet;
import java.util.Set;

public class Team {
    
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

    public static Team createTheBeams() {
        Set<Player> roster = new HashSet<Player>();
        Player zach = new Player(1, "Zach", "Smith", 31, "zps5000@gmail.com", 1);
        roster.add(zach);
        Player jorge = new Player(2, "Jorge", "Vazquez", 40, "jorgevazquez@utexas.edu", 1);
        roster.add(jorge);
        Player rudy = new Player(3, "Rudresh", "Ghosh", 0, "rudreshcalls@gmail.com", 1);
        roster.add(rudy);
        Player joey = new Player(4, "Joseph", "Ripley", 0, "luckyschmuck@yahoo.com", 1);
        roster.add(joey);
        Player brandon = new Player(5, "Brandon", "DeKosky", 28, "dekosky@che.utexas.edu", 1);
        roster.add(brandon);
        Player alexP = new Player(6, "Alex", "Pak", 2, "alexander.jin.pak@gmail.com", 1);
        roster.add(alexP);
        Player ross = new Player(7, "Ross", "Orlando", 12, "rossee028@yahoo.com", 1);
        roster.add(ross);
        Player jon = new Player(8, "Jon", "Laurent", 51, "jonmlaurent@gmail.com", 1);
        roster.add(jon);
        Player jordan = new Player(9, "Jordan", "Piedt", 26, "jordanpiedt@yahoo.com", 1);
        roster.add(jordan);
        Player will = new Player(10, "Will", "Kelton", 10, "wjkelton@gmail.com", 1);
        roster.add(will);
        Player adrian = new Player(11, "Adrian", "Lopez", 4, "Ballack13atw@yahoo.com", 1);
        roster.add(adrian);
        Player chrisC = new Player(12, "Chris", "Cameorn", 11, "c.cameron@utexas.edu", 1);
        roster.add(chrisC);
        Player greg = new Player(13, "Greg", "Mullen", 0, "gregory.m.mullen@gmail.com", 1);
        roster.add(greg);
        Player chrisL = new Player(14, "Chris", "Longe", 48, "chrislonge@utexas.edu", 1);
        roster.add(chrisL);
        Player cameron = new Player(15, "Cameron", "Faxon", 21, "cfaxon1@gmail.com", 1);
        roster.add(cameron);
        Player pedro = new Player(16, "Pedro", "Miquel", 50, "pedromquintal@gmail.com", 1);
        roster.add(pedro);
        Player alexE = new Player(17, "Alex", "Espinoza", 0, "espinoza.alex@utexas.edu", 1);
        roster.add(alexE);
        Player doug = new Player(18, "Doug", "Pernik", 0, "dougpernik@gmail.com", 1);
        roster.add(doug);
        Player jacob = new Player(19, "Jacob", "Heiser", 0, "jacobheiser@utexas.edu", 1);
        roster.add(jacob);
        Team beams = new Team(1, "Effusive Beams", "AMSA D4", roster.toArray(new Player[roster.size()]));
        return beams;
    }

}
