package com.sebng.minesweeper.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sebng.minesweeper.BuildConfig;
import com.sebng.minesweeper.MSApplication;
import com.sebng.minesweeper.model.MSCell;
import com.sebng.minesweeper.model.MSGame;

import java.util.ArrayList;

public class MSDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mine_sweeper";
    private static final int DATABASE_VERSION = 1;
    private static MSDatabaseHelper mInstance = null;
    private Context mContext;

    public MSDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setContext(context);
    }

    public static MSDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MSDatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (id integer primary key autoincrement" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ");",
                MSGame.DB_TABLE_NAME,
                MSGame.PARAM_KEY_DIMENSION,
                MSGame.PARAM_KEY_MINES,
                MSGame.PARAM_KEY_HAS_STARTED,
                MSGame.PARAM_KEY_HAS_ENDED,
                MSGame.PARAM_KEY_HAS_WON));

        db.execSQL(String.format("create table %s (id integer primary key autoincrement" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ");",
                MSCell.DB_TABLE_NAME,
                MSCell.PARAM_KEY_ROW_INDEX,
                MSCell.PARAM_KEY_COL_INDEX,
                MSCell.PARAM_KEY_LINEAR_INDEX,
                MSCell.PARAM_KEY_IS_EXPLORED,
                MSCell.PARAM_KEY_IS_FLAGGED,
                MSCell.PARAM_KEY_HAS_MINE,
                MSCell.PARAM_KEY_ADJACENT_MINES));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(MSApplication.LOG_TAG, "Upgrading database, which will destroy all old data");
        db.execSQL(String.format("drop table if exists %s", MSGame.DB_TABLE_NAME));
        db.execSQL(String.format("drop table if exists %s", MSCell.DB_TABLE_NAME));
        onCreate(db);
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public MSGame createNewGame(int dimension, int mines) {
        ContentValues cv = new ContentValues();
        if (loadGame() == null) {
            cv.put(MSGame.PARAM_KEY_ID, MSGame.DEFAULT_ID_VALUE); // since this app only supports 1 game at the moment, the id of game will be fixed
        }
        cv.put(MSGame.PARAM_KEY_DIMENSION, dimension);
        cv.put(MSGame.PARAM_KEY_MINES, mines);
        cv.put(MSGame.PARAM_KEY_HAS_STARTED, 0);
        cv.put(MSGame.PARAM_KEY_HAS_ENDED, 0);
        cv.put(MSGame.PARAM_KEY_HAS_WON, 0);
        if (loadGame() == null) {
            getWritableDatabase().insert(MSGame.DB_TABLE_NAME, MSGame.PARAM_KEY_ID, cv);
        } else {
            getWritableDatabase().update(MSGame.DB_TABLE_NAME, cv, MSGame.PARAM_KEY_ID + " = ?", new String[]{String.valueOf(MSGame.DEFAULT_ID_VALUE)});
        }
        return new MSGame(dimension, mines, false, false, false);
    }

    public MSGame loadGame() {
        ArrayList<MSGame> games = new ArrayList<MSGame>();
        Cursor result = getReadableDatabase()
                .rawQuery(String.format("select id" +
                                ",  %s" +
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
                        MSGame.DB_TABLE_NAME), null);
        while (result.moveToNext()) {
            int id = result.getInt(0);
            games.add(new MSGame(
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3) == 1,
                    result.getInt(4) == 1,
                    result.getInt(5) == 1
            ));
        }
        result.close();
        return games.isEmpty() ? null : games.get(0);
    }
}
