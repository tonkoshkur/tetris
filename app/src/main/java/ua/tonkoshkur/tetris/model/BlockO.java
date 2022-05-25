package ua.tonkoshkur.tetris.model;

import java.util.Collections;
import java.util.List;

import ua.tonkoshkur.tetris.R;

public class BlockO extends Block {

    public BlockO(int squareSize) {
        super(new int[]{R.layout.layout_o}, squareSize, 1);
    }

    @Override
    public List<Point> getPossiblePointsForPreviousRes() {
        return getPossiblePoints();
    }

    @Override
    public List<Point> getPossiblePointsForNextRes() {
        return getPossiblePoints();
    }

    private List<Point> getPossiblePoints() {
        Point point = new Point(mView.getX(), mView.getY());
        return Collections.singletonList(point);
    }

    @Override
    public BlockShape getBlockShape() {
        return BlockShape.O;
    }
}
