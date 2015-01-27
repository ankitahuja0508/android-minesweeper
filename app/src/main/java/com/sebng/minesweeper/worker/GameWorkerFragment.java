package com.sebng.minesweeper.worker;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.helper.MSDatabaseHelper;
import com.sebng.minesweeper.model.MSGame;
import com.sebng.minesweeper.model.MSGameState;
import com.sebng.minesweeper.model.MSTile;

import java.util.List;
import java.util.Random;

public class GameWorkerFragment extends Fragment {
    public static final String FRAGMENT_TAG = "fragment_tag.GameWorkerFragment";
    protected static final String ARG_DIMENSION = "arg.DIMENSION";
    protected static final String ARG_MINES = "arg.MINES";
    protected static final String ARG_LOAD_GAME = "arg.LOAD_GAME";
    protected OnWorkerFragmentCallbacks mCallbacks = null;
    protected boolean mIsCreatingNewGame = false;
    protected MSCreateNewGameTask mCreateNewGameTask = null;
    protected MSExploreTileTask mExploreTileTask = null;
    protected MSFlagTileTask mFlagTileTask = null;
    protected MSValidateGameTask mValidateGameTask = null;
    protected MSToggleCheatTask mToggleCheatTask = null;
    protected MSToggleFlagModeTask mToggleFlagModeTask = null;
    protected MSProvideHintTask mProvideHintTask = null;

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

    public boolean getIsCreatingNewGame() {
        return mIsCreatingNewGame;
    }

    public void setIsCreatingNewGame(boolean isCreatingNewGame) {
        mIsCreatingNewGame = isCreatingNewGame;
    }

    public MSGameState createNewGame(int dimension, int mines) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        return MSGame.createNewGame(databaseHelper, dimension, mines);
    }

    public void createNewGameAsync(int dimension, int mines) {
        if (mCreateNewGameTask != null)
            return;

        mCreateNewGameTask = new MSCreateNewGameTask();
        Object[] params = {
                dimension,
                mines
        };
        mCreateNewGameTask.execute(params);
    }

    public MSGameState exploreTile(int rowIndex, int colIndex) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = MSGame.loadGame(databaseHelper);
        if (!game.getHasStarted()) {
            return MSTile.resetTiles(databaseHelper, game.getDimension(), game.getMines(), rowIndex, colIndex);
        } else {
            return MSTile.exploreTile(databaseHelper, new MSGameState(game, MSTile.loadTiles(databaseHelper)), rowIndex, colIndex);
        }
    }

    public MSGameState flagTile(int rowIndex, int colIndex, boolean bFlag) {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = MSGame.loadGame(databaseHelper);
        return MSTile.flagTile(databaseHelper, new MSGameState(game, MSTile.loadTiles(databaseHelper)), rowIndex, colIndex, bFlag);
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
        MSGame game = MSGame.loadGame(databaseHelper);
        if (game != null) {
            MSGameState gameState = new MSGameState(game, MSTile.loadTiles(databaseHelper));
            if (game.getHasEnded()) {
                return gameState;
            } else {
                return MSGame.validateGame(databaseHelper, gameState);
            }
        } else {
            return null;
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

    public MSGameState provideHint() {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = MSGame.loadGame(databaseHelper);
        if (game != null) {
            List<MSTile> unexploredMineFreeTiles = MSTile.loadUnexploredMineFreeTiles(databaseHelper);
            if (!unexploredMineFreeTiles.isEmpty()) {
                Random random = new Random();
                int indexMineFreeTile = random.nextInt(unexploredMineFreeTiles.size());
                MSTile mineFreeTile = unexploredMineFreeTiles.get(indexMineFreeTile);
                exploreTile(mineFreeTile.getRowIndex(), mineFreeTile.getColIndex());
            }
            return new MSGameState(game, MSTile.loadTiles(databaseHelper));
        } else {
            return null;
        }
    }

    public void provideHintAsync() {
        if (mProvideHintTask != null)
            return;

        mProvideHintTask = new MSProvideHintTask();
        mProvideHintTask.execute();
    }

    @Override
    public void onDestroy() {
        cancelAsyncTasks();

        super.onDestroy();
    }

    public void cancelAsyncTasks() {
        cancelCreateNewGameTask();
        cancelExploreTileTask();
        cancelFlagTileTask();
        cancelValidateGameTask();
        cancelToggleCheatTask();
        cancelToggleFlagModeTask();
        cancelProvideHintTask();
    }

    public void cancelCreateNewGameTask() {
        if (mCreateNewGameTask != null &&
                !mCreateNewGameTask.isCancelled()) {
            mCreateNewGameTask.cancel(true);
        }
    }

    public void cancelExploreTileTask() {
        if (mExploreTileTask != null &&
                !mExploreTileTask.isCancelled()) {
            mExploreTileTask.cancel(true);
        }
    }

    public void cancelFlagTileTask() {
        if (mFlagTileTask != null &&
                !mFlagTileTask.isCancelled()) {
            mFlagTileTask.cancel(true);
        }
    }

    public void cancelValidateGameTask() {
        if (mValidateGameTask != null &&
                !mValidateGameTask.isCancelled()) {
            mValidateGameTask.cancel(true);
        }
    }

    public void cancelToggleCheatTask() {
        if (mToggleCheatTask != null &&
                !mToggleCheatTask.isCancelled()) {
            mToggleCheatTask.cancel(true);
        }
    }

    public void cancelToggleFlagModeTask() {
        if (mToggleFlagModeTask != null &&
                !mToggleFlagModeTask.isCancelled()) {
            mToggleFlagModeTask.cancel(true);
        }
    }

    public void cancelProvideHintTask() {
        if (mProvideHintTask != null &&
                !mProvideHintTask.isCancelled()) {
            mProvideHintTask.cancel(true);
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
        return databaseHelper != null ? MSGame.loadGame(databaseHelper) : null;
    }

    public int getDimension() {
        MSGame game = getGame();
        return game != null ? game.getDimension() : 0;
    }

    public int getNumberOfUnexploredTiles() {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        return MSTile.getNumberOfUnexploredTiles(databaseHelper);
    }

    public int getMines() {
        MSGame game = getGame();
        return game != null ? game.getMines() : 0;
    }

    public static interface OnWorkerFragmentCallbacks {
        void onCreateNewGamePreExecute();

        void onCreateNewGameCancelled();

        void onCreateNewGamePostExecute(MSGameState result);

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

        void onProvideHintPreExecute();

        void onProvideHintCancelled();

        void onProvideHintPostExecute(MSGameState result);
    }

    public class MSCreateNewGameTask extends AsyncTask<Object, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            setIsCreatingNewGame(true);
            if (mCallbacks != null) mCallbacks.onCreateNewGamePreExecute();
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
            setIsCreatingNewGame(false);
            if (mCallbacks != null) mCallbacks.onCreateNewGamePostExecute(result);
            mCreateNewGameTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onCreateNewGameCancelled();
            mCreateNewGameTask = null;
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
                MSGame.updateGame(databaseHelper, game);
            }
            return new MSGameState(game, MSTile.loadTiles(databaseHelper));
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
                MSGame.updateGame(databaseHelper, game);
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

    public class MSProvideHintTask extends AsyncTask<Void, Void, MSGameState> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onProvideHintPreExecute();
        }

        @Override
        protected MSGameState doInBackground(Void... params) {
            return provideHint();
        }

        @Override
        protected void onPostExecute(MSGameState result) {
            if (mCallbacks != null) mCallbacks.onProvideHintPostExecute(result);
            mProvideHintTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onProvideHintCancelled();
            mProvideHintTask = null;
        }
    }
}
