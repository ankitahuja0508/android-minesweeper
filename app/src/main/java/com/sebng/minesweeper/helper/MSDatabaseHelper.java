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
        MSGame.createTableForGames(db);
        MSTile.createTableForGames(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(MSApplication.LOG_TAG, "Upgrading database, which will destroy all old data");
        MSGame.dropTableForGames(db);
        MSTile.dropTableForTiles(db);
        onCreate(db);
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }
}
