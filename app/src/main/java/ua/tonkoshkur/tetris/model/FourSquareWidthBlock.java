package ua.tonkoshkur.tetris.model;

import java.util.ArrayList;
import java.util.List;

public class FourSquareWidthBlock extends Block {

    public FourSquareWidthBlock(int[] resIds, int squareSize) {
        super(resIds, squareSize, 4);
    }

    @Override
    public List<Point> getPossiblePointsForPreviousRes() {
        int newResID = getPreviousResPosition();
        float newX = view.getX();
        float newY = view.getY();
        switch (newResID) {
            case 0:
                newX -= squareSize;
                newY += squareSize * 2;
                break;
            case 1:
                newX += squareSize;
                newY -= squareSize;
                break;
            case 2:
                newX -= squareSize * 2;
                newY += squareSize;
                break;
            case 3:
                newX += squareSize * 2;
                newY -= squareSize * 2;
                break;
        }
        return getPossiblePointsFor(newX, newY);
    }

    @Override
    public List<Point> getPossiblePointsForNextRes() {
        int newResID = getNextResPosition();
        float newX = view.getX();
        float newY = view.getY();
        switch (newResID) {
            case 0:
                newX -= squareSize * 2;
                newY += squareSize * 2;
                break;
            case 1:
                newX += squareSize;
                newY -= squareSize * 2;
                break;
            case 2:
                newX -= squareSize;
                newY += squareSize;
                break;
            case 3:
                newX += squareSize * 2;
                newY -= squareSize;
                break;
        }
        return getPossiblePointsFor(newX, newY);
    }

    private List<Point> getPossiblePointsFor(float x, float y) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(x, y));
        points.add(new Point(x + squareSize, y));
        points.add(new Point(x - squareSize, y));
        points.add(new Point(x + (squareSize * 2), y));
        points.add(new Point(x - (squareSize * 2), y));
        return points;
    }
}
