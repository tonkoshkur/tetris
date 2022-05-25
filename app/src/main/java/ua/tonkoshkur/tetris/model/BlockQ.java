package ua.tonkoshkur.tetris.model;

import ua.tonkoshkur.tetris.R;

public class BlockQ extends ThreeSquareWidthBlock {

    public BlockQ(int squareSize) {
        super(new int[]{
                        R.layout.layout_q_up,
                        R.layout.layout_q_right,
                        R.layout.layout_q_down,
                        R.layout.layout_q_left
                },
                squareSize);
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.Q;
    }
}
