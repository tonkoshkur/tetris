package ua.tonkoshkur.tetris.model;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class Block {

    private final String TAG = Block.class.getSimpleName();
    protected final int squareSize;
    protected final int maxWidthInSquares;

    protected ViewGroup view;
    protected int[] resIds;
    protected int currentResPosition = 0;

    public Block(int[] resIds, int squareSize, int maxWidthInSquares) {
        this.resIds = resIds;
        this.squareSize = squareSize;
        this.maxWidthInSquares = maxWidthInSquares;
    }

    public ViewGroup getView() {
        return view;
    }

    public void setView(View view) {
        this.view = (ViewGroup) view;
    }

    public void setView(View view, Point point) {
        this.view = (ViewGroup) view;
        view.setX(point.getX());
        view.setY(point.getY());
    }

    public int getCurrentRes() {
        return resIds[currentResPosition];
    }

    public int getPreviousRes() {
        return resIds[getPreviousResPosition()];
    }

    public int getNextRes() {
        return resIds[getNextResPosition()];
    }

    public int getMaxWidthInSquares() {
        return this.maxWidthInSquares;
    }

    public int getPreviousResPosition() {
        return currentResPosition == 0
                ? resIds.length - 1
                : currentResPosition - 1;
    }

    public int getNextResPosition() {
        return currentResPosition == resIds.length - 1
                ? 0
                : currentResPosition + 1;
    }

    public void decrementCurrentResPosition() {
        currentResPosition = getPreviousResPosition();
    }

    public void incrementCurrentResPosition() {
        currentResPosition = getNextResPosition();
    }

    public void moveLeft() {
        view.setX(view.getX() - squareSize);
    }

    public void moveRight() {
        view.setX(view.getX() + squareSize);
    }

    public void moveBottom() {
        view.setY(view.getY() + squareSize);
    }

    public abstract List<Point> getPossiblePointsForNextRes();

    public abstract List<Point> getPossiblePointsForPreviousRes();

}
