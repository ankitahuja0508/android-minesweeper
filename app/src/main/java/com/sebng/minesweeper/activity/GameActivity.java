package com.sebng.minesweeper.activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.fragment.GameFragment;
import com.sebng.minesweeper.model.MSGameState;
import com.sebng.minesweeper.model.MSGame;
import com.sebng.minesweeper.worker.GameWorkerFragment;

public class GameActivity extends MSBaseActivity
        implements GameFragment.OnFragmentInteractionListener,
        GameWorkerFragment.OnWorkerFragmentCallbacks {
    public static final String EXTRA_DIMENSION = "extra.DIMENSION";
    public static final String EXTRA_MINES = "extra.MINES";
    public static final String EXTRA_LOAD_GAME = "extra.LOAD_GAME";
    protected GameWorkerFragment mWorkerFragment;

    protected GameFragment mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        Resources resources = getResources();
        int dimension = 0, mines = 0;
        boolean loadGame = false;
        if (mWorkerFragment == null) {
            Intent intent = getIntent();
            if (intent != null) {
                dimension = intent.getIntExtra(EXTRA_DIMENSION, resources.getInteger(R.integer.ms_default_dimension));
                mines = intent.getIntExtra(EXTRA_MINES, resources.getInteger(R.integer.ms_default_dimension));
                loadGame = intent.getBooleanExtra(EXTRA_LOAD_GAME, false);
            } else {
                dimension = resources.getInteger(R.integer.ms_default_dimension);
                mines = resources.getInteger(R.integer.ms_default_dimension);
            }

            mWorkerFragment = GameWorkerFragment.newInstance(dimension, mines, loadGame);
            fm.beginTransaction().add(mWorkerFragment, GameWorkerFragment.FRAGMENT_TAG).commit();
        } else {
            dimension = mWorkerFragment.getDimension();
            mines = mWorkerFragment.getMines();
        }

        // Display back button in actionbar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            updateActionBarTitle(actionBar, dimension, mines);
        }

        fm.beginTransaction()
                .replace(android.R.id.content, GameFragment.newInstance())
                .commit();
    }

    public void updateActionBarTitle(ActionBar actionBar, int dimension, int mines) {
        if (actionBar != null) {
            actionBar.setTitle(String.format(getString(R.string.game__activity_title), dimension, dimension, mines));
        }
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
                FragmentManager fm = getFragmentManager();
                GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

                if (workerFragment != null) {
                    MSGame game = workerFragment.getGame();
                    if (game.getHasEnded()) {
                        Toast.makeText(this, getString(game.getHasWon() ? R.string.game__ended_and_won : R.string.game__ended_and_lost), Toast.LENGTH_SHORT).show();
                    } else {
                        workerFragment.validateGameAsync();
                    }
                }
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

    public GameFragment getGameFragment() {
        if (mGameFragment == null) {
            FragmentManager fm = getFragmentManager();
            GameFragment gameFragment = (GameFragment) fm.findFragmentByTag(GameFragment.FRAGMENT_TAG);
            if (gameFragment != null) {
                mGameFragment = gameFragment;
            }
        }
        return mGameFragment;
    }

    public void setGameFragment(GameFragment gameFragment) {
        mGameFragment = gameFragment;
    }

    @Override
    public void onGenerateGameDataPreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onGenerateGameDataPreExecute();
        }
    }

    @Override
    public void onGenerateGameDataCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onGenerateGameDataCancelled();
        }
    }

    @Override
    public void onGenerateGameDataPostExecute(MSGame result) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            updateActionBarTitle(actionBar, result.getDimension(), result.getMines());
        }

        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onGenerateGameDataPostExecute(result);
        }
    }

    @Override
    public void onExploreCellPreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreCellPreExecute();
        }
    }

    @Override
    public void onExploreCellCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreCellCancelled();
        }
    }

    @Override
    public void onExploreCellPostExecute(MSGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreCellPostExecute(result);
        }
    }

    @Override
    public void onValidateGamePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onValidateGamePreExecute();
        }
    }

    @Override
    public void onValidateGameCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onValidateGameCancelled();
        }
    }

    @Override
    public void onValidateGamePostExecute(MSGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onValidateGamePostExecute(result);
        }
    }

    @Override
    public void onGameFragmentAttached(GameFragment gameFragment) {
        setGameFragment(gameFragment);
    }
}
