package ua.tonkoshkur.tetris.model;

import ua.tonkoshkur.tetris.R;

public class BlockL extends FourSquareWidthBlock {

    public BlockL(int squareSize) {
        super(new int[]{
                        R.layout.layout_l_horizontal,
                        R.layout.layout_l_vertical,
                        R.layout.layout_l_horizontal,
                        R.layout.layout_l_vertical
                },
                squareSize);
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.L;
    }
}
