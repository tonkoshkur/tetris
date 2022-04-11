package ua.tonkoshkur.tetris.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ua.tonkoshkur.tetris.R;

public class BlockT extends Block {

    public BlockT(Context context, LayoutInflater inflater, ViewGroup screenView) {
        super(context, inflater, screenView);
        resIds = new Integer[]{R.layout.layout_t_up, R.layout.layout_t_right, R.layout.layout_t_down, R.layout.layout_t_left};
        init(resIds);
    }
}
