package ua.tonkoshkur.tetris.model;

import ua.tonkoshkur.tetris.R;

public class BlockBackZ extends ThreeSquareWidthBlock {

    public BlockBackZ(int squareSize) {
        super(new int[]{
                        R.layout.layout_back_z_horizontal,
                        R.layout.layout_back_z_vertical,
                        R.layout.layout_back_z_horizontal,
                        R.layout.layout_back_z_vertical
                },
                squareSize);
    }
}
