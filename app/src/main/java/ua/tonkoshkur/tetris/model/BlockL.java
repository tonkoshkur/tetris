package ua.tonkoshkur.tetris.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ua.tonkoshkur.tetris.R;

public class BlockL extends Block {

    public BlockL(Context context, LayoutInflater inflater, ViewGroup screenView) {
        super(context, inflater, screenView);
        resIds = new Integer[]{R.layout.layout_l_horizontal, R.layout.layout_l_vertical,};
        init(resIds);
    }
}
