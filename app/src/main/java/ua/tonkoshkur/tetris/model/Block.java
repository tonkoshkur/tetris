package ua.tonkoshkur.tetris.model;

import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_WIDTH_MULTIPLIER;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ua.tonkoshkur.tetris.R;

public abstract class Block {

    private final ViewGroup screenView;
    private final int squareSize;
    private final LayoutInflater inflater;

    private int currentBlockId = 0;
    private ViewGroup view;

    protected Integer[] resIds;

    public Block(Context context, LayoutInflater inflater, ViewGroup screenView) {
        this.inflater = inflater;
        this.screenView = screenView;
        this.squareSize = context.getResources().getDimensionPixelSize(R.dimen.square_size);
    }

    protected void init(Integer[] resIds) {
        this.resIds = resIds;
        view = (ViewGroup) inflater.inflate(resIds[currentBlockId], null, false);
        view.setX(SCREEN_WIDTH_MULTIPLIER * squareSize / 2f - squareSize);
        screenView.addView(view);
    }

    public ViewGroup getView() {
        return view;
    }

    public boolean moveLeft() {
        float x = view.getX();
        if (x > 0) {
            view.setX(x - squareSize);
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        float x = view.getX();
        if (x < screenView.getWidth() - view.getWidth()) {
            view.setX(x + squareSize);
            return true;
        }
        return false;
    }

    public boolean moveBottom() {
        float y = view.getY();
        if (y + view.getHeight() < screenView.getHeight()) {
            view.setY(y + squareSize);
            return true;
        }
        return false;
    }

    public void rotate(boolean clockwise, List<Point> busyPoints) {
        if (resIds.length <= 1) {
            return;
        }
        float x = view.getX();
        float y = view.getY() + view.getHeight() - view.getWidth();
        if (view.getWidth() > view.getHeight()) {
            x += squareSize;
            y += squareSize;
        } else {
            x -= squareSize;
            y -= squareSize;
        }

        int newBlockId;
        if (clockwise)
            newBlockId = currentBlockId == resIds.length - 1
                    ? 0
                    : currentBlockId + 1;
        else
            newBlockId = currentBlockId == 0
                    ? resIds.length - 1
                    : currentBlockId - 1;
        ViewGroup newView = (ViewGroup) inflater.inflate(resIds[newBlockId], null, false);
        for (int i = 0; i < newView.getChildCount(); i++) {
            float squareX = view.getChildAt(i).getY();
            float squareY = view.getChildAt(i).getX();
            if (x + squareX < 0
                    || x + squareX + squareSize > screenView.getWidth()) {
                return;
            }
            for (Point busyPoint : busyPoints) {
                if (busyPoint.getX() == squareX + x
                        && busyPoint.getY() == squareY + y) {
                    return;
                }
            }
        }
        currentBlockId = newBlockId;
        screenView.removeView(view);
        screenView.addView(newView);
        view = newView;
        view.setX(x);
        view.setY(y);
    }

}
