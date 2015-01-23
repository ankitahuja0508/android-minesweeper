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
import java.util.List;

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
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement" +
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
                MSGame.PARAM_KEY_HAS_WON));

        db.execSQL(String.format("create table %s (%s integer primary key autoincrement" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ");",
                MSCell.DB_TABLE_NAME,
                MSGame.PARAM_KEY_ID,
                MSCell.PARAM_KEY_ROW_INDEX,
                MSCell.PARAM_KEY_COL_INDEX,
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
                .rawQuery(String.format("select %s" +
                                ",  %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                " from %s",
                        MSGame.PARAM_KEY_ID,
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

    public void deleteAllCells() {
        getWritableDatabase().delete(MSCell.DB_TABLE_NAME, null, null);
    }

    public List<MSCell> resetCells(int dimension, int mines, int row_index_first_move, int col_index_first_move) {
        deleteAllCells();
        List<MSCell> cells = generateCells(dimension, mines);
        for (MSCell cell : cells) {
            ContentValues cv = new ContentValues();
            cv.put(MSCell.PARAM_KEY_ID, cell.getId());
            cv.put(MSCell.PARAM_KEY_ROW_INDEX, cell.getRowIndex());
            cv.put(MSCell.PARAM_KEY_COL_INDEX, cell.getColIndex());
            cv.put(MSCell.PARAM_KEY_IS_EXPLORED, cell.getIsExplored());
            cv.put(MSCell.PARAM_KEY_IS_FLAGGED, cell.getIsFlagged());
            cv.put(MSCell.PARAM_KEY_HAS_MINE, cell.getHasMine());
            cv.put(MSCell.PARAM_KEY_ADJACENT_MINES, cell.getAdjacentMines());
            getWritableDatabase().insert(MSCell.DB_TABLE_NAME, MSCell.PARAM_KEY_ID, cv);
            cells.add(cell);
        }
        return cells;
    }

    public List<MSCell> generateCells(int dimension, int mines) {
        ArrayList<MSCell> cells = new ArrayList<MSCell>();
        for (int i = 0, k = 1; i < dimension; i++) {
            for (int j = 0; j < dimension; j++, k++) {
                MSCell cell = new MSCell(k, i, j, false, false, false, 0);
                cells.add(cell);
            }
        }
        return cells;
    }

    public List<MSCell> loadCells() {
        ArrayList<MSCell> cells = new ArrayList<MSCell>();
        Cursor result = getReadableDatabase()
                .rawQuery(String.format("select %s" +
                                ",  %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                " from %s",
                        MSCell.PARAM_KEY_ID,
                        MSCell.PARAM_KEY_ROW_INDEX,
                        MSCell.PARAM_KEY_COL_INDEX,
                        MSCell.PARAM_KEY_IS_EXPLORED,
                        MSCell.PARAM_KEY_IS_FLAGGED,
                        MSCell.PARAM_KEY_HAS_MINE,
                        MSCell.PARAM_KEY_ADJACENT_MINES,
                        MSCell.DB_TABLE_NAME), null);
        while (result.moveToNext()) {
            cells.add(new MSCell(
                    result.getInt(0),
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3) == 1,
                    result.getInt(4) == 1,
                    result.getInt(5) == 1,
                    result.getInt(6)
            ));
        }
        result.close();
        return cells;
    }
}
