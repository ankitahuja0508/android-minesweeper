package com.sebng.minesweeper.model;

import android.util.Pair;

import com.sebng.minesweeper.helper.MSDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class MSGameState extends MSObject {
    public final static String PARAM_KEY_GAME = "game";
    public final static String PARAM_KEY_TILES = "tiles";
    private MSGame mGame = null;
    private List<MSTile> mTiles = null;

    public MSGameState(MSGame game, List<MSTile> tile) {
        super();

        setGame(game);
        setTiles(tile);
    }

    public void resetGame(MSDatabaseHelper dbHelper, int rowIndexFirstMove, int colIndexFirstMove) {
        MSGame game = getGame();
        if (game != null) {
            List<MSTile> tiles = getTiles();
            if (tiles != null && !tiles.isEmpty()) {
                int dimension = game.getDimension();
                int mines = game.getMines();
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
                    tile.saveChanges(dbHelper);
                }
            }
            game.setHasStarted(true);
            game.saveChanges(dbHelper);
        }
    }

    public void revealBlankTiles(MSDatabaseHelper dbHelper, List<Pair<Integer, Integer>> coordinatesOfNewBlankTiles) {
        if (coordinatesOfNewBlankTiles != null && !coordinatesOfNewBlankTiles.isEmpty()) {
            MSGame game = getGame();
            if (game != null && !game.getHasEnded()) {
                int dimension = getGame().getDimension();
                List<MSTile> tiles = getTiles();
                if (tiles != null && !tiles.isEmpty()) {
                    List<Pair<Integer, Integer>> coordinatesOfAdditionalNewBlankTiles = new ArrayList<>();
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
                                            adjacentTile.saveChanges(dbHelper);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!coordinatesOfAdditionalNewBlankTiles.isEmpty()) {
                        revealBlankTiles(dbHelper, coordinatesOfAdditionalNewBlankTiles);
                    }
                }
            }
        }
    }

    public void exploreTile(MSDatabaseHelper dbHelper, int rowIndexMove, int colIndexMove) {
        MSGame game = getGame();
        if (game != null) {
            if (!game.getHasEnded()) {
                if (!game.getHasStarted()) {
                    resetGame(dbHelper, rowIndexMove, colIndexMove);
                }
                List<MSTile> tiles = getTiles();
                if (tiles != null && !tiles.isEmpty()) {
                    MSTile tile = tiles.get(rowIndexMove * game.getDimension() + colIndexMove);
                    if (tile != null) {
                        tile.setIsExplored(true);
                        tile.saveChanges(dbHelper);
                        if (tile.getHasMine()) {
                            game.setHasEnded(true);
                            game.setHasWon(false);
                            game.saveChanges(dbHelper);
                        } else if (tile.getAdjacentMines() == 0) {
                            List<Pair<Integer, Integer>> coordinatesOfNewBlankTiles = new ArrayList<>();
                            coordinatesOfNewBlankTiles.add(new Pair<>(rowIndexMove, colIndexMove));
                            revealBlankTiles(dbHelper, coordinatesOfNewBlankTiles);
                        }
                    }
                }
            }
        }
    }

    public void flagTile(MSDatabaseHelper dbHelper, int rowIndexMove, int colIndexMove, boolean bFlag) {
        MSGame game = getGame();
        if (game != null && !game.getHasEnded() && game.getEnableFlagMode()) {
            List<MSTile> tiles = getTiles();
            if (tiles != null && !tiles.isEmpty()) {
                MSTile tile = tiles.get(rowIndexMove * game.getDimension() + colIndexMove);
                if (tile != null && !tile.getIsExplored()) {
                    tile.setIsFlagged(bFlag);
                    tile.saveChanges(dbHelper);
                }
            }
        }
    }

    public MSGame getGame() {
        return mGame;
    }

    public void setGame(MSGame game) {
        mGame = game;
    }

    public List<MSTile> getTiles() {
        return mTiles;
    }

    public void setTiles(List<MSTile> tiles) {
        mTiles = tiles;
    }

    @Override
    public HashMap<String, String> toDict() {
        HashMap<String, String> obj = super.toDict();
        obj.put(PARAM_KEY_GAME, getTiles() != null ? getTiles().toString() : null);
        obj.put(PARAM_KEY_TILES, getTiles() != null ? getTiles().toString() : null);
        return obj;
    }
}
