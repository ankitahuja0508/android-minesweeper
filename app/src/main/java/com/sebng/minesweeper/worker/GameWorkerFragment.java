package com.sebng.minesweeper.worker;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.helper.MSDatabaseHelper;
import com.sebng.minesweeper.model.MSGame;
import com.sebng.minesweeper.model.MSGameState;

public class GameWorkerFragment extends Fragment {
    public static final String FRAGMENT_TAG = "fragment_tag.GameWorkerFragment";
    protected static final String ARG_DIMENSION = "arg.DIMENSION";
    protected static final String ARG_MINES = "arg.MINES";
    protected static final String ARG_LOAD_GAME = "arg.LOAD_GAME";
    protected OnWorkerFragmentCallbacks mCallbacks = null;
    protected MSGenerateGameDataTask mGenerateGameDataTask = null;
    protected MSExploreTileTask mExploreTileTask = null;
    protected MSFlagTileTask mFlagTileTask = null;
    protected MSValidateGameTask mValidateGameTask = null;
    protected MSToggleCheatTask mToggleCheatTask = null;
    protected MSToggleFlagModeTask mToggleFlagModeTask = null;

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

    public MSGameState createNewGame(int dimension, int mines) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        databaseHelper.deleteAllTiles();
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

    public MSGameState exploreTile(int rowIndex, int colIndex) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = databaseHelper.loadGame();
        if (!game.getHasStarted()) {
            return databaseHelper.resetTiles(game.getDimension(), game.getMines(), rowIndex, colIndex);
        } else {
            return databaseHelper.exploreTile(new MSGameState(game, databaseHelper.loadTiles()), rowIndex, colIndex);
        }
    }

    public MSGameState flagTile(int rowIndex, int colIndex, boolean bFlag) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = databaseHelper.loadGame();
        return databaseHelper.flagTile(new MSGameState(game, databaseHelper.loadTiles()), rowIndex, colIndex, bFlag);
    }

    public void exploreTileAsync(int rowIndex, int colIndex) {
        if (mExploreTileTask != null)
            return;

        mExploreTileTask = new MSExploreTileTask();
        Object[] params = {
                rowIndex,
                colIndex
        };
        mExploreTileTask.execute(params);
    }

    public void flagTileAsync(int rowIndex, int colIndex, boolean bFlag) {
        if (mFlagTileTask != null)
            return;

        mFlagTileTask = new MSFlagTileTask();
        Object[] params = {
                rowIndex,
                colIndex,
                bFlag
        };
        mFlagTileTask.execute(params);
    }

    public MSGameState validateGame() {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = databaseHelper.loadGame();
        MSGameState gameState = new MSGameState(game, databaseHelper.loadTiles());
        if (game.getHasEnded()) {
            return gameState;
        } else {
            return databaseHelper.validateGame(gameState);
        }
    }

    public void validateGameAsync() {
        if (mValidateGameTask != null)
            return;

        mValidateGameTask = new MSValidateGameTask();
        mValidateGameTask.execute();
    }

    public void toggleCheatAsync(boolean bEnable) {
        if (mToggleCheatTask != null)
            return;

        mToggleCheatTask = new MSToggleCheatTask();
        Object[] params = {
                bEnable
        };
        mToggleCheatTask.execute(params);
    }

    public void toggleFlagModeAsync(boolean bEnable) {
        if (mToggleFlagModeTask != null)
            return;

        mToggleFlagModeTask = new MSToggleFlagModeTask();
        Object[] params = {
                bEnable
        };
        mToggleFlagModeTask.execute(params);
    }

    @Override
    public void onDestroy() {
        cancelAsyncTasks();

        super.onDestroy();
    }

    public void cancelAsyncTasks() {
        cancelGenerateGameData();
        cancelExploreTile();
        cancelFlagTile();
        cancelValidateGame();
        cancelToggleCheat();
        cancelToggleFlagMode();
    }

    public void cancelGenerateGameData() {
        if (mGenerateGameDataTask != null &&
                !mGenerateGameDataTask.isCancelled()) {
            mGenerateGameDataTask.cancel(true);
        }
    }

    public void cancelExploreTile() {
        if (mExploreTileTask != null &&
                !mExploreTileTask.isCancelled()) {
            mExploreTileTask.cancel(true);
        }
    }

    public void cancelFlagTile() {
        if (mFlagTileTask != null &&
                !mFlagTileTask.isCancelled()) {
            mFlagTileTask.cancel(true);
        }
    }

    public void cancelValidateGame() {
        if (mValidateGameTask != null &&
                !mValidateGameTask.isCancelled()) {
            mValidateGameTask.cancel(true);
        }
    }

    public void cancelToggleCheat() {
        if (mToggleCheatTask != null &&
                !mToggleCheatTask.isCancelled()) {
            mToggleCheatTask.cancel(true);
        }
    }

    public void cancelToggleFlagMode() {
        if (mToggleFlagModeTask != null &&
                !mToggleFlagModeTask.isCancelled()) {
            mToggleFlagModeTask.cancel(true);
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

        void onGenerateGameDataPostExecute(MSGameState result);

        void onExploreTilePreExecute();

        void onExploreTileCancelled();

        void onExploreTilePostExecute(MSGameState result);

        void onFlagTilePreExecute();

        void onFlagTileCancelled();

        void onFlagTilePostExecute(MSGameState result);

        void onValidateGamePreExecute();

        void onValidateGameCancelled();

        void onValidateGamePostExecute(MSGameState result);

        void onToggleCheatPreExecute();

        void onToggleCheatCancelled();

        void onToggleCheatPostExecute(MSGameState result);

        void onToggleFlagModePreExecute();

        void onToggleFlagModeCancelled();

        void onToggleFlagModePostExecute(MSGame result);
    }

    public class MSGenerateGameDataTask extends AsyncTask<Object, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataPreExecute();
        }

        @Override
        protected MSGameState doInBackground(Object... params) {
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
        protected void onPostExecute(MSGameState result) {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataPostExecute(result);
            mGenerateGameDataTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataCancelled();
            mGenerateGameDataTask = null;
        }
    }

    public class MSExploreTileTask extends AsyncTask<Object, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onExploreTilePreExecute();
        }

        @Override
        protected MSGameState doInBackground(Object... params) {
            if (params != null && params.length == 2) {
                Integer rowIndex = (Integer) params[0];
                Integer colIndex = (Integer) params[1];
                if (rowIndex != null && colIndex != null) {
                    return exploreTile(rowIndex, colIndex);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(MSGameState result) {
            if (mCallbacks != null) mCallbacks.onExploreTilePostExecute(result);
            mExploreTileTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onExploreTileCancelled();
            mExploreTileTask = null;
        }
    }

    public class MSFlagTileTask extends AsyncTask<Object, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onFlagTilePreExecute();
        }

        @Override
        protected MSGameState doInBackground(Object... params) {
            if (params != null && params.length == 3) {
                Integer rowIndex = (Integer) params[0];
                Integer colIndex = (Integer) params[1];
                Boolean bFlag = (Boolean) params[2];
                if (rowIndex != null && colIndex != null) {
                    return flagTile(rowIndex, colIndex, bFlag);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(MSGameState result) {
            if (mCallbacks != null) mCallbacks.onFlagTilePostExecute(result);
            mFlagTileTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onFlagTileCancelled();
            mFlagTileTask = null;
        }
    }

    public class MSValidateGameTask extends AsyncTask<Void, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onValidateGamePreExecute();
        }

        @Override
        protected MSGameState doInBackground(Void... params) {
            return validateGame();
        }

        @Override
        protected void onPostExecute(MSGameState result) {
            if (mCallbacks != null) mCallbacks.onValidateGamePostExecute(result);
            mValidateGameTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onValidateGameCancelled();
            mValidateGameTask = null;
        }
    }

    public class MSToggleCheatTask extends AsyncTask<Object, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onToggleCheatPreExecute();
        }

        @Override
        protected MSGameState doInBackground(Object... params) {
            MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
            MSGame game = getGame();
            if (params != null && params.length == 1) {
                Boolean bEnable = (Boolean) params[0];
                game.setEnableCheat(bEnable);
                databaseHelper.updateGame(game);
            }
            return new MSGameState(game, databaseHelper.loadTiles());
        }

        @Override
        protected void onPostExecute(MSGameState result) {
            if (mCallbacks != null) mCallbacks.onToggleCheatPostExecute(result);
            mToggleCheatTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onToggleCheatCancelled();
            mToggleCheatTask = null;
        }
    }

    public class MSToggleFlagModeTask extends AsyncTask<Object, Void, MSGame> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onToggleFlagModePreExecute();
        }

        @Override
        protected MSGame doInBackground(Object... params) {
            MSGame game = getGame();
            if (params != null && params.length == 1) {
                Boolean bEnable = (Boolean) params[0];
                game.setEnableFlagMode(bEnable);
                MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
                databaseHelper.updateGame(game);
            }
            return game;
        }

        @Override
        protected void onPostExecute(MSGame result) {
            if (mCallbacks != null) mCallbacks.onToggleFlagModePostExecute(result);
            mToggleFlagModeTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onToggleFlagModeCancelled();
            mToggleFlagModeTask = null;
        }
    }
}
