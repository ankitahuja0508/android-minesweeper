package com.sebng.minesweeper.model;

import java.util.HashMap;

public class MSObject {
    public final static String PARAM_KEY_ID = "id";

    public MSObject() {
    }

    public HashMap<String, String> toDict() {
        return new HashMap<String, String>();
    }

    @Override
    public String toString() {
        HashMap<String, String> dict = toDict();
        return dict != null ? toDict().toString() : null;
    }
}
