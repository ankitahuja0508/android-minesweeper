package com.sebng.minesweeper.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
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
    public static final int INDEX_MENU_FLAG_MODE = 2;
    public static final int INDEX_MENU_CHEAT = 3;
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
                mines = resources.getInteger(R.integer.ms_default_mines);
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
        boolean outcome = super.onCreateOptionsMenu(menu);
        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
        if (mWorkerFragment != null) {
            MSGame game = mWorkerFragment.getGame();
            menu.getItem(INDEX_MENU_FLAG_MODE).setChecked(game.getEnableFlagMode());
            menu.getItem(INDEX_MENU_CHEAT).setChecked(game.getEnableCheat());
        }
        return outcome;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.game__action_validate:
                if (workerFragment != null) {
                    MSGame game = workerFragment.getGame();
                    if (game != null) {
                        if (game.getHasEnded()) {
                            Toast.makeText(this, getString(game.getHasWon() ? R.string.game__ended_and_won : R.string.game__ended_and_lost), Toast.LENGTH_SHORT).show();
                        } else if (game.getHasStarted()) {
                            new AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.game__validate_confirmation_dialog___title))
                                    .setMessage(getString(R.string.game__validate_confirmation_dialog___prompt))
                                    .setPositiveButton(getString(R.string.game__validate_confirmation_dialog___button_positive), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FragmentManager fm = getFragmentManager();
                                            GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
                                            if (mWorkerFragment != null) {
                                                workerFragment.validateGameAsync();
                                            }
                                        }

                                    })
                                    .setNegativeButton(getString(R.string.dialog___button_cancel), null)
                                    .show();
                        } else {
                            Toast.makeText(this, getString(R.string.game__validate_disallowed_first_move_required), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                return true;
            case R.id.game__action_restart:
                if (workerFragment != null) {
                    MSGame game = workerFragment.getGame();
                    if (game != null) {
                        if (game.getHasStarted()) {
                            if (game.getHasEnded()) {
                                workerFragment.createNewGameAsync(game.getDimension(), game.getMines());
                            } else {
                                new AlertDialog.Builder(this)
                                        .setTitle(getString(R.string.game__restart_confirmation_dialog___title))
                                        .setMessage(getString(R.string.game__restart_confirmation_dialog___prompt))
                                        .setPositiveButton(getString(R.string.game__restart_confirmation_dialog___button_positive), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FragmentManager fm = getFragmentManager();
                                                GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
                                                if (mWorkerFragment != null) {
                                                    MSGame game = workerFragment.getGame();
                                                    if (game != null) {
                                                        workerFragment.createNewGameAsync(game.getDimension(), game.getMines());
                                                    }
                                                }
                                            }

                                        })
                                        .setNegativeButton(getString(R.string.dialog___button_cancel), null)
                                        .show();
                            }
                        }
                    }
                }
                return true;
            case R.id.game__action_flag_mode:
                item.setChecked(!item.isChecked());
                if (workerFragment != null) {
                    MSGame game = workerFragment.getGame();
                    if (game != null) {
                        workerFragment.toggleFlagModeAsync(!game.getEnableFlagMode());
                    }
                }
                return true;
            case R.id.game__action_hint:
                return true;
            case R.id.game__action_cheat:
                item.setChecked(!item.isChecked());
                if (workerFragment != null) {
                    MSGame game = workerFragment.getGame();
                    if (game != null) {
                        workerFragment.toggleCheatAsync(!game.getEnableCheat());
                        if (!game.getHasStarted()) {
                            Toast.makeText(this, getString(R.string.game__cheat_shown_only_after_first_move), Toast.LENGTH_LONG).show();
                        }
                    }
                }
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
    public void onGenerateGameDataPostExecute(MSGameState result) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (result != null) {
                MSGame game = result.getGame();
                if (game != null) {
                    updateActionBarTitle(actionBar, game.getDimension(), game.getMines());
                }
            }
        }
        invalidateOptionsMenu();

        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onGenerateGameDataPostExecute(result);
        }
    }

    @Override
    public void onExploreTilePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreTilePreExecute();
        }
    }

    @Override
    public void onExploreTileCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreTileCancelled();
        }
    }

    @Override
    public void onExploreTilePostExecute(MSGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreTilePostExecute(result);
        }
    }

    @Override
    public void onFlagTilePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onFlagTilePreExecute();
        }
    }

    @Override
    public void onFlagTileCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onFlagTileCancelled();
        }
    }

    @Override
    public void onFlagTilePostExecute(MSGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onFlagTilePostExecute(result);
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
    public void onToggleCheatPreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleCheatPreExecute();
        }
    }

    @Override
    public void onToggleCheatCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleCheatCancelled();
        }
    }

    @Override
    public void onToggleCheatPostExecute(MSGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleCheatPostExecute(result);
        }
    }

    @Override
    public void onToggleFlagModePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleFlagModePreExecute();
        }
    }

    @Override
    public void onToggleFlagModeCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleFlagModeCancelled();
        }
    }

    @Override
    public void onToggleFlagModePostExecute(MSGame result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleFlagModePostExecute(result);
        }
    }

    @Override
    public void onGameFragmentAttached(GameFragment gameFragment) {
        setGameFragment(gameFragment);
    }
}
