package ua.tonkoshkur.tetris.model;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class Block {

    private final String TAG = Block.class.getSimpleName();
    protected final int mSquareSize;
    protected final int mMaxWidthInSquares;
    protected ViewGroup mView;
    protected int[] mResIds;
    private int mCurrentResPosition;

    public enum BlockShape {
        Z,
        L,
        O,
        P,
        Q,
        T,
        BACK_Z
    }

    public Block(int[] resIds, int squareSize, int maxWidthInSquares) {
        mResIds = resIds;
        mSquareSize = squareSize;
        mMaxWidthInSquares = maxWidthInSquares;
        mCurrentResPosition = 0;
    }

    protected int getCurrentResPosition() {
        return mCurrentResPosition;
    }

    protected int getPreviousResPosition() {
        return mCurrentResPosition == 0
                ? mResIds.length - 1
                : mCurrentResPosition - 1;
    }

    protected int getNextResPosition() {
        return mCurrentResPosition == mResIds.length - 1
                ? 0
                : mCurrentResPosition + 1;
    }

    public ViewGroup getView() {
        return mView;
    }

    public void setView(View view) {
        mView = (ViewGroup) view;
    }

    public void setViewPoint(Point point) {
        mView.setX(point.getX());
        mView.setY(point.getY());
    }

    public int getCurrentRes() {
        return mResIds[mCurrentResPosition];
    }

    public int getPreviousRes() {
        return mResIds[getPreviousResPosition()];
    }

    public int getNextRes() {
        return mResIds[getNextResPosition()];
    }

    public int getMaxWidthInSquares() {
        return mMaxWidthInSquares;
    }

    public void decrementCurrentResPosition() {
        mCurrentResPosition = getPreviousResPosition();
    }

    public void incrementCurrentResPosition() {
        mCurrentResPosition = getNextResPosition();
    }

    public void moveLeft() {
        mView.setX(mView.getX() - mSquareSize);
    }

    public void moveRight() {
        mView.setX(mView.getX() + mSquareSize);
    }

    public void moveBottom() {
        mView.setY(mView.getY() + mSquareSize);
    }

    public abstract List<Point> getPossiblePointsForNextRes();

    public abstract List<Point> getPossiblePointsForPreviousRes();

    public abstract BlockShape getBlockShape();

}
