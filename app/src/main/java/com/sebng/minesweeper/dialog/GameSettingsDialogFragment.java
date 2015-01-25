package com.sebng.minesweeper.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.sebng.minesweeper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameSettingsDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameSettingsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameSettingsDialogFragment extends DialogFragment {
    private static final String ARG_DIMENSION = "arg.DIMENSION";
    private static final String ARG_MINES = "arg.MINES";

    private Integer mInitialDimension;
    private Integer mInitialMines;
    private List<RowModel> mListItemsDimension;
    private List<RowModel> mListItemsMines;
    private ArrayAdapter<RowModel> mArrayAdapterDimension;
    private ArrayAdapter<RowModel> mArrayAdapterMines;
    // UI references.
    private Spinner mSpinnerDimension;
    private Spinner mSpinnerMines;

    private OnFragmentInteractionListener mListener;

    public GameSettingsDialogFragment() {
        // Required empty public constructor
    }

    public static GameSettingsDialogFragment newInstance(Integer dimension, Integer mines) {
        GameSettingsDialogFragment fragment = new GameSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DIMENSION, dimension);
        args.putInt(ARG_MINES, mines);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setInitialDimension(getArguments().getInt(ARG_DIMENSION, getResources().getInteger(R.integer.ms_default_dimension)));
            setInitialMines(getArguments().getInt(ARG_MINES, getResources().getInteger(R.integer.ms_default_mines)));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_game_settings, null);
        mSpinnerDimension = (Spinner) dialogView.findViewById(R.id.game_settings__dimension);
        mSpinnerMines = (Spinner) dialogView.findViewById(R.id.game_settings__mines);

        setDimensionArrayAdapter(new ArrayAdapter<RowModel>(getActivity(), 0) {
            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public int getCount() {
                return getListItemsDimension() != null ? getListItemsDimension().size() : 0;
            }

            @Override
            public RowModel getItem(int position) {
                return getListItemsDimension() != null ? getListItemsDimension().get(position) : null;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(getResources().getLayout(R.layout.spinner_item), null);
                }

                ListItemViewHolder holder = (ListItemViewHolder) convertView.getTag();

                if (holder == null) {
                    holder = new ListItemViewHolder(convertView);
                    convertView.setTag(holder);
                }

                RowModel item = getItem(position);
                holder.getTextViewLabel().setText(item.getLabel());

                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(getResources().getLayout(R.layout.spinner_dropdown_item), null);
                }

                ListItemViewHolder holder = (ListItemViewHolder) convertView.getTag();

                if (holder == null) {
                    holder = new ListItemViewHolder(convertView);
                    convertView.setTag(holder);
                }

                RowModel item = getItem(position);
                holder.getTextViewLabel().setText(item.getLabel());

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
                return getListItemsDimension() == null || getListItemsDimension().isEmpty();
            }
        });
        getDimensionArrayAdapter().setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDimension.setAdapter(getDimensionArrayAdapter());

        setMinesArrayAdapter(new ArrayAdapter<RowModel>(getActivity(), 0) {
            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public int getCount() {
                return getListItemsMines() != null ? getListItemsMines().size() : 0;
            }

            @Override
            public RowModel getItem(int position) {
                return getListItemsMines() != null ? getListItemsMines().get(position) : null;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(getResources().getLayout(R.layout.spinner_item), null);
                }

                ListItemViewHolder holder = (ListItemViewHolder) convertView.getTag();

                if (holder == null) {
                    holder = new ListItemViewHolder(convertView);
                    convertView.setTag(holder);
                }

                RowModel item = getItem(position);
                holder.getTextViewLabel().setText(item.getLabel());

                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(getResources().getLayout(R.layout.spinner_dropdown_item), null);
                }

                ListItemViewHolder holder = (ListItemViewHolder) convertView.getTag();

                if (holder == null) {
                    holder = new ListItemViewHolder(convertView);
                    convertView.setTag(holder);
                }

                RowModel item = getItem(position);
                holder.getTextViewLabel().setText(item.getLabel());

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
                return getListItemsDimension() == null || getListItemsDimension().isEmpty();
            }
        });
        getMinesArrayAdapter().setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMines.setAdapter(getMinesArrayAdapter());

        builder.setTitle(getString(R.string.game_settings__dialog_title))
                .setView(dialogView)
                .setPositiveButton(R.string.game_settings__button_start, null)
                .setNegativeButton(R.string.game_settings__button_cancel, null);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnSubmit = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RowModel selectedItemDimension = (RowModel) mSpinnerDimension.getSelectedItem();
                        RowModel selectedItemMines = (RowModel) mSpinnerMines.getSelectedItem();
                        if (mListener != null) {
                            mListener.onGameSettingsDialogPositiveClick(selectedItemDimension.getValue(), selectedItemMines.getValue());
                        }
                        alertDialog.dismiss();
                    }
                });
            }
        });

        updateViews();

        return alertDialog;
    }

    public void updateViews() {
        int initialDimension = getInitialDimension();
        int initialMines = getInitialMines();
        int selectedIndexDimension = 0;
        int selectedIndexMines = 0;

        List<RowModel> listItemsDimension = new ArrayList<>();
        for (int i = 6, j = 0; i <= 10; i += 2, j++) {
            listItemsDimension.add(new RowModel(i, String.format(getString(R.string.game_settings__option_dimension), i, i)));
            if (i == initialDimension) selectedIndexDimension = j;
        }
        setListItemsDimension(listItemsDimension);

        List<RowModel> listItemsMines = new ArrayList<>();
        for (int i = 5, j = 0; i < 20; i += 5, j++) {
            listItemsMines.add(new RowModel(i, String.format(getString(R.string.game_settings__option_mines), i)));
            if (i == initialMines) selectedIndexMines = j;
        }
        setMinesListItems(listItemsMines);

        getDimensionArrayAdapter().notifyDataSetChanged();
        getMinesArrayAdapter().notifyDataSetChanged();

        mSpinnerDimension.setSelection(selectedIndexDimension);
        mSpinnerMines.setSelection(selectedIndexMines);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GameSettingsDialogFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public Integer getInitialDimension() {
        return mInitialDimension;
    }

    public void setInitialDimension(Integer initialDimension) {
        mInitialDimension = initialDimension;
    }

    public Integer getInitialMines() {
        return mInitialMines;
    }

    public void setInitialMines(Integer initialMines) {
        mInitialMines = initialMines;
    }

    public List<RowModel> getListItemsDimension() {
        return mListItemsDimension;
    }

    public void setListItemsDimension(List<RowModel> listItemsDimension) {
        mListItemsDimension = listItemsDimension;
    }

    public List<RowModel> getListItemsMines() {
        return mListItemsMines;
    }

    public void setMinesListItems(List<RowModel> listItemsMines) {
        mListItemsMines = listItemsMines;
    }

    public ArrayAdapter<RowModel> getDimensionArrayAdapter() {
        return mArrayAdapterDimension;
    }

    public void setDimensionArrayAdapter(ArrayAdapter<RowModel> arrayAdapterDimension) {
        mArrayAdapterDimension = arrayAdapterDimension;
    }

    public ArrayAdapter<RowModel> getMinesArrayAdapter() {
        return mArrayAdapterMines;
    }

    public void setMinesArrayAdapter(ArrayAdapter<RowModel> minesArrayAdapter) {
        mArrayAdapterMines = minesArrayAdapter;
    }

    public interface OnFragmentInteractionListener {
        public void onGameSettingsDialogPositiveClick(int dimension, int mines);
    }

    private class ListItemViewHolder {
        private TextView mTextViewLabel = null;

        public ListItemViewHolder(View base) {
            setTextViewLabel((TextView) base.findViewById(android.R.id.text1));
        }

        private TextView getTextViewLabel() {
            return mTextViewLabel;
        }

        private void setTextViewLabel(TextView textViewLabel) {
            mTextViewLabel = textViewLabel;
        }
    }

    public class RowModel {
        private Integer mValue = null;
        private String mLabel = null;

        public RowModel(Integer value, String label) {
            setValue(value);
            setLabel(label);
        }

        public Integer getValue() {
            return mValue;
        }

        public void setValue(Integer value) {
            mValue = value;
        }

        public String getLabel() {
            return mLabel;
        }

        public void setLabel(String label) {
            mLabel = label;
        }
    }
}
