package ua.tonkoshkur.tetris.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ua.tonkoshkur.tetris.R;

public class BlockP extends Block {

    public BlockP(Context context, LayoutInflater inflater, ViewGroup screenView) {
        super(context, inflater, screenView);
        resIds = new Integer[]{R.layout.layout_p_up, R.layout.layout_p_right, R.layout.layout_p_down, R.layout.layout_p_left};
        init(resIds);
    }
}
