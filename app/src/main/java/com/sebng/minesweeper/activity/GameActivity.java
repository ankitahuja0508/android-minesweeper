package com.sebng.minesweeper.activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.fragment.GameFragment;
import com.sebng.minesweeper.worker.GameWorkerFragment;

public class GameActivity extends MSBaseActivity
        implements GameFragment.OnFragmentInteractionListener,
        GameWorkerFragment.OnWorkerFragmentCallbacks {
    public static final String EXTRA_DIMENSION = "extra.DIMENSION";
    public static final String EXTRA_MINES = "extra.MINES";
    protected GameWorkerFragment mWorkerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        int dimension, mines;
        if (mWorkerFragment == null) {
            Intent intent = getIntent();
            if (intent != null) {
                Resources resources = getResources();
                dimension = intent.getIntExtra(EXTRA_DIMENSION, resources.getInteger(R.integer.ms_default_dimension));
                mines = intent.getIntExtra(EXTRA_MINES, resources.getInteger(R.integer.ms_default_dimension));
            } else {
                Resources resources = getResources();
                dimension = resources.getInteger(R.integer.ms_default_dimension);
                mines = resources.getInteger(R.integer.ms_default_dimension);
            }

            mWorkerFragment = GameWorkerFragment.newInstance(dimension, mines);
            fm.beginTransaction().add(mWorkerFragment, GameWorkerFragment.FRAGMENT_TAG).commit();
        } else {
            dimension = mWorkerFragment.getDimension();
            mines = mWorkerFragment.getMines();
        }

        // Display back button in actionbar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(String.format(getString(R.string.game__activity_title), dimension, dimension, mines));
        }

        fm.beginTransaction()
                .replace(android.R.id.content, GameFragment.newInstance())
                .commit();
    }

    public void setUp() {

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

    @Override
    public void onGenerateGameDataPreExecute() {

    }

    @Override
    public void onGenerateGameDataCancelled() {

    }

    @Override
    public void onGenerateGameDataPostExecute(Void result) {

    }
}
