package com.sebng.minesweeper.worker;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sebng.minesweeper.R;

public class GameWorkerFragment extends Fragment {
    public static final String FRAGMENT_TAG = "fragment_tag.GameWorkerFragment";
    protected static final String ARG_DIMENSION = "arg.DIMENSION";
    protected static final String ARG_MINES = "arg.MINES";
    protected OnWorkerFragmentCallbacks mCallbacks;
    protected MSGenerateGameDataTask mGenerateGameDataTask;
    protected int mDimension;
    protected int mMines;

    /**
     * Returns a new instance of this fragment.
     *
     * @param dimension Dimension of board.
     * @param mines     Number of mines in the board.
     * @return A new instance of fragment GameWorkerFragment.
     */
    public static GameWorkerFragment newInstance(int dimension, int mines) {
        GameWorkerFragment fragment = new GameWorkerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DIMENSION, dimension);
        args.putInt(ARG_MINES, mines);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        if (getArguments() != null) {
            setDimension(getArguments().getInt(ARG_DIMENSION, getResources().getInteger(R.integer.ms_default_dimension)));
            setMines(getArguments().getInt(ARG_MINES, getResources().getInteger(R.integer.ms_default_mines)));
        }
    }

    @Override
    public void onDestroy() {
        cancelAsyncTasks();

        super.onDestroy();
    }

    public void cancelAsyncTasks() {
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

    public int getDimension() {
        return mDimension;
    }

    public void setDimension(int dimension) {
        mDimension = dimension;
    }

    public int getMines() {
        return mMines;
    }

    public void setMines(int mines) {
        mMines = mines;
    }


    public static interface OnWorkerFragmentCallbacks {
        void onGenerateGameDataPreExecute();

        void onGenerateGameDataCancelled();

        void onGenerateGameDataPostExecute(Void result);
    }

    public class MSGenerateGameDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        public void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataPostExecute(result);
            mGenerateGameDataTask = null;
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onGenerateGameDataCancelled();
            mGenerateGameDataTask = null;
        }
    }
}
