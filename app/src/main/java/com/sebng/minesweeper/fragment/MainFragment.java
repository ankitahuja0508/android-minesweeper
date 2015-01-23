package com.sebng.minesweeper.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.dialog.GameSettingsDialogFragment;
import com.sebng.minesweeper.helper.MSDatabaseHelper;
import com.sebng.minesweeper.model.MSGame;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // UI references.
    private Button mButtonNewGame;
    private Button mButtonContinue;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mButtonNewGame = (Button) rootView.findViewById(R.id.main__button_new_game);
        mButtonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
                MSGame game = databaseHelper.loadGame();
                int dimension, mines;
                if (game != null) {
                    dimension = game.getDimension();
                    mines = game.getMines();
                } else {
                    dimension = getResources().getInteger(R.integer.ms_default_dimension);
                    mines = getResources().getInteger(R.integer.ms_default_mines);
                }
                GameSettingsDialogFragment extendRideDialogFragment = GameSettingsDialogFragment.newInstance(dimension, mines);
                extendRideDialogFragment.show(getFragmentManager(), GameSettingsDialogFragment.class.toString());
            }
        });
        mButtonContinue = (Button) rootView.findViewById(R.id.main__button_continue);
        mButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRequestToContinueGame();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateViews();
    }

    public void updateViews() {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        MSGame game = databaseHelper.loadGame();
        mButtonContinue.setVisibility(game != null && !game.getHasEnded() ? View.VISIBLE : View.GONE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onRequestToContinueGame();
    }
}
