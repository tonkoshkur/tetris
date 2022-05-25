package ua.tonkoshkur.tetris.model;

import ua.tonkoshkur.tetris.R;

public class BlockZ extends ThreeSquareWidthBlock {

    public BlockZ(int squareSize) {
        super(new int[]{
                        R.layout.layout_z_horizontal,
                        R.layout.layout_z_vertical,
                        R.layout.layout_z_horizontal,
                        R.layout.layout_z_vertical
                },
                squareSize);
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.Z;
    }
}
