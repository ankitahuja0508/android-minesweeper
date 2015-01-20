package com.sebng.minesweeper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.dialog.GameSettingsDialogFragment;
import com.sebng.minesweeper.fragment.MainFragment;


public class MainActivity extends Activity
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGameSettingsDialogPositiveClick(int dimension, int mines) {
        //TODO: invoke worker fragment
        Toast.makeText(this, String.format("Dimension: %d, Mines: %d", dimension, mines), Toast.LENGTH_SHORT).show();
    }
}
