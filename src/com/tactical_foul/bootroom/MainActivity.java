
package com.tactical_foul.bootroom;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "BootroomMain";

    private Button bST, bCF, bLW, bLCM, bRCM, bRW, bLB, bLCB, bRCB, bRB, bGK;
    private TextView tvClock;
    private TextView tvHomeScore;
    private TextView tvAwayScore;
    private TextView tvHomeTeam;
    private TextView tvAwayTeam;
    private Timer myTimer;
    private int seconds = 0;
    private int HomeScore = 0;
    private int AwayScore = 0;

    private Game CurrentGame;
    private Team HomeTeam;
    // map of player_id -> player
    private Map<Long, Player> SubbedPlayers;
    // map of button id -> player at that position
    private Map<Integer, Player> FieldPlayers;

    /* databases */
    GameEventDatabase GameEventDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connectivity.init();
        initButtons();
        initClock();
        tvHomeScore = (TextView) findViewById(R.id.home_score);
        tvAwayScore = (TextView) findViewById(R.id.away_score);
        tvHomeTeam = (TextView) findViewById(R.id.home_team);
        tvAwayTeam = (TextView) findViewById(R.id.away_team);
        HomeTeam = Team.createTheBeams();
        tvHomeTeam.setText(HomeTeam.Name);
        tvAwayTeam.setText("Cervezas");
        SubbedPlayers = new HashMap<Long, Player>();
        for (int i = 0; i < HomeTeam.Roster.length; ++i) {
            SubbedPlayers.put(HomeTeam.Roster[i].id, HomeTeam.Roster[i]);
        }
        FieldPlayers = new HashMap<Integer, Player>();
        CurrentGame = new Game(4, HomeTeam.id, Game.NONE, Game.NONE, Game.NONE);
        GameEventDB = new GameEventDatabase(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_events:
                GameEventDB.reset();
                break;

            case R.id.action_export_events:
                GameEventDB.export_all();
                break;

            case R.id.action_export_beams:
//                GameEvent ge = new GameEvent(1337, 1000, 50, 228,
//                        GameEvent.SHOT, GameEvent.GOAL, Player.NONE);
//                Player test = new Player(50, "thierry", "henry", 14, "titi@gmail.com", 7);
//                ge.export();
                break;

            case R.id.action_export_game:
                CurrentGame.export();

            default:
                break;
        }
        String name = getResources().getResourceEntryName(item.getItemId());
        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
        return super.onMenuItemSelected(featureId, item);
    }

    private class PositionButton implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            PopupMenu pm = new PopupMenu(v.getContext(), v);
            pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // there are two basic categories of menu items:
                    //  1) those where we need to dynamically generate a further menu (e.g. sub, swap)
                    //  2) those where we need to carry out some action, like record a game-event or change position
                    if (item.getItemId() == R.id.swap) {
                        // create a submenu with the other field players
                        SubMenu subMenu = item.getSubMenu();
                        for (int i : FieldPlayers.keySet()) {
                            if (i == v.getId())  // skip the current player
                                continue;
                            subMenu.add(R.id.popup_swap_menu_group, i, Menu.NONE,
                                    FieldPlayers.get(i).fullName());
                        }
                        return true;
                    }
                    if (item.getItemId() == R.id.substitution) {
                        // Create a submenu with the available players to sub in
                        SubMenu subMenu = item.getSubMenu();
                        for (long i : SubbedPlayers.keySet())
                            subMenu.add(R.id.popup_subs_menu_group, (int) i, Menu.NONE,
                                    SubbedPlayers.get(i).fullName());
                        return true;
                    }
                    switch (item.getGroupId()) {
                        case R.id.popup_subs_menu_group : {
                            Player playerOn = SubbedPlayers.get((long) item.getItemId());
                            if (playerOn == null) {
                                Log.e(LOG_TAG, "tried to sub on null!");
                                return false;
                            }
                            // Don't generate an event if there isn't a player
                            // coming off - assume the initial lineup is being set
                            if (FieldPlayers.containsKey(v.getId())) {
                                Player playerOff = FieldPlayers.get(v.getId());
                                GameEvent subOn = new GameEvent(seconds, playerOn.id, CurrentGame.id,
                                        GameEvent.SUBSTITUTION, GameEvent.SUBSTITUTION_ON, playerOff.id);
                                GameEvent subOff = new GameEvent(seconds, playerOff.id, CurrentGame.id,
                                        GameEvent.SUBSTITUTION, GameEvent.SUBSTITUTION_OFF, playerOn.id);
                                GameEventDB.addEvent(subOn);
                                GameEventDB.addEvent(subOff);
                                SubbedPlayers.put(playerOff.id, playerOff);
                            }
                            FieldPlayers.put(v.getId(), playerOn);
                            SubbedPlayers.remove(playerOn.id);
                            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT)
                                    .show();
                            ((TextView) v).setText(item.getTitle());
                            return true;
                        }

                        case R.id.popup_swap_menu_group : {
                            Player p1 = FieldPlayers.get(v.getId());
                            Player p2 = FieldPlayers.get(item.getItemId());
                            // swap
                            FieldPlayers.put(v.getId(), p2);
                            FieldPlayers.put(item.getItemId(), p1);
                            ((TextView) v).setText(item.getTitle());
                            ((TextView) findViewById(item.getItemId())).setText(p1.FirstName + " "
                                    + p1.LastName);
                            return true;
                        }

                        case R.id.create_event_menu_group : {
                            GameEvent ge = gameEventFromMenuId(item.getItemId());
                            Player p = FieldPlayers.get(v.getId());
                            ge.Player_id = p.id;
                            GameEventDB.addEvent(ge);
                            // update score if needed
                            if(ge.EventSubType == GameEvent.GOAL) {
                                ++CurrentGame.HomeTeamFinalScore;
                                tvHomeScore.setText(Integer.toString(CurrentGame.HomeTeamFinalScore));
                            } else if (ge.EventSubType == GameEvent.CONCEDED) {
                                ++CurrentGame.AwayTeamFinalScore;
                                tvAwayScore.setText(Integer.toString(CurrentGame.AwayTeamFinalScore));
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });
            pm.inflate(R.menu.popup);
            pm.show();
        }
    }

    /**
     * Create a game event with the proper type, subtype, and timestamp from the id of a menu item
     * @param menu_id id of the popupmenu item that was pressed
     * @return game event with the proper type, subtype, and timestamp from the id of a menu item
     */
    GameEvent gameEventFromMenuId(int menu_id) {
        long player_id = Player.NONE;
        int timestamp = seconds;
        int type = type_for_menu_id(menu_id);
        int subtype = subtype_for_menu_id(menu_id);
        long other_player_id = Player.NONE;
        return new GameEvent(timestamp, player_id, CurrentGame.id, type, subtype, other_player_id);
    }

    /**
     * Get the game event primary type for a menu item
     * @param menu_id
     * @return
     */
    int type_for_menu_id(int menu_id) {
        switch (menu_id) {
            case R.id.gk_conceded: case R.id.gk_save:
                return GameEvent.SHOT_AGAINST;
            case R.id.pass_completed:case R.id.pass_assist:
            case R.id.pass_incompleted:case R.id.pass_key:
                return GameEvent.PASS;
            case R.id.shot_on_target: case R.id.shot_goal:case R.id.shot_off_target:
                return GameEvent.SHOT;
            case R.id.tackle:
                return GameEvent.TACKLE;
            case R.id.interception:
                return GameEvent.INTERCEPTION;
            case R.id.clearance:
                return GameEvent.CLEARANCE;
            case R.id.dribble:
                return GameEvent.DRIBBLE;
            case R.id.foul:
                return GameEvent.FOUL;
            case R.id.yellow_card:
                return GameEvent.YELLOW_CARD;
            case R.id.red_card:
                return GameEvent.RED_CARD;
            default:
                Log.w(LOG_TAG, "failed to match menu item to event type!");
                return GameEvent.NONE;
        }
    }

    /**
     * Get the game event subtype, if it exists, for a menu item
     * @param menu_id
     * @return the subtype corresponding for a menu item if it exists, otherwise GameEvent.NONE
     */
    int subtype_for_menu_id(int menu_id) {
        switch (menu_id) {
            case R.id.gk_conceded:
                return GameEvent.CONCEDED;
            case R.id.gk_save:
                return GameEvent.SAVE;
            case R.id.pass_completed:
                return GameEvent.PASS_COMPLETED;
            case R.id.pass_incompleted:
                return GameEvent.PASS_INCOMPLETED;
            case R.id.pass_assist:
                return GameEvent.ASSIST;
            case R.id.pass_key:
                return GameEvent.PASS_KEY;
            case R.id.shot_on_target:
                return GameEvent.SHOT_ON_TARGET;
            case R.id.shot_off_target:
                return GameEvent.SHOT_OFF_TARGET;
            case R.id.shot_goal:
                return GameEvent.GOAL;
            default:
                return GameEvent.NONE;
        }
    }


    private void initButtons() {
        PositionButton listener = new PositionButton();
        bST = (Button) findViewById(R.id.button_st);
        bST.setOnClickListener(listener);
        bCF = (Button) findViewById(R.id.button_cf);
        bCF.setOnClickListener(listener);
        bLW = (Button) findViewById(R.id.button_lw);
        bLW.setOnClickListener(listener);
        bLCM = (Button) findViewById(R.id.button_lcm);
        bLCM.setOnClickListener(listener);
        bRCM = (Button) findViewById(R.id.button_rcm);
        bRCM.setOnClickListener(listener);
        bRW = (Button) findViewById(R.id.button_rw);
        bRW.setOnClickListener(listener);
        bLB = (Button) findViewById(R.id.button_lb);
        bLB.setOnClickListener(listener);
        bLCB = (Button) findViewById(R.id.button_lcb);
        bLCB.setOnClickListener(listener);
        bRCB = (Button) findViewById(R.id.button_rcb);
        bRCB.setOnClickListener(listener);
        bRB = (Button) findViewById(R.id.button_rb);
        bRB.setOnClickListener(listener);
        bGK = (Button) findViewById(R.id.button_gk);
        bGK.setOnClickListener(listener);
    }

    /**
     * Create a timer that will increment every second and update a text view with the time in the format
     * MM:SS
     */
    private void initClock() {
        tvClock = (TextView) findViewById(R.id.clock);
        seconds = 0;
        tvClock.setText(seconds_to_String(seconds));
        tvClock.setOnClickListener(new View.OnClickListener() {
            private boolean running = false;

            @Override
            public void onClick(View v) {
                if (running) {
                    myTimer.cancel();
                    running = false;
                } else {
                    myTimer = new Timer();
                    myTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            timerUpdate();
                        }
                    }, 0, 1000);
                    running = true;
                }

            }
        });
    }

    private void timerUpdate() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ++seconds;
                tvClock.setText(seconds_to_String(seconds));
            }
        });
    }

    /**
     * formats seconds as mm:ss, including leading 0's
     */
    private String seconds_to_String(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        String fmt = "";
        if (minutes < 10)
            fmt += '0';
        fmt += minutes;
        fmt += ":";
        if (secs < 10)
            fmt += "0";
        fmt += secs;
        return fmt;
    }
}
