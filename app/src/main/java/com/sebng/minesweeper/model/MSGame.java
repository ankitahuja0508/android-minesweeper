package com.sebng.minesweeper.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sebng.minesweeper.helper.MSDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MSGame extends MSObject {
    public final static String DB_TABLE_NAME = "games";
    public final static int DEFAULT_ID_VALUE = 1;
    public final static String PARAM_KEY_DIMENSION = "dimension";
    public final static String PARAM_KEY_MINES = "mines";
    public final static String PARAM_KEY_HAS_STARTED = "has_started";
    public final static String PARAM_KEY_HAS_ENDED = "has_ended";
    public final static String PARAM_KEY_HAS_WON = "has_won";
    public final static String PARAM_KEY_ENABLE_CHEAT = "enable_cheat";
    public final static String PARAM_KEY_ENABLE_FLAG_MODE = "enable_flag_mode";

    private Integer mDimension = null;
    private Integer mMines = null;
    private Boolean mHasStarted = null;
    private Boolean mHasEnded = null;
    private Boolean mHasWon = null;
    private Boolean mEnableCheat = null;
    private Boolean mEnableFlagMode = null;

    public MSGame(Integer dimension,
                  Integer mines,
                  Boolean hasStarted,
                  Boolean hasEnded,
                  Boolean hasWon,
                  Boolean enableCheat,
                  Boolean enableFlagMode) {
        super();

        setDimension(dimension);
        setMines(mines);
        setHasStarted(hasStarted);
        setHasEnded(hasEnded);
        setHasWon(hasWon);
        setEnableCheat(enableCheat);
        setEnableFlagMode(enableFlagMode);
    }

    public Integer getDimension() {
        return mDimension;
    }

    public void setDimension(Integer dimension) {
        mDimension = dimension;
    }

    public Integer getMines() {
        return mMines;
    }

    public void setMines(Integer mines) {
        mMines = mines;
    }

    public Boolean getHasStarted() {
        return mHasStarted;
    }

    public void setHasStarted(Boolean hasStarted) {
        mHasStarted = hasStarted;
    }

    public Boolean getHasEnded() {
        return mHasEnded;
    }

    public void setHasEnded(Boolean hasEnded) {
        mHasEnded = hasEnded;
    }

    public Boolean getHasWon() {
        return mHasWon;
    }

    public void setHasWon(Boolean hasWon) {
        mHasWon = hasWon;
    }

    public Boolean getEnableCheat() {
        return mEnableCheat;
    }

    public void setEnableCheat(Boolean enableCheat) {
        mEnableCheat = enableCheat;
    }

    public Boolean getEnableFlagMode() {
        return mEnableFlagMode;
    }

    public void setEnableFlagMode(Boolean enableFlagMode) {
        mEnableFlagMode = enableFlagMode;
    }

    public static void createTableForGames(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ");",
                MSGame.DB_TABLE_NAME,
                MSGame.PARAM_KEY_ID,
                MSGame.PARAM_KEY_DIMENSION,
                MSGame.PARAM_KEY_MINES,
                MSGame.PARAM_KEY_HAS_STARTED,
                MSGame.PARAM_KEY_HAS_ENDED,
                MSGame.PARAM_KEY_HAS_WON,
                MSGame.PARAM_KEY_ENABLE_CHEAT,
                MSGame.PARAM_KEY_ENABLE_FLAG_MODE));
    }

    public static void dropTableForGames(SQLiteDatabase db) {
        db.execSQL(String.format("drop table if exists %s", MSGame.DB_TABLE_NAME));
    }

    public static MSGameState createNewGame(MSDatabaseHelper dbHelper, int dimension, int mines) {
        ContentValues cv = new ContentValues();
        if (MSGame.loadGame(dbHelper) == null) {
            cv.put(MSGame.PARAM_KEY_ID, MSGame.DEFAULT_ID_VALUE); // since this app only supports 1 game at the moment, the id of game will be fixed
        }
        cv.put(MSGame.PARAM_KEY_DIMENSION, dimension);
        cv.put(MSGame.PARAM_KEY_MINES, mines);
        cv.put(MSGame.PARAM_KEY_HAS_STARTED, 0);
        cv.put(MSGame.PARAM_KEY_HAS_ENDED, 0);
        cv.put(MSGame.PARAM_KEY_HAS_WON, 0);
        cv.put(MSGame.PARAM_KEY_ENABLE_CHEAT, 0);
        cv.put(MSGame.PARAM_KEY_ENABLE_FLAG_MODE, 0);
        if (MSGame.loadGame(dbHelper) == null) {
            dbHelper.getWritableDatabase().insert(MSGame.DB_TABLE_NAME, MSGame.PARAM_KEY_ID, cv);
        } else {
            dbHelper.getWritableDatabase().update(MSGame.DB_TABLE_NAME, cv, MSGame.PARAM_KEY_ID + " = ?", new String[]{String.valueOf(MSGame.DEFAULT_ID_VALUE)});
        }
        MSGame game = new MSGame(dimension, mines, false, false, false, false, false);

        List<MSTile> tiles = MSTile.generateTiles(dbHelper, dimension, mines);

        return new MSGameState(game, tiles);
    }

    public static void updateGame(MSDatabaseHelper dbHelper, MSGame game) {
        if (game != null) {
            ContentValues cv = new ContentValues();
            cv.put(MSGame.PARAM_KEY_ID, MSGame.DEFAULT_ID_VALUE);
            cv.put(MSGame.PARAM_KEY_DIMENSION, game.getDimension());
            cv.put(MSGame.PARAM_KEY_MINES, game.getMines());
            cv.put(MSGame.PARAM_KEY_HAS_STARTED, game.getHasStarted() ? 1 : 0);
            cv.put(MSGame.PARAM_KEY_HAS_ENDED, game.getHasEnded() ? 1 : 0);
            cv.put(MSGame.PARAM_KEY_HAS_WON, game.getHasWon() ? 1 : 0);
            cv.put(MSGame.PARAM_KEY_ENABLE_CHEAT, game.getEnableCheat() ? 1 : 0);
            cv.put(MSGame.PARAM_KEY_ENABLE_FLAG_MODE, game.getEnableFlagMode() ? 1 : 0);
            dbHelper.getWritableDatabase().update(MSGame.DB_TABLE_NAME, cv, MSGame.PARAM_KEY_ID + " = ?", new String[]{String.valueOf(MSGame.DEFAULT_ID_VALUE)});
        }
    }

    public static MSGame loadGame(MSDatabaseHelper dbHelper) {
        ArrayList<MSGame> games = new ArrayList<>();
        Cursor result = dbHelper.getReadableDatabase()
                .rawQuery(String.format("select %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                " from %s",
                        MSGame.PARAM_KEY_DIMENSION,
                        MSGame.PARAM_KEY_MINES,
                        MSGame.PARAM_KEY_HAS_STARTED,
                        MSGame.PARAM_KEY_HAS_ENDED,
                        MSGame.PARAM_KEY_HAS_WON,
                        MSGame.PARAM_KEY_ENABLE_CHEAT,
                        MSGame.PARAM_KEY_ENABLE_FLAG_MODE,
                        MSGame.DB_TABLE_NAME), null);
        while (result.moveToNext()) {
            games.add(new MSGame(
                    result.getInt(0),
                    result.getInt(1),
                    result.getInt(2) == 1,
                    result.getInt(3) == 1,
                    result.getInt(4) == 1,
                    result.getInt(5) == 1,
                    result.getInt(6) == 1
            ));
        }
        result.close();
        return games.isEmpty() ? null : games.get(0);
    }

    public static MSGameState validateGame(MSDatabaseHelper dbHelper, MSGameState gameState) {
        MSGame game = gameState.getGame();
        if (game != null) {
            game.setHasEnded(true);
            game.setHasWon(MSTile.getNumberOfUnexploredTiles(dbHelper) == game.getMines());
            updateGame(dbHelper, game);
        }
        return gameState;
    }

    @Override
    public HashMap<String, String> toDict() {
        HashMap<String, String> obj = super.toDict();
        obj.put(PARAM_KEY_DIMENSION, getDimension() != null ? getDimension().toString() : null);
        obj.put(PARAM_KEY_MINES, getMines() != null ? getMines().toString() : null);
        obj.put(PARAM_KEY_HAS_STARTED, getHasStarted() != null ? getHasStarted().toString() : null);
        obj.put(PARAM_KEY_HAS_ENDED, getHasEnded() != null ? getHasEnded().toString() : null);
        obj.put(PARAM_KEY_HAS_WON, getHasWon() != null ? getHasWon().toString() : null);
        obj.put(PARAM_KEY_ENABLE_CHEAT, getEnableCheat() != null ? getEnableCheat().toString() : null);
        obj.put(PARAM_KEY_ENABLE_FLAG_MODE, getEnableFlagMode() != null ? getEnableFlagMode().toString() : null);
        return obj;
    }
}
