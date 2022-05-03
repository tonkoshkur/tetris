package ua.tonkoshkur.tetris.model;

import ua.tonkoshkur.tetris.R;

public class BlockT extends ThreeSquareWidthBlock {

    public BlockT(int squareSize) {
        super(new int[]{
                        R.layout.layout_t_up,
                        R.layout.layout_t_right,
                        R.layout.layout_t_down,
                        R.layout.layout_t_left
                },
                squareSize);
    }
}
