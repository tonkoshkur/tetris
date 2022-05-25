package ua.tonkoshkur.tetris.model;

import java.util.ArrayList;
import java.util.List;

public abstract class FourSquareWidthBlock extends Block {

    public FourSquareWidthBlock(int[] resIds, int squareSize) {
        super(resIds, squareSize, 4);
    }

    @Override
    public List<Point> getPossiblePointsForPreviousRes() {
        float newX = mView.getX();
        float newY = mView.getY();
        switch (getPreviousResPosition()) {
            case 0:
                newX -= mSquareSize;
                newY += mSquareSize * 2;
                break;
            case 1:
                newX += mSquareSize;
                newY -= mSquareSize;
                break;
            case 2:
                newX -= mSquareSize * 2;
                newY += mSquareSize;
                break;
            case 3:
                newX += mSquareSize * 2;
                newY -= mSquareSize * 2;
                break;
        }
        return getPossiblePointsFor(newX, newY);
    }

    @Override
    public List<Point> getPossiblePointsForNextRes() {
        float newX = mView.getX();
        float newY = mView.getY();
        switch (getNextResPosition()) {
            case 0:
                newX -= mSquareSize * 2;
                newY += mSquareSize * 2;
                break;
            case 1:
                newX += mSquareSize;
                newY -= mSquareSize * 2;
                break;
            case 2:
                newX -= mSquareSize;
                newY += mSquareSize;
                break;
            case 3:
                newX += mSquareSize * 2;
                newY -= mSquareSize;
                break;
        }
        return getPossiblePointsFor(newX, newY);
    }

    private List<Point> getPossiblePointsFor(float x, float y) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(x, y));
        switch (getCurrentResPosition()) {
            case 0:
            case 2:
                points.add(new Point(x, y - mSquareSize));
                points.add(new Point(x, y - (mSquareSize * 2)));
                break;
            case 1:
            case 3:
                points.add(new Point(x + mSquareSize, y));
                points.add(new Point(x + (mSquareSize * 2), y));
                points.add(new Point(x - mSquareSize, y));
                points.add(new Point(x - (mSquareSize * 2), y));
                break;
        }
        return points;
    }
}
