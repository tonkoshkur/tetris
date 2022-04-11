package ua.tonkoshkur.tetris.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ua.tonkoshkur.tetris.R;

public class BlockQ extends Block {

    public BlockQ(Context context, LayoutInflater inflater, ViewGroup screenView) {
        super(context, inflater, screenView);
        resIds = new Integer[]{R.layout.layout_q_up, R.layout.layout_q_right, R.layout.layout_q_down, R.layout.layout_q_left};
        init(resIds);
    }
}
