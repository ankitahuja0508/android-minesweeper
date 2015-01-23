package com.sebng.minesweeper.model;

import java.util.HashMap;

public class MSCell extends MSObject {
    public final static String DB_TABLE_NAME = "cells";
    public final static String PARAM_KEY_ROW_INDEX = "row_index"; // zero-based
    public final static String PARAM_KEY_COL_INDEX = "col_index"; // zero-based
    public final static String PARAM_KEY_LINEAR_INDEX = "linear_index"; // zero-based index if all rows were to be concatenated
    public final static String PARAM_KEY_IS_EXPLORED = "is_explored";
    public final static String PARAM_KEY_IS_FLAGGED = "is_flagged";
    public final static String PARAM_KEY_HAS_MINE = "has_mine";
    public final static String PARAM_KEY_ADJACENT_MINES = "adjacent_mines";

    private Integer mRowIndex = null;
    private Integer mColIndex = null;
    private Integer mLinearIndex = null;
    private Boolean mIsExplored = null;
    private Boolean mIsFlagged = null;
    private Boolean mHasMine = null;
    private Integer mAdjacentMines = null;

    public MSCell(Integer rowIndex,
                  Integer colIndex,
                  Integer linearIndex,
                  Boolean isExplored,
                  Boolean isFlagged,
                  Boolean hasMine,
                  Integer adjacentMines) {
        super();

        setRowIndex(rowIndex);
        setColIndex(colIndex);
        setLinearIndex(linearIndex);
        setIsExplored(isExplored);
        setIsFlagged(isFlagged);
        setHasMine(hasMine);
        setAdjacentMines(adjacentMines);
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

    public Integer getLinearIndex() {
        return mLinearIndex;
    }

    public void setLinearIndex(Integer linearIndex) {
        mLinearIndex = linearIndex;
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

    @Override
    public HashMap<String, String> toDict() {
        HashMap<String, String> obj = super.toDict();
        obj.put(PARAM_KEY_ROW_INDEX, getRowIndex() != null ? getRowIndex().toString() : null);
        obj.put(PARAM_KEY_COL_INDEX, getColIndex() != null ? getColIndex().toString() : null);
        obj.put(PARAM_KEY_LINEAR_INDEX, getLinearIndex() != null ? getLinearIndex().toString() : null);
        obj.put(PARAM_KEY_IS_EXPLORED, getIsExplored() != null ? getIsExplored().toString() : null);
        obj.put(PARAM_KEY_IS_FLAGGED, getIsFlagged() != null ? getIsFlagged().toString() : null);
        obj.put(PARAM_KEY_HAS_MINE, getHasMine() != null ? getHasMine().toString() : null);
        obj.put(PARAM_KEY_ADJACENT_MINES, getAdjacentMines() != null ? getAdjacentMines().toString() : null);
        return obj;
    }
}
