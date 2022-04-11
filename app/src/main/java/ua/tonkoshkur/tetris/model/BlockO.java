package ua.tonkoshkur.tetris.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ua.tonkoshkur.tetris.R;

public class BlockO extends Block {

    public BlockO(Context context, LayoutInflater inflater, ViewGroup screenView) {
        super(context, inflater, screenView);
        resIds = new Integer[]{R.layout.layout_o};
        init(resIds);
    }
}
