package com.sebng.minesweeper.model;

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

    private Integer mDimension = null;
    private Integer mMines = null;
    private Boolean mHasStarted = null;
    private Boolean mHasEnded = null;
    private Boolean mHasWon = null;

    public MSGame(Integer dimension,
                  Integer mines,
                  Boolean hasStarted,
                  Boolean hasEnded,
                  Boolean hasWon) {
        super();

        setDimension(dimension);
        setMines(mines);
        setHasStarted(hasStarted);
        setHasEnded(hasEnded);
        setHasWon(hasWon);
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

    @Override
    public HashMap<String, String> toDict() {
        HashMap<String, String> obj = super.toDict();
        obj.put(PARAM_KEY_DIMENSION, getDimension() != null ? getDimension().toString() : null);
        obj.put(PARAM_KEY_MINES, getMines() != null ? getMines().toString() : null);
        obj.put(PARAM_KEY_HAS_STARTED, getHasStarted() != null ? getHasStarted().toString() : null);
        obj.put(PARAM_KEY_HAS_ENDED, getHasEnded() != null ? getHasEnded().toString() : null);
        obj.put(PARAM_KEY_HAS_WON, getHasWon() != null ? getHasWon().toString() : null);
        return obj;
    }
}
