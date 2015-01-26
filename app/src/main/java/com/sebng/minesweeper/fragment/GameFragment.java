package com.sebng.minesweeper.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.sebng.minesweeper.R;
import com.sebng.minesweeper.helper.MSDatabaseHelper;
import com.sebng.minesweeper.model.MSTile;
import com.sebng.minesweeper.model.MSGameState;
import com.sebng.minesweeper.model.MSGame;
import com.sebng.minesweeper.worker.GameWorkerFragment;

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
    public static final String FRAGMENT_TAG = "fragment_tag.GameFragment";
    private ArrayAdapter<MSTile> mArrayAdapterForTiles;
    private List<MSTile> mTiles;
    private GridView mGridView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setNumColumns(getDimension());
        setArrayAdapterForTiles(new ArrayAdapter<MSTile>(getActivity(), 0) {
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
                return getTiles() != null ? getTiles().size() : 0;
            }

            @Override
            public MSTile getItem(int position) {
                return getTiles() != null ? getTiles().get(position) : null;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(getResources().getLayout(R.layout.grid_item_tiles), null);
                }

                GridItemViewHolder holder = (GridItemViewHolder) convertView.getTag();

                if (holder == null) {
                    holder = new GridItemViewHolder(convertView);
                    convertView.setTag(holder);
                }

                MSTile item = getItem(position);
                if (item != null) {
                    holder.setTileId(item.getId());
                    if (item.getIsExplored()) {
                        holder.getImageButtonMask().setVisibility(View.GONE);
                        holder.getImageButtonFlagged().setVisibility(View.GONE);
                    } else {
                        boolean isFlagged = item.getIsFlagged();
                        holder.getImageButtonFlagged().setVisibility(isFlagged ? View.VISIBLE : View.GONE);
                        holder.getImageButtonMask().setVisibility(isFlagged ? View.GONE : View.VISIBLE);

                        MSGame game = getGame();
                        boolean shouldReveal = game != null && (game.getEnableCheat() || (game.getHasEnded() && (item.getHasMine() || item.getIsFlagged())));
                        float alpha = shouldReveal ? 0.7f : 1;
                        holder.getImageButtonMask().setAlpha(alpha);
                        holder.getImageButtonFlagged().setAlpha(alpha);
                    }
                    int resId;
                    if (item.getHasMine()) {
                        resId = R.drawable.ic_tile_bomb;
                    } else {
                        switch (item.getAdjacentMines()) {
                            case 0: // start with 0 because it is most common
                                resId = R.drawable.ic_tile_blank;
                                break;
                            case 1:
                                resId = R.drawable.ic_tile_number_1;
                                break;
                            case 2:
                                resId = R.drawable.ic_tile_number_2;
                                break;
                            case 3:
                                resId = R.drawable.ic_tile_number_3;
                                break;
                            case 4:
                                resId = R.drawable.ic_tile_number_4;
                                break;
                            case 5:
                                resId = R.drawable.ic_tile_number_5;
                                break;
                            case 6:
                                resId = R.drawable.ic_tile_number_6;
                                break;
                            case 7:
                                resId = R.drawable.ic_tile_number_7;
                                break;
                            case 8:
                                resId = R.drawable.ic_tile_number_8;
                                break;
                            default:
                                resId = R.drawable.ic_tile_blank;
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
                return getTiles() == null || getTiles().isEmpty();
            }
        });
        mGridView.setAdapter(getArrayAdapterForTiles());
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mListener.onGameFragmentAttached(this);
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

    public void updateViews(MSGameState gameState) {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        if (workerFragment != null && !workerFragment.getIsCreatingNewGame()) {
            MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
            if (gameState == null) {
                gameState = new MSGameState(databaseHelper.loadGame(), databaseHelper.loadTiles());
            }
            MSGame game = gameState.getGame();
            if (game != null) {
                setTiles(gameState.getTiles());
                mGridView.setNumColumns(getDimension());
                getArrayAdapterForTiles().notifyDataSetChanged();

                if (game.getHasEnded() && mListener != null) {
                    mListener.onGameEnded(game);
                }
            }
        }
    }

    public void updateViews() {
        updateViews(null);
    }

    public void invokeMove(Integer tileId) {
        if (tileId != null) {
            List<MSTile> tiles = getTiles();
            int index = tileId - 1;
            if (index < tiles.size()) {
                MSTile tile = tiles.get(index);
                if (tile != null) {
                    FragmentManager fm = getFragmentManager();
                    GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

                    if (workerFragment != null) {
                        MSGame game = workerFragment.getGame();
                        if (!game.getHasEnded()) {
                            if (game.getEnableFlagMode()) {
                                workerFragment.flagTileAsync(tile.getRowIndex(), tile.getColIndex(), !tile.getIsFlagged());
                            } else {
                                workerFragment.exploreTileAsync(tile.getRowIndex(), tile.getColIndex());
                            }
                        } else if (mListener != null) {
                            mListener.onGameEnded(game);
                        }
                    }
                }
            }
        }
    }

    public MSGame getGame() {
        MSDatabaseHelper databaseHelper = MSDatabaseHelper.getInstance(getActivity());
        return databaseHelper.loadGame();
    }

    public int getDimension() {
        MSGame game = getGame();
        return game != null ? game.getDimension() : 0;
    }

    public ArrayAdapter<MSTile> getArrayAdapterForTiles() {
        return mArrayAdapterForTiles;
    }

    public void setArrayAdapterForTiles(ArrayAdapter<MSTile> arrayAdapterForTiles) {
        mArrayAdapterForTiles = arrayAdapterForTiles;
    }

    public List<MSTile> getTiles() {
        return mTiles;
    }

    public void setTiles(List<MSTile> tiles) {
        mTiles = tiles;
    }

    public void onCreateNewGamePreExecute() {
    }

    public void onCreateNewGameCancelled() {
    }

    public void onCreateNewGamePostExecute(MSGameState result) {
        updateViews(result);
    }

    public void onExploreTilePreExecute() {
    }

    public void onExploreTileCancelled() {
    }

    public void onExploreTilePostExecute(MSGameState result) {
        updateViews(result);
    }

    public void onValidateGamePreExecute() {
    }

    public void onValidateGameCancelled() {
    }

    public void onValidateGamePostExecute(MSGameState result) {
        updateViews(result);
    }

    public void onToggleCheatPreExecute() {
    }

    public void onToggleCheatCancelled() {
    }

    public void onToggleCheatPostExecute(MSGameState result) {
        updateViews(result);
    }

    public void onToggleFlagModePreExecute() {
    }

    public void onToggleFlagModeCancelled() {
    }

    public void onToggleFlagModePostExecute(MSGame result) {
    }

    public void onFlagTilePreExecute() {
    }

    public void onFlagTileCancelled() {
    }

    public void onFlagTilePostExecute(MSGameState result) {
        updateViews(result);
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
        public void onGameFragmentAttached(GameFragment gameFragment);

        public void onGameEnded(MSGame game);
    }

    private class GridItemViewHolder {
        private ImageView mImageViewUnderlying = null;
        private ImageButton mImageButtonMask = null;
        private ImageButton mImageButtonFlagged = null;
        private Integer mTileId = null;

        public GridItemViewHolder(View base) {
            setImageUnderlying((ImageView) base.findViewById(R.id.game__imageview_underlying));
            setImageButtonMask((ImageButton) base.findViewById(R.id.game__imagebutton_mask));
            setImageButtonFlagged((ImageButton) base.findViewById(R.id.game__imagebutton_flagged));
            getImageButtonMask().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invokeMove(getTileId());
                }
            });
            getImageButtonFlagged().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invokeMove(getTileId());
                }
            });
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

        public Integer getTileId() {
            return mTileId;
        }

        public void setTileId(Integer tileId) {
            mTileId = tileId;
        }
    }
}
