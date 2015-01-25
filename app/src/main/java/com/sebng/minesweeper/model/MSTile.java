package com.sebng.minesweeper.model;

import java.util.HashMap;

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
