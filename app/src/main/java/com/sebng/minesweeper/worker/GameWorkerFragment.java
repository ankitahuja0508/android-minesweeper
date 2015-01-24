package com.sebng.minesweeper.worker;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.helper.MSDatabaseHelper;
import com.sebng.minesweeper.model.MSCell;
import com.sebng.minesweeper.model.MSGameState;
import com.sebng.minesweeper.model.MSGame;

import java.util.List;

public class GameWorkerFragment extends Fragment {
    public static final String FRAGMENT_TAG = "fragment_tag.GameWorkerFragment";
    protected static final String ARG_DIMENSION = "arg.DIMENSION";
    protected static final String ARG_MINES = "arg.MINES";
    protected static final String ARG_LOAD_GAME = "arg.LOAD_GAME";
    protected OnWorkerFragmentCallbacks mCallbacks = null;
    protected MSGenerateGameDataTask mGenerateGameDataTask = null;
    protected MSExploreCellTask mExploreCellTask = null;

    /**
     * Returns a new instance of this fragment.
     *
     * @return A new instance of fragment GameWorkerFragment.
     */
    public static GameWorkerFragment newInstance(int dimension, int mines, boolean loadGame) {
        GameWorkerFragment fragment = new GameWorkerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DIMENSION, dimension);
        args.putInt(ARG_MINES, mines);
        args.putBoolean(ARG_LOAD_GAME, loadGame);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        if (getArguments() != null) {
            boolean loadGame = getArguments().getBoolean(ARG_LOAD_GAME, false);
            if (!loadGame) {
                int dimension = getArguments().getInt(ARG_DIMENSION, getResources().getInteger(R.integer.ms_default_dimension));
                int mines = getArguments().getInt(ARG_MINES, getResources().getInteger(R.integer.ms_default_mines));
                createNewGameAsync(dimension, mines);
            }
        }
    }

    public MSGame createNewGame(int dimension, int mines) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        databaseHelper.deleteAllCells();
        return databaseHelper.createNewGame(dimension, mines);
    }

    public void createNewGameAsync(int dimension, int mines) {
        if (mGenerateGameDataTask != null)
            return;

        mGenerateGameDataTask = new MSGenerateGameDataTask();
        Object[] params = {
                dimension,
                mines
        };
        mGenerateGameDataTask.execute(params);
    }

    public MSGameState exploreCell(int rowIndex, int colIndex) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = databaseHelper.loadGame();
        if (!game.getHasStarted()) {
            return databaseHelper.resetCells(game.getDimension(), game.getMines(), rowIndex, colIndex);
        } else {
            return databaseHelper.exploreCell(new MSGameState(game, databaseHelper.loadCells()), rowIndex, colIndex);
        }
    }

    public void exploreCellAsync(int rowIndex, int colIndex) {
        if (mExploreCellTask != null)
            return;

        mExploreCellTask = new MSExploreCellTask();
        Object[] params = {
                rowIndex,
                colIndex
        };
        mExploreCellTask.execute(params);
    }

    @Override
    public void onDestroy() {
        cancelAsyncTasks();

        super.onDestroy();
    }

    public void cancelAsyncTasks() {
        cancelGenerateGameData();
    }

    public void cancelGenerateGameData() {
        if (mGenerateGameDataTask != null &&
                !mGenerateGameDataTask.isCancelled()) {
            mGenerateGameDataTask.cancel(true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (OnWorkerFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement GameWorkerFragment.OnWorkerFragmentCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public MSGame getGame() {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        return databaseHelper != null ? databaseHelper.loadGame() : null;
    }

    public int getDimension() {
        MSGame game = getGame();
        return game != null ? game.getDimension() : 0;
    }

    public int getMines() {
        MSGame game = getGame();
        return game != null ? game.getMines() : 0;
    }

    public static interface OnWorkerFragmentCallbacks {
        void onGenerateGameDataPreExecute();

        void onGenerateGameDataCancelled();

        void onGenerateGameDataPostExecute(MSGame result);

        void onExploreCellPreExecute();

        void onExploreCellCancelled();

        void onExploreCellPostExecute(MSGameState result);
    }

    public class MSGenerateGameDataTask extends AsyncTask<Object, Void, MSGame> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataPreExecute();
        }

        @Override
        protected MSGame doInBackground(Object... params) {
            if (params != null && params.length == 2) {
                Integer dimension = (Integer) params[0];
                Integer mines = (Integer) params[1];
                if (dimension != null && dimension != 0 && mines != null && mines != 0) {
                    return createNewGame(dimension, mines);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(MSGame result) {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataPostExecute(result);
            mGenerateGameDataTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataCancelled();
            mGenerateGameDataTask = null;
        }
    }

    public class MSExploreCellTask extends AsyncTask<Object, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onExploreCellPreExecute();
        }

        @Override
        protected MSGameState doInBackground(Object... params) {
            if (params != null && params.length == 2) {
                Integer rowIndex = (Integer) params[0];
                Integer colIndex = (Integer) params[1];
                if (rowIndex != null && colIndex != null) {
                    return exploreCell(rowIndex, colIndex);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(MSGameState result) {
            if (mCallbacks != null) mCallbacks.onExploreCellPostExecute(result);
            mExploreCellTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onExploreCellCancelled();
            mExploreCellTask = null;
        }
    }
}
