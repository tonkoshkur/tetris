package ua.tonkoshkur.tetris.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ThreeSquareWidthBlock extends Block {

    public ThreeSquareWidthBlock(int[] resIds, int squareSize) {
        super(resIds, squareSize, 3);
    }

    @Override
    public List<Point> getPossiblePointsForPreviousRes() {
        int newResID = getPreviousResPosition();
        float newX = mView.getX();
        float newY = mView.getY();
        switch (newResID) {
            case 0:
                newX -= mSquareSize;
                break;
            case 1:
                newX += mSquareSize;
                newY -= mSquareSize;
                break;
            case 2:
                newY += mSquareSize;
                break;
            case 3:
                break;
        }
        return getPossiblePointsFor(newX, newY);
    }

    @Override
    public List<Point> getPossiblePointsForNextRes() {
        int newResID = getNextResPosition();
        float newX = mView.getX();
        float newY = mView.getY();
        switch (newResID) {
            case 0:
                break;
            case 1:
                newX += mSquareSize;
                break;
            case 2:
                newX -= mSquareSize;
                newY += mSquareSize;
                break;
            case 3:
                newY -= mSquareSize;
                break;
        }
        return getPossiblePointsFor(newX, newY);
    }

    /**
     * Order is important
     */
    private List<Point> getPossiblePointsFor(float x, float y) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(x, y));
        switch (getCurrentResPosition()) {
            case 0:
                points.add(new Point(x, y - mSquareSize));
                points.add(new Point(x + mSquareSize, y));
                points.add(new Point(x - mSquareSize, y));
                points.add(new Point(x, y + mSquareSize));
                break;
            case 1:
                points.add(new Point(x + mSquareSize, y));
                points.add(new Point(x - mSquareSize, y));
                points.add(new Point(x, y - mSquareSize));
                points.add(new Point(x, y + mSquareSize));
                break;
            case 2:
                points.add(new Point(x, y + mSquareSize));
                points.add(new Point(x, y - mSquareSize));
                points.add(new Point(x - mSquareSize, y));
                points.add(new Point(x + mSquareSize, y));
                break;
            case 3:
                points.add(new Point(x - mSquareSize, y));
                points.add(new Point(x + mSquareSize, y));
                points.add(new Point(x, y - mSquareSize));
                points.add(new Point(x, y + mSquareSize));
                break;
        }
        return points;
    }
}
