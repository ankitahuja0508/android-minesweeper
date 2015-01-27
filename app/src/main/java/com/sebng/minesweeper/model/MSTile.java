package com.sebng.minesweeper.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.sebng.minesweeper.helper.MSDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class MSTile extends MSObject {
    public final static String DB_TABLE_NAME = "tiles";
    public final static String PARAM_KEY_ROW_INDEX = "row_index"; // zero-based
    public final static String PARAM_KEY_COL_INDEX = "col_index"; // zero-based
    public final static String PARAM_KEY_IS_EXPLORED = "is_explored";
    public final static String PARAM_KEY_IS_FLAGGED = "is_flagged";
    public final static String PARAM_KEY_HAS_MINE = "has_mine";
    public final static String PARAM_KEY_ADJACENT_MINES = "adjacent_mines";

    private Integer mId = null;
    private Integer mRowIndex = null;
    private Integer mColIndex = null;
    private Boolean mIsExplored = null;
    private Boolean mIsFlagged = null;
    private Boolean mHasMine = null;
    private Integer mAdjacentMines = null;

    public MSTile(Integer id,
                  Integer rowIndex,
                  Integer colIndex,
                  Boolean isExplored,
                  Boolean isFlagged,
                  Boolean hasMine,
                  Integer adjacentMines) {
        super();

        setId(id);
        setRowIndex(rowIndex);
        setColIndex(colIndex);
        setIsExplored(isExplored);
        setIsFlagged(isFlagged);
        setHasMine(hasMine);
        setAdjacentMines(adjacentMines);
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getRowIndex() {
        return mRowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        mRowIndex = rowIndex;
    }

    public Integer getColIndex() {
        return mColIndex;
    }

    public void setColIndex(Integer colIndex) {
        mColIndex = colIndex;
    }

    public Boolean getIsExplored() {
        return mIsExplored;
    }

    public void setIsExplored(Boolean isExplored) {
        mIsExplored = isExplored;
    }

    public Boolean getIsFlagged() {
        return mIsFlagged;
    }

    public void setIsFlagged(Boolean isFlagged) {
        mIsFlagged = isFlagged;
    }

    public Boolean getHasMine() {
        return mHasMine;
    }

    public void setHasMine(Boolean hasMine) {
        mHasMine = hasMine;
    }

    public Integer getAdjacentMines() {
        return mAdjacentMines;
    }

    public void setAdjacentMines(Integer adjacentMines) {
        mAdjacentMines = adjacentMines;
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ");",
                MSTile.DB_TABLE_NAME,
                MSGame.PARAM_KEY_ID,
                MSTile.PARAM_KEY_ROW_INDEX,
                MSTile.PARAM_KEY_COL_INDEX,
                MSTile.PARAM_KEY_IS_EXPLORED,
                MSTile.PARAM_KEY_IS_FLAGGED,
                MSTile.PARAM_KEY_HAS_MINE,
                MSTile.PARAM_KEY_ADJACENT_MINES));
    }

    public static void dropTableForTiles(SQLiteDatabase db) {
        db.execSQL(String.format("drop table if exists %s", MSTile.DB_TABLE_NAME));
    }

    public static void insertOrUpdateTile(MSDatabaseHelper dbHelper, MSTile tile, boolean bUpdate) {
        if (tile != null) {
            ContentValues cv = new ContentValues();
            cv.put(MSTile.PARAM_KEY_ID, tile.getId());
            cv.put(MSTile.PARAM_KEY_ROW_INDEX, tile.getRowIndex());
            cv.put(MSTile.PARAM_KEY_COL_INDEX, tile.getColIndex());
            cv.put(MSTile.PARAM_KEY_IS_EXPLORED, tile.getIsExplored());
            cv.put(MSTile.PARAM_KEY_IS_FLAGGED, tile.getIsFlagged());
            cv.put(MSTile.PARAM_KEY_HAS_MINE, tile.getHasMine());
            cv.put(MSTile.PARAM_KEY_ADJACENT_MINES, tile.getAdjacentMines());
            if (bUpdate) {
                dbHelper.getWritableDatabase().update(MSTile.DB_TABLE_NAME, cv, MSTile.PARAM_KEY_ID + " = ?", new String[]{String.valueOf(tile.getId())});
            } else {
                dbHelper.getWritableDatabase().insert(MSTile.DB_TABLE_NAME, MSTile.PARAM_KEY_ID, cv);
            }
        }
    }

    public static void insertTile(MSDatabaseHelper dbHelper, MSTile tile) {
        insertOrUpdateTile(dbHelper, tile, false);
    }

    public static void updateTile(MSDatabaseHelper dbHelper, MSTile tile) {
        insertOrUpdateTile(dbHelper, tile, true);
    }

    public static List<MSTile> loadTiles(MSDatabaseHelper dbHelper) {
        ArrayList<MSTile> tiles = new ArrayList<>();
        Cursor result = dbHelper.getReadableDatabase()
                .rawQuery(String.format("select %s" +
                                ",  %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                " from %s",
                        MSTile.PARAM_KEY_ID,
                        MSTile.PARAM_KEY_ROW_INDEX,
                        MSTile.PARAM_KEY_COL_INDEX,
                        MSTile.PARAM_KEY_IS_EXPLORED,
                        MSTile.PARAM_KEY_IS_FLAGGED,
                        MSTile.PARAM_KEY_HAS_MINE,
                        MSTile.PARAM_KEY_ADJACENT_MINES,
                        MSTile.DB_TABLE_NAME), null);
        while (result.moveToNext()) {
            tiles.add(new MSTile(
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
        return tiles;
    }

    public static List<MSTile> generateTiles(MSDatabaseHelper dbHelper, int dimension, int mines) {
        dbHelper.getWritableDatabase().delete(MSTile.DB_TABLE_NAME, null, null);

        ArrayList<MSTile> tiles = new ArrayList<MSTile>();
        for (int i = 0, k = 1; i < dimension; i++) {
            for (int j = 0; j < dimension; j++, k++) {
                MSTile tile = new MSTile(k, i, j, false, false, false, 0);
                tiles.add(tile);
                insertTile(dbHelper, tile);
            }
        }
        return tiles;
    }

    public static MSGameState resetTiles(MSDatabaseHelper dbHelper, int dimension, int mines, int rowIndexFirstMove, int colIndexFirstMove) {
        List<MSTile> tiles = loadTiles(dbHelper);

        Hashtable<Integer, Boolean> indicesOfMineFreeTiles = new Hashtable<>();
        for (int m = -1; m <= 1; m++) {
            for (int n = -1; n <= 1; n++) {
                int rowIndexMineFreeTile = rowIndexFirstMove + m;
                int colIndexMineFreeTile = colIndexFirstMove + n;
                if (rowIndexMineFreeTile >= 0 && rowIndexMineFreeTile < dimension && colIndexMineFreeTile >= 0 && colIndexMineFreeTile < dimension) {
                    int indexMineFreeTile = rowIndexMineFreeTile * dimension + colIndexMineFreeTile;
                    indicesOfMineFreeTiles.put(indexMineFreeTile, true);
                }
            }
        }

        Hashtable<Integer, Boolean> indicesOfMines = new Hashtable<>();
        int totalTiles = dimension * dimension;
        Random random = new Random();
        do {
            int randomIndex = random.nextInt(totalTiles);
            if (!indicesOfMineFreeTiles.containsKey(randomIndex) && !indicesOfMines.containsKey(randomIndex)) {
                indicesOfMines.put(randomIndex, true);
            }
        } while (indicesOfMines.size() < mines);

        int i, j, k = 0;

        for (MSTile tile : tiles) {
            i = k / dimension;
            j = k % dimension;
            if (indicesOfMines.containsKey(k)) {
                tile.setHasMine(true);
            } else {
                int adjacentMines = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        if (m != 0 || n != 0) {
                            int rowIndexOfAdjacentTile = i + m;
                            int colIndexOfAdjacentTile = j + n;
                            if (rowIndexOfAdjacentTile >= 0 && rowIndexOfAdjacentTile < dimension && colIndexOfAdjacentTile >= 0 && colIndexOfAdjacentTile < dimension) {
                                int indexAdjacentTile = rowIndexOfAdjacentTile * dimension + colIndexOfAdjacentTile;
                                if (indicesOfMines.containsKey(indexAdjacentTile)) {
                                    adjacentMines++;
                                }
                            }
                        }
                    }
                }
                tile.setAdjacentMines(adjacentMines);
            }
            k++;
            updateTile(dbHelper, tile);
        }
        MSGame game = MSGame.loadGame(dbHelper);
        game.setHasStarted(true);
        game.saveChanges(dbHelper);
        return exploreTile(dbHelper, new MSGameState(game, tiles), rowIndexFirstMove, colIndexFirstMove);
    }

    public static void revealBlankTiles(MSDatabaseHelper dbHelper, MSGameState gameState, List<Pair<Integer, Integer>> coordinatesOfNewBlankTiles) {
        if (coordinatesOfNewBlankTiles != null && !coordinatesOfNewBlankTiles.isEmpty()) {
            List<Pair<Integer, Integer>> coordinatesOfAdditionalNewBlankTiles = new ArrayList<>();
            int dimension = gameState.getGame().getDimension();
            List<MSTile> tiles = gameState.getTiles();
            for (Pair<Integer, Integer> coordinatesOfBlankTile : coordinatesOfNewBlankTiles) {
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        if (m != 0 || n != 0) {
                            int rowIndexOfAdjacentTile = coordinatesOfBlankTile.first + m;
                            int colIndexOfAdjacentTile = coordinatesOfBlankTile.second + n;
                            if (rowIndexOfAdjacentTile >= 0 && rowIndexOfAdjacentTile < dimension && colIndexOfAdjacentTile >= 0 && colIndexOfAdjacentTile < dimension) {
                                int indexAdjacentTile = rowIndexOfAdjacentTile * dimension + colIndexOfAdjacentTile;
                                MSTile adjacentTile = tiles.get(indexAdjacentTile);
                                if (!adjacentTile.getIsExplored() && !adjacentTile.getHasMine()) {
                                    adjacentTile.setIsExplored(true);
                                    if (adjacentTile.getAdjacentMines() == 0) {
                                        coordinatesOfAdditionalNewBlankTiles.add(new Pair<>(adjacentTile.getRowIndex(), adjacentTile.getColIndex()));
                                    }
                                    updateTile(dbHelper, adjacentTile);
                                }
                            }
                        }
                    }
                }
            }
            if (!coordinatesOfAdditionalNewBlankTiles.isEmpty()) {
                revealBlankTiles(dbHelper, gameState, coordinatesOfAdditionalNewBlankTiles);
            }
        }
    }

    public static MSGameState exploreTile(MSDatabaseHelper dbHelper, MSGameState gameState, int rowIndexMove, int colIndexMove) {
        if (gameState != null) {
            MSGame game = gameState.getGame();
            if (game != null && !game.getHasEnded()) {
                List<MSTile> tiles = gameState.getTiles();
                if (tiles != null && !tiles.isEmpty()) {
                    MSTile tile = tiles.get(rowIndexMove * game.getDimension() + colIndexMove);
                    if (tile != null) {
                        tile.setIsExplored(true);
                        updateTile(dbHelper, tile);
                        if (tile.getHasMine()) {
                            game.setHasEnded(true);
                            game.setHasWon(false);
                            game.saveChanges(dbHelper);
                        } else if (tile.getAdjacentMines() == 0) {
                            List<Pair<Integer, Integer>> coordinatesOfNewBlankTiles = new ArrayList<>();
                            coordinatesOfNewBlankTiles.add(new Pair<>(rowIndexMove, colIndexMove));
                            revealBlankTiles(dbHelper, gameState, coordinatesOfNewBlankTiles);
                        }
                    }
                }
            }
        }
        return gameState;
    }

    public static int getNumberOfUnexploredTiles(MSDatabaseHelper dbHelper) {
        int num = 0;
        Cursor result = dbHelper.getReadableDatabase()
                .rawQuery(String.format("select 1 from %s where %s = 0",
                        MSTile.DB_TABLE_NAME,
                        MSTile.PARAM_KEY_IS_EXPLORED), null);
        while (result.moveToNext()) {
            num++;
        }
        result.close();
        return num;
    }

    public static List<MSTile> loadUnexploredMineFreeTiles(MSDatabaseHelper dbHelper) {
        ArrayList<MSTile> tiles = new ArrayList<>();
        Cursor result = dbHelper.getReadableDatabase()
                .rawQuery(String.format("select %s" +
                                ",  %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                " from %s where %s = 0 and %s = 0",
                        MSTile.PARAM_KEY_ID,
                        MSTile.PARAM_KEY_ROW_INDEX,
                        MSTile.PARAM_KEY_COL_INDEX,
                        MSTile.PARAM_KEY_IS_EXPLORED,
                        MSTile.PARAM_KEY_IS_FLAGGED,
                        MSTile.PARAM_KEY_HAS_MINE,
                        MSTile.PARAM_KEY_ADJACENT_MINES,
                        MSTile.DB_TABLE_NAME,
                        MSTile.PARAM_KEY_IS_EXPLORED,
                        MSTile.PARAM_KEY_HAS_MINE), null);
        while (result.moveToNext()) {
            tiles.add(new MSTile(
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
        return tiles;
    }

    public static MSGameState flagTile(MSDatabaseHelper dbHelper, MSGameState gameState, int rowIndexMove, int colIndexMove, boolean bFlag) {
        if (gameState != null) {
            MSGame game = gameState.getGame();
            if (game != null && !game.getHasEnded() && game.getEnableFlagMode()) {
                List<MSTile> tiles = gameState.getTiles();
                if (tiles != null && !tiles.isEmpty()) {
                    MSTile tile = tiles.get(rowIndexMove * game.getDimension() + colIndexMove);
                    if (tile != null && !tile.getIsExplored()) {
                        tile.setIsFlagged(bFlag);
                        updateTile(dbHelper, tile);
                    }
                }
            }
        }
        return gameState;
    }

    @Override
    public HashMap<String, String> toDict() {
        HashMap<String, String> obj = super.toDict();
        obj.put(PARAM_KEY_ID, getId() != null ? getId().toString() : null);
        obj.put(PARAM_KEY_ROW_INDEX, getRowIndex() != null ? getRowIndex().toString() : null);
        obj.put(PARAM_KEY_COL_INDEX, getColIndex() != null ? getColIndex().toString() : null);
        obj.put(PARAM_KEY_IS_EXPLORED, getIsExplored() != null ? getIsExplored().toString() : null);
        obj.put(PARAM_KEY_IS_FLAGGED, getIsFlagged() != null ? getIsFlagged().toString() : null);
        obj.put(PARAM_KEY_HAS_MINE, getHasMine() != null ? getHasMine().toString() : null);
        obj.put(PARAM_KEY_ADJACENT_MINES, getAdjacentMines() != null ? getAdjacentMines().toString() : null);
        return obj;
    }
}
