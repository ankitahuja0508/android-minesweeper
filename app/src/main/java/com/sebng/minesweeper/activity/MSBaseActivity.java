package com.sebng.minesweeper.activity;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.sebng.minesweeper.R;

public class MSBaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.global__action_about:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showTutorial() {
        //TODO: show tutorial in modal activity
    }
}
