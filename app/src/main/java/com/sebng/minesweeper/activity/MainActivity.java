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
            case R.id.main__action_help:
                showTutorial();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onGameSettingsDialogPositiveClick(int dimension, int mines) {
        Intent intent = new Intent(this, GameActivity.class);
        Bundle extras = new Bundle();
        extras.putInt(GameActivity.EXTRA_DIMENSION, dimension);
        extras.putInt(GameActivity.EXTRA_MINES, mines);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
