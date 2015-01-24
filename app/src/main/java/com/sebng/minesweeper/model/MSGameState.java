package com.sebng.minesweeper.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MSGameState extends MSObject {
    public final static String PARAM_KEY_GAME = "game";
    public final static String PARAM_KEY_CELLS = "cells";
    private MSGame mGame = null;
    private List<MSCell> mCells = null;

    public MSGameState() {
        setCells(new ArrayList<MSCell>());
    }

    public MSGameState(MSGame game, List<MSCell> cell) {
        super();

        setGame(game);
        setCells(cell);
    }

    public MSGame getGame() {
        return mGame;
    }

    public void setGame(MSGame game) {
        mGame = game;
    }

    public List<MSCell> getCells() {
        return mCells;
    }

    public void setCells(List<MSCell> cells) {
        mCells = cells;
    }

    @Override
    public HashMap<String, String> toDict() {
        HashMap<String, String> obj = super.toDict();
        obj.put(PARAM_KEY_GAME, getCells() != null ? getCells().toString() : null);
        obj.put(PARAM_KEY_CELLS, getCells() != null ? getCells().toString() : null);
        return obj;
    }
}
