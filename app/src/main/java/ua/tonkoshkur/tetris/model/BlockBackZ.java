package ua.tonkoshkur.tetris.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ua.tonkoshkur.tetris.R;

public class BlockBackZ extends Block {

    public BlockBackZ(Context context, LayoutInflater inflater, ViewGroup screenView) {
        super(context, inflater, screenView);
        resIds = new Integer[]{R.layout.layout_back_z_horizontal, R.layout.layout_back_z_vertical};
        init(resIds);
    }
}
