package com.sebng.minesweeper.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.dialog.AboutDialogFragment;

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
            case R.id.global__action_about:
                AboutDialogFragment aboutDialogFragment = AboutDialogFragment.newInstance();
                aboutDialogFragment.show(getFragmentManager(), AboutDialogFragment.class.toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showTutorial() {
        //TODO: show tutorial in modal activity
    }

    public void onClickBtnAuthorWebsite(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.ms_author_website_link)));
        startActivity(intent);
    }
}
