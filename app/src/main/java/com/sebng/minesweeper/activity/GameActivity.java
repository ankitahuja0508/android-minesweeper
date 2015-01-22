package com.sebng.minesweeper.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.fragment.GameFragment;

public class GameActivity extends MSBaseActivity
        implements GameFragment.OnFragmentInteractionListener {
    public static final String EXTRA_DIMENSION = "extra.DIMENSION";
    public static final String EXTRA_MINES = "extra.MINES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int dimension, mines;
        if (intent != null) {
            Resources resources = getResources();
            dimension = intent.getIntExtra(EXTRA_DIMENSION, resources.getInteger(R.integer.ms_default_dimension));
            mines = intent.getIntExtra(EXTRA_MINES, resources.getInteger(R.integer.ms_default_dimension));
        } else {
            Resources resources = getResources();
            dimension = resources.getInteger(R.integer.ms_default_dimension);
            mines = resources.getInteger(R.integer.ms_default_dimension);
        }

        // Display back button in actionbar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(String.format(getString(R.string.game__activity_title), dimension, dimension, mines));
        }

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, GameFragment.newInstance(dimension, mines))//TODO-TEMP
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.game__action_validate:
                //TODO: show confirmation prompt
                return true;
            case R.id.game__action_reset:
                //TODO: show confirmation prompt
                return true;
            case R.id.game__action_flag_mode:
                item.setChecked(!item.isChecked());
                return true;
            case R.id.game__action_hint:
                item.setChecked(!item.isChecked());
                return true;
            case R.id.game__action_cheat:
                item.setChecked(!item.isChecked());
                return true;
            case R.id.game__action_help:
                showTutorial();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
