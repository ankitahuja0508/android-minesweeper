package com.sebng.minesweeper.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.worker.GameWorkerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    private static final String ARG_DIMENSION = "arg.DIMENSION";
    private static final String ARG_MINES = "arg.MINES";
    private ArrayAdapter<CellModel> mArrayAdapterForBoardCells;
    private List<CellModel> mGridItemsForBoardCells;

    private OnFragmentInteractionListener mListener;

    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment GameFragment.
     */
    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setDimension(getArguments().getInt(ARG_DIMENSION));
            setMines(getArguments().getInt(ARG_MINES));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setNumColumns(getDimension());
        setArrayAdapterForBoardCells(new ArrayAdapter<CellModel>(getActivity(), 0) {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public int getCount() {
                return getGridItemsForBoardCells() != null ? getGridItemsForBoardCells().size() : 0;
            }

            @Override
            public CellModel getItem(int position) {
                return getGridItemsForBoardCells() != null ? getGridItemsForBoardCells().get(position) : null;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(getResources().getLayout(R.layout.grid_item_board_cells), null);
                }

                GridItemViewHolder holder = (GridItemViewHolder) convertView.getTag();

                if (holder == null) {
                    holder = new GridItemViewHolder(convertView);
                    convertView.setTag(holder);
                }

                CellModel item = getItem(position);
                if (item != null) {
                    if (item.getIsHidden()) {
                        holder.getImageButtonMask().setVisibility(View.VISIBLE);
                        holder.getImageButtonFlagged().setVisibility(item.getIsFlagged() ? View.VISIBLE : View.GONE);
                    } else {
                        holder.getImageButtonMask().setVisibility(View.GONE);
                        holder.getImageButtonFlagged().setVisibility(View.GONE);
                    }
                    int resId;
                    if (item.getHasBomb()) {
                        resId = R.drawable.ic_cell_bomb;
                    } else {
                        switch (item.getAdjacentMines()) {
                            case 0: // start with 0 because it is most common
                                resId = R.drawable.ic_cell_blank;
                                break;
                            case 1:
                                resId = R.drawable.ic_cell_number_1;
                                break;
                            case 2:
                                resId = R.drawable.ic_cell_number_2;
                                break;
                            case 3:
                                resId = R.drawable.ic_cell_number_3;
                                break;
                            case 4:
                                resId = R.drawable.ic_cell_number_4;
                                break;
                            case 5:
                                resId = R.drawable.ic_cell_number_5;
                                break;
                            case 6:
                                resId = R.drawable.ic_cell_number_6;
                                break;
                            case 7:
                                resId = R.drawable.ic_cell_number_7;
                                break;
                            case 8:
                                resId = R.drawable.ic_cell_number_8;
                                break;
                            default:
                                resId = R.drawable.ic_cell_blank;
                                break;
                        }
                    }
                    holder.getImageUnderlying().setImageResource(resId);
                }

                return convertView;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return getGridItemsForBoardCells() == null || getGridItemsForBoardCells().isEmpty();
            }
        });
        gridView.setAdapter(getArrayAdapterForBoardCells());
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GameFragment.OnFragmentInteractionListener");
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
        List<CellModel> gridItems = new ArrayList<CellModel>();
        int dimension = getDimension();
        int totalCells = dimension * dimension;
        for (int i = 0; i < totalCells; i++) {
            gridItems.add(new CellModel(i == 2 ? 0 : i % 8 + 1, i % 3 == 0, i % 5 == 0, i == 5));
        }
        setGridItemsForBoardCells(gridItems);
    }

    public int getDimension() {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        if (workerFragment != null) {
            return workerFragment.getDimension();
        } else {
            return getResources().getInteger(R.integer.ms_default_dimension);
        }
    }

    public void setDimension(int dimension) {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        if (workerFragment != null) {
            workerFragment.setDimension(dimension);
        }
    }

    public int getMines() {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        if (workerFragment != null) {
            return workerFragment.getMines();
        } else {
            return getResources().getInteger(R.integer.ms_default_mines);
        }
    }

    public void setMines(int mines) {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        if (workerFragment != null) {
            workerFragment.setMines(mines);
        }
    }

    public ArrayAdapter<CellModel> getArrayAdapterForBoardCells() {
        return mArrayAdapterForBoardCells;
    }

    public void setArrayAdapterForBoardCells(ArrayAdapter<CellModel> arrayAdapterForBoardCells) {
        mArrayAdapterForBoardCells = arrayAdapterForBoardCells;
    }

    public List<CellModel> getGridItemsForBoardCells() {
        return mGridItemsForBoardCells;
    }

    public void setGridItemsForBoardCells(List<CellModel> listItemsForBoardCells) {
        mGridItemsForBoardCells = listItemsForBoardCells;
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
    }

    private class GridItemViewHolder {
        private ImageView mImageViewUnderlying = null;
        private ImageButton mImageButtonMask = null;
        private ImageButton mImageButtonFlagged = null;

        public GridItemViewHolder(View base) {
            setImageUnderlying((ImageView) base.findViewById(R.id.game__imageview_underlying));
            setImageButtonMask((ImageButton) base.findViewById(R.id.game__imagebutton_mask));
            setImageButtonFlagged((ImageButton) base.findViewById(R.id.game__imagebutton_flagged));
        }

        public ImageView getImageUnderlying() {
            return mImageViewUnderlying;
        }

        public void setImageUnderlying(ImageView imageViewUnderlying) {
            mImageViewUnderlying = imageViewUnderlying;
        }

        public ImageButton getImageButtonMask() {
            return mImageButtonMask;
        }

        public void setImageButtonMask(ImageButton imageButtonMask) {
            mImageButtonMask = imageButtonMask;
        }

        public ImageButton getImageButtonFlagged() {
            return mImageButtonFlagged;
        }

        public void setImageButtonFlagged(ImageButton imageButtonFlagged) {
            mImageButtonFlagged = imageButtonFlagged;
        }
    }

    public class CellModel {
        private Integer mAdjacentMines = null;
        private Boolean mHasBomb = null;
        private Boolean mIsHidden = null;
        private Boolean mIsFlagged = null;

        public CellModel(Integer adjacentMines, Boolean hasBomb, Boolean isHidden, Boolean isFlagged) {
            setAdjacentMines(adjacentMines);
            setHasBomb(hasBomb);
            setIsHidden(isHidden);
            setIsFlagged(isFlagged);
        }

        public Integer getAdjacentMines() {
            return mAdjacentMines;
        }

        public void setAdjacentMines(Integer adjacentMines) {
            mAdjacentMines = adjacentMines;
        }

        public Boolean getHasBomb() {
            return mHasBomb;
        }

        public void setHasBomb(Boolean hasBomb) {
            mHasBomb = hasBomb;
        }

        public Boolean getIsHidden() {
            return mIsHidden;
        }

        public void setIsHidden(Boolean isHidden) {
            mIsHidden = isHidden;
        }

        public Boolean getIsFlagged() {
            return mIsFlagged;
        }

        public void setIsFlagged(Boolean isFlagged) {
            mIsFlagged = isFlagged;
        }
    }
}
