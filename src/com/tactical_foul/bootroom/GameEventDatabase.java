
package com.tactical_foul.bootroom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GameEventDatabase extends SQLiteOpenHelper {

    private static final String LOG_TAG = "BootroomGameEventDB";

    /* sql database name */
    public static final String TABLE_GAME_EVENTS = "game_events";
    /* database columns */
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_GAME_ID = "game_id";
    public static final String COLUMN_EVENT_TYPE = "event_type";
    public static final String COLUMN_EVENT_SUBTYPE = "event_sub_type";
    public static final String COLUMN_OTHER_PLAYER_ID = "other_player_id";
    public static final String COLUMN_POSITION = "position";

    private static final String DATABASE_NAME = "game_events.db";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase Database;

    /* database create sql statement */
    private static final String DATABASE_CREATE = "create table " + TABLE_GAME_EVENTS + "("
            + COLUMN_ID + " integer primary key, " + 
            COLUMN_TIMESTAMP + " integer, " + 
            COLUMN_PLAYER_ID + " integer, " + 
            COLUMN_GAME_ID + " integer, " + 
            COLUMN_EVENT_TYPE + " integer, " +
            COLUMN_EVENT_SUBTYPE + " integer, " +
            COLUMN_OTHER_PLAYER_ID + " integer, " +
            COLUMN_POSITION + " string);";

    public GameEventDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d(LOG_TAG, "Create DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_EVENTS);
        onCreate(db);
    }

    public boolean addEvent(GameEvent ge) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TIMESTAMP, ge.timestamp);
        cv.put(COLUMN_PLAYER_ID, ge.Player_id);
        cv.put(COLUMN_GAME_ID, ge.Game_id);
        cv.put(COLUMN_EVENT_TYPE, ge.EventType);
        cv.put(COLUMN_EVENT_SUBTYPE, ge.EventSubType);
        cv.put(COLUMN_OTHER_PLAYER_ID, ge.OtherPlayer_id);
        cv.put(COLUMN_POSITION, ge.Position);
        long id = Database.insert(TABLE_GAME_EVENTS, null, cv);
        if (id == -1) {
            Log.e(LOG_TAG, "Error writing DB");
            return false;
        }
        Log.d(LOG_TAG, "Add Event: " + Long.toString(id));
        return true;
    }

    public void reset() {
        onUpgrade(Database, DATABASE_VERSION, DATABASE_VERSION);
        Log.d(LOG_TAG, "Reset DB");
    }
    
    public void export_all() {
        Cursor c = Database.query(TABLE_GAME_EVENTS, null, null, null, null, null, null);
        while (c.moveToNext()) {
            GameEvent ge = cursorToGameEvent(c);
            ge.export();
        }
    }
    
    protected static GameEvent cursorToGameEvent(Cursor c) {
        int timestamp = c.getInt(c.getColumnIndex(COLUMN_TIMESTAMP));
        long player_id = c.getLong(c.getColumnIndex(COLUMN_PLAYER_ID));
        long game_id = c.getLong(c.getColumnIndex(COLUMN_GAME_ID));
        int event_type = c.getInt(c.getColumnIndex(COLUMN_EVENT_TYPE));
        int event_subtype = c.getInt(c.getColumnIndex(COLUMN_EVENT_SUBTYPE));
        long other_player_id = c.getLong(c.getColumnIndex(COLUMN_OTHER_PLAYER_ID));
        String position = c.getString(c.getColumnIndex(COLUMN_POSITION));
        return new GameEvent(timestamp, player_id, game_id, event_type, event_subtype, other_player_id, position);
    }

}
