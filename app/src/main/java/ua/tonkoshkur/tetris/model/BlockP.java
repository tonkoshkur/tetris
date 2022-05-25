package ua.tonkoshkur.tetris.model;

import ua.tonkoshkur.tetris.R;

public class BlockP extends ThreeSquareWidthBlock {

    public BlockP(int squareSize) {
        super(new int[]{
                        R.layout.layout_p_up,
                        R.layout.layout_p_right,
                        R.layout.layout_p_down,
                        R.layout.layout_p_left
                },
                squareSize);
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.P;
    }
}
