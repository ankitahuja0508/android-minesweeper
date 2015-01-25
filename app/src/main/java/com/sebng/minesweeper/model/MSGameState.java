package com.sebng.minesweeper.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MSGameState extends MSObject {
    public final static String PARAM_KEY_GAME = "game";
    public final static String PARAM_KEY_TILES = "tiles";
    private MSGame mGame = null;
    private List<MSTile> mTiles = null;

    public MSGameState() {
        setTiles(new ArrayList<MSTile>());
    }

    public MSGameState(MSGame game, List<MSTile> tile) {
        super();

        setGame(game);
        setTiles(tile);
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
