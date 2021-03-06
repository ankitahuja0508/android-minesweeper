package com.sebng.minesweeper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.dialog.GameSettingsDialogFragment;
import com.sebng.minesweeper.fragment.MainFragment;
import com.sebng.minesweeper.helper.MSDatabaseHelper;
import com.sebng.minesweeper.model.MSGame;


public class MainActivity extends MSBaseActivity
        implements MainFragment.OnFragmentInteractionListener,
        GameSettingsDialogFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, MainFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.main__action_help:
//                showTutorial();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GameActivity.REQUEST_CODE_NEW_GAME &&
                resultCode == Activity.RESULT_OK) {
            showGameSettings();
        }
    }

    @Override
    public void onGameSettingsDialogPositiveClick(int dimension, int mines) {
        Intent intent = new Intent(this, GameActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(GameActivity.EXTRA_DIMENSION, dimension);
        extras.putInt(GameActivity.EXTRA_MINES, mines);
        extras.putBoolean(GameActivity.EXTRA_LOAD_GAME, false);
        intent.putExtras(extras);

        startActivityForResult(intent, GameActivity.REQUEST_CODE_NEW_GAME);
    }

    @Override
    public void onRequestToCreateNewGame() {
        showGameSettings();
    }

    @Override
    public void onRequestToContinueGame() {
        Intent intent = new Intent(this, GameActivity.class);
        Bundle extras = new Bundle();
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(this);
        MSGame game = MSGame.loadGame(databaseHelper);
        if (game != null) {
            extras.putInt(GameActivity.EXTRA_DIMENSION, game.getDimension());
            extras.putInt(GameActivity.EXTRA_MINES, game.getMines());
            extras.putBoolean(GameActivity.EXTRA_LOAD_GAME, true);
        } else {
            extras.putInt(GameActivity.EXTRA_DIMENSION, getResources().getInteger(R.integer.ms_default_dimension));
            extras.putInt(GameActivity.EXTRA_MINES, getResources().getInteger(R.integer.ms_default_mines));
            extras.putBoolean(GameActivity.EXTRA_LOAD_GAME, false);
        }
        intent.putExtras(extras);

        startActivityForResult(intent, GameActivity.REQUEST_CODE_NEW_GAME);
    }

    public void showGameSettings() {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(this);
        MSGame game = MSGame.loadGame(databaseHelper);
        int dimension, mines;
        if (game != null) {
            dimension = game.getDimension();
            mines = game.getMines();
        } else {
            dimension = getResources().getInteger(R.integer.ms_default_dimension);
            mines = getResources().getInteger(R.integer.ms_default_mines);
        }
        GameSettingsDialogFragment gameSettingsDialogFragment = GameSettingsDialogFragment.newInstance(dimension, mines);
        gameSettingsDialogFragment.show(getFragmentManager(), GameSettingsDialogFragment.class.toString());
    }
}
