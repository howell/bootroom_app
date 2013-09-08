
package com.tactical_foul.bootroom;

public class Player {

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

}
