
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

    private static final int SUBS_MENU_GROUP = 42;
    private static final int SWAP_MENU_GROUP = 43;

    private static final String LOG_TAG = "Main";

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
        tvAwayTeam.setText("Real Austin");
        SubbedPlayers = new HashMap<Long, Player>();
        for (int i = 0; i < HomeTeam.Roster.length; ++i) {
            SubbedPlayers.put(HomeTeam.Roster[i].id, HomeTeam.Roster[i]);
        }
        FieldPlayers = new HashMap<Integer, Player>();
        CurrentGame = new Game(3, HomeTeam.id, Game.NONE, Game.NONE, Game.NONE);
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
                    if (item.getGroupId() == SUBS_MENU_GROUP) {
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
                    if (item.getGroupId() == SWAP_MENU_GROUP) {
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
                    Player p = null;
                    if (FieldPlayers.containsKey(v.getId()))
                        p = FieldPlayers.get(v.getId());
                    else
                        Log.w(LOG_TAG, "no player!");
                    GameEvent ge = null;
                    switch (item.getItemId()) {

                        case R.id.substitution: {
                            // Create a submenu with the available players to
                            // sub in
                            SubMenu subMenu = item.getSubMenu();
                            for (long i : SubbedPlayers.keySet()) {
                                subMenu.add(SUBS_MENU_GROUP, (int) i, Menu.NONE,
                                        SubbedPlayers.get(i).fullName());
                            }
                            return true;
                        }

                        case R.id.swap: {
                            // create a submenu with the other field players
                            SubMenu subMenu = item.getSubMenu();
                            for (long i : FieldPlayers.keySet()) {
                                if (i == (long) v.getId())
                                    continue;
                                subMenu.add(SWAP_MENU_GROUP, (int) i, Menu.NONE,
                                        FieldPlayers.get(i).fullName());
                            }
                            return true;
                        }

                        case R.id.gk_conceded:
                            // create event and update away team score
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.SHOT_AGAINST, GameEvent.CONCEDED, Player.NONE);
                            ++AwayScore;
                            tvAwayScore.setText(Integer.toString(AwayScore));
                            break;

                        case R.id.gk_save:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.SHOT_AGAINST, GameEvent.SAVE, Player.NONE);

                            break;

                        case R.id.pass_completed:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.PASS, GameEvent.PASS_COMPLETED, Player.NONE);
                            break;

                        case R.id.pass_incompleted:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.PASS, GameEvent.PASS_INCOMPLETED, Player.NONE);
                            break;

                        case R.id.pass_assist:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.PASS, GameEvent.ASSIST, Player.NONE);
                            break;

                        case R.id.pass_key:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.PASS, GameEvent.PASS_KEY, Player.NONE);
                            break;

                        case R.id.shot_on_target:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.SHOT, GameEvent.SHOT_ON_TARGET, Player.NONE);
                            break;

                        case R.id.shot_off_target:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.SHOT, GameEvent.SHOT_OFF_TARGET, Player.NONE);
                            break;

                        case R.id.shot_goal:
                            if (p == null)
                                return false;
                            // update score
                            ++HomeScore;
                            tvHomeScore.setText(Integer.toString(HomeScore));
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.SHOT, GameEvent.GOAL, Player.NONE);
                            break;

                        case R.id.tackle:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.TACKLE, GameEvent.NONE, Player.NONE);
                            break;

                        case R.id.foul:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.FOUL, GameEvent.NONE, Player.NONE);
                            break;

                        case R.id.yellow_card:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.YELLOW_CARD, GameEvent.NONE, Player.NONE);
                            break;

                        case R.id.red_card:
                            if (p == null)
                                return false;
                            ge = new GameEvent(seconds, p.id, CurrentGame.id,
                                    GameEvent.RED_CARD, GameEvent.NONE, Player.NONE);
                            break;

                        default:
                            break;
                    }
                    if (ge != null)
                        GameEventDB.addEvent(ge);
                    return true;
                }

            });
            pm.inflate(R.menu.popup);
            pm.show();
        }
    }

    /*
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

}
