package com.sebng.minesweeper.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import com.sebng.minesweeper.BuildConfig;
import com.sebng.minesweeper.MSApplication;
import com.sebng.minesweeper.model.MSTile;
import com.sebng.minesweeper.model.MSGame;
import com.sebng.minesweeper.model.MSGameState;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class MSDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mine_sweeper";
    private static final int DATABASE_VERSION = 3;
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(MSApplication.LOG_TAG, "Upgrading database, which will destroy all old data");
        db.execSQL(String.format("drop table if exists %s", MSGame.DB_TABLE_NAME));
        db.execSQL(String.format("drop table if exists %s", MSTile.DB_TABLE_NAME));
        onCreate(db);
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public MSGameState createNewGame(int dimension, int mines) {
        deleteAllTiles();

        ContentValues cv = new ContentValues();
        if (loadGame() == null) {
            cv.put(MSGame.PARAM_KEY_ID, MSGame.DEFAULT_ID_VALUE); // since this app only supports 1 game at the moment, the id of game will be fixed
        }
        cv.put(MSGame.PARAM_KEY_DIMENSION, dimension);
        cv.put(MSGame.PARAM_KEY_MINES, mines);
        cv.put(MSGame.PARAM_KEY_HAS_STARTED, 0);
        cv.put(MSGame.PARAM_KEY_HAS_ENDED, 0);
        cv.put(MSGame.PARAM_KEY_HAS_WON, 0);
        cv.put(MSGame.PARAM_KEY_ENABLE_CHEAT, 0);
        cv.put(MSGame.PARAM_KEY_ENABLE_FLAG_MODE, 0);
        if (loadGame() == null) {
            getWritableDatabase().insert(MSGame.DB_TABLE_NAME, MSGame.PARAM_KEY_ID, cv);
        } else {
            getWritableDatabase().update(MSGame.DB_TABLE_NAME, cv, MSGame.PARAM_KEY_ID + " = ?", new String[]{String.valueOf(MSGame.DEFAULT_ID_VALUE)});
        }
        MSGame game = new MSGame(dimension, mines, false, false, false, false, false);

        ArrayList<MSTile> tiles = new ArrayList<MSTile>();
        for (int i = 0, k = 1; i < dimension; i++) {
            for (int j = 0; j < dimension; j++, k++) {
                MSTile tile = new MSTile(k, i, j, false, false, false, 0);
                tiles.add(tile);
                insertTile(tile);
            }
        }

        return new MSGameState(game, tiles);
    }

    public void updateGame(MSGame game) {
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
            getWritableDatabase().update(MSGame.DB_TABLE_NAME, cv, MSGame.PARAM_KEY_ID + " = ?", new String[]{String.valueOf(MSGame.DEFAULT_ID_VALUE)});
        }
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
                                ", %s" +
                                ", %s" +
                                " from %s",
                        MSGame.PARAM_KEY_ID,
                        MSGame.PARAM_KEY_DIMENSION,
                        MSGame.PARAM_KEY_MINES,
                        MSGame.PARAM_KEY_HAS_STARTED,
                        MSGame.PARAM_KEY_HAS_ENDED,
                        MSGame.PARAM_KEY_HAS_WON,
                        MSGame.PARAM_KEY_ENABLE_CHEAT,
                        MSGame.PARAM_KEY_ENABLE_FLAG_MODE,
                        MSGame.DB_TABLE_NAME), null);
        while (result.moveToNext()) {
            int id = result.getInt(0);
            games.add(new MSGame(
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3) == 1,
                    result.getInt(4) == 1,
                    result.getInt(5) == 1,
                    result.getInt(6) == 1,
                    result.getInt(7) == 1
            ));
        }
        result.close();
        return games.isEmpty() ? null : games.get(0);
    }

    public MSGameState validateGame(MSGameState gameState) {
        MSGame game = gameState.getGame();
        game.setHasEnded(true);
        game.setHasWon(getNumberOfUnexploredTiles() == game.getMines());
        updateGame(game);
        return gameState;
    }

    public void deleteAllTiles() {
        getWritableDatabase().delete(MSTile.DB_TABLE_NAME, null, null);
    }

    public MSGameState resetTiles(int dimension, int mines, int rowIndexFirstMove, int colIndexFirstMove) {
        List<MSTile> tiles = loadTiles();

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
            updateTile(tile);
        }
        MSGame game = loadGame();
        game.setHasStarted(true);
        updateGame(game);
        return exploreTile(new MSGameState(game, tiles), rowIndexFirstMove, colIndexFirstMove);
    }

    public void revealBlankTiles(MSGameState gameState, List<Pair<Integer, Integer>> coordinatesOfNewBlankTiles) {
        if (coordinatesOfNewBlankTiles != null && !coordinatesOfNewBlankTiles.isEmpty()) {
            List<Pair<Integer, Integer>> coordinatesOfAdditionalNewBlankTiles = new ArrayList<>();
            int dimension = gameState.getGame().getDimension();
            List<MSTile> tiles = gameState.getTiles();
            for (Pair<Integer, Integer> coordinatesOfBlankTile : coordinatesOfNewBlankTiles) {
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        if ((m == 0 && n != 0) || (m != 0 && n == 0)) {
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
                                    updateTile(adjacentTile);
                                }
                            }
                        }
                    }
                }
            }
            if (!coordinatesOfAdditionalNewBlankTiles.isEmpty()) {
                revealBlankTiles(gameState, coordinatesOfAdditionalNewBlankTiles);
            }
        }
    }

    public MSGameState exploreTile(MSGameState gameState, int rowIndexMove, int colIndexMove) {
        if (gameState != null) {
            MSGame game = gameState.getGame();
            if (game != null && !game.getHasEnded()) {
                List<MSTile> tiles = gameState.getTiles();
                if (tiles != null && !tiles.isEmpty()) {
                    MSTile tile = tiles.get(rowIndexMove * game.getDimension() + colIndexMove);
                    if (tile != null) {
                        tile.setIsExplored(true);
                        updateTile(tile);
                        if (tile.getHasMine()) {
                            game.setHasEnded(true);
                            game.setHasWon(false);
                            updateGame(game);
                        } else if (tile.getAdjacentMines() == 0) {
                            List<Pair<Integer, Integer>> coordinatesOfNewBlankTiles = new ArrayList<>();
                            coordinatesOfNewBlankTiles.add(new Pair<>(rowIndexMove, colIndexMove));
                            revealBlankTiles(gameState, coordinatesOfNewBlankTiles);
                        }
                    }
                }
            }
        }
        return gameState;
    }

    public MSGameState flagTile(MSGameState gameState, int rowIndexMove, int colIndexMove, boolean bFlag) {
        if (gameState != null) {
            MSGame game = gameState.getGame();
            if (game != null && !game.getHasEnded() && game.getEnableFlagMode()) {
                List<MSTile> tiles = gameState.getTiles();
                if (tiles != null && !tiles.isEmpty()) {
                    MSTile tile = tiles.get(rowIndexMove * game.getDimension() + colIndexMove);
                    if (tile != null && !tile.getIsExplored()) {
                        tile.setIsFlagged(bFlag);
                        updateTile(tile);
                    }
                }
            }
        }
        return gameState;
    }

    public List<MSTile> loadTiles() {
        ArrayList<MSTile> tiles = new ArrayList<>();
        Cursor result = getReadableDatabase()
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

    public int getNumberOfUnexploredTiles() {
        int num = 0;
        Cursor result = getReadableDatabase()
                .rawQuery(String.format("select 1 from %s where %s = 0",
                        MSTile.DB_TABLE_NAME,
                        MSTile.PARAM_KEY_IS_EXPLORED), null);
        while (result.moveToNext()) {
            num++;
        }
        result.close();
        return num;
    }

    public void insertOrUpdateTile(MSTile tile, boolean bUpdate) {
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
                getWritableDatabase().update(MSTile.DB_TABLE_NAME, cv, MSTile.PARAM_KEY_ID + " = ?", new String[]{String.valueOf(tile.getId())});
            } else {
                getWritableDatabase().insert(MSTile.DB_TABLE_NAME, MSTile.PARAM_KEY_ID, cv);
            }
        }
    }

    public void insertTile(MSTile tile) {
        insertOrUpdateTile(tile, false);
    }

    public void updateTile(MSTile tile) {
        insertOrUpdateTile(tile, true);
    }
}
