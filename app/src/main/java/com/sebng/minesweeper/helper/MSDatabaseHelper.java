package com.sebng.minesweeper.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sebng.minesweeper.BuildConfig;
import com.sebng.minesweeper.MSApplication;
import com.sebng.minesweeper.model.MSTile;
import com.sebng.minesweeper.model.MSGame;

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
        MSGame.createTable(db);
        MSTile.createTable(db);
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
