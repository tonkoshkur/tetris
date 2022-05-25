package ua.tonkoshkur.tetris.ui.view;

import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_HEIGHT_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_WIDTH_MULTIPLIER;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.databinding.ViewScreenBinding;
import ua.tonkoshkur.tetris.factory.BlockFactory;
import ua.tonkoshkur.tetris.model.Block;
import ua.tonkoshkur.tetris.model.Point;
import ua.tonkoshkur.tetris.viewmodel.GameViewModel;

public class ScreenView extends FrameLayout {

    private final String TAG = this.getClass().getSimpleName();
    private ViewScreenBinding mBinding;
    private GameViewModel mViewModel;
    private int mSquareSize;
    private List<View> mBusySquares;
    private Block mRunningBlock;

    public ScreenView(Context context) {
        super(context);
        init();
    }

    public ScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private LayoutInflater getLayoutInflater() {
        return (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void init() {
        mBinding = ViewScreenBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mBusySquares = new ArrayList<>();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initViewModel();
    }

    private void initViewModel() {
        try {
            mViewModel = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(GameViewModel.class);
            mViewModel.getActionLiveData().observe(getLifecycleOwner(), new ActionObserver());
            mViewModel.getRunningBlockShapeLiveData().observe(getLifecycleOwner(), blockShape -> {
                if (blockShape == null || mViewModel.isPaused()) return;
                mRunningBlock = BlockFactory.getBlock(blockShape, mSquareSize);
                addBlockToScreen(mRunningBlock);
            });
        } catch (Exception ignored) {}
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBusySquares.clear();
        mBinding.screen.removeAllViews();
    }

    public void setSquareSize(int squareSize) {
        mSquareSize = squareSize;
    }

    private LifecycleOwner getLifecycleOwner() {
        Context context = getContext();
        try {
            return (LifecycleOwner) getContext();
        } catch (ClassCastException classCastException) {
            return (LifecycleOwner) ((ContextWrapper) context).getBaseContext();
        }
    }

    private boolean canRotateBlockAt(Point point) {
        ViewGroup runningBlockView = mRunningBlock.getView();
        for (int i = 0; i < runningBlockView.getChildCount(); i++) {
            View square = runningBlockView.getChildAt(i);
            float squareX = point.getX() + runningBlockView.getHeight() - square.getY() - mSquareSize;
            float squareY = point.getY() + square.getX();
            Point squarePoint = new Point(squareX, squareY);
            if (isPointOutOfScreen(squarePoint)
                    || isBusySquareAtPoint(squarePoint)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPointOutOfScreen(Point point) {
        return point.getX() < 0
                || mBinding.screen.getWidth() <= point.getX()
                || mBinding.screen.getHeight() <= point.getY();
    }

    private boolean isBusySquareAtPoint(Point point) {
        return mBusySquares.stream()
                .anyMatch(square -> square.getX() == point.getX() && square.getY() == point.getY());
    }

    private boolean isBusySquareAtLeftEdgeFromRunningBlock() {
        ViewGroup runningBlockView = mRunningBlock.getView();
        for (int j = 0; j < runningBlockView.getChildCount(); j++) {
            View runningSquare = runningBlockView.getChildAt(j);
            for (View busySquare : mBusySquares) {
                float xBetweenRunningAndBusySquare = runningSquare.getX() + runningBlockView.getX() - busySquare.getX();
                float yBetweenRunningAndBusySquare = runningSquare.getY() + runningBlockView.getY() - busySquare.getY();
                if (yBetweenRunningAndBusySquare == 0
                        && xBetweenRunningAndBusySquare == mSquareSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBusySquareAtRunningBlockRightEdge() {
        ViewGroup runningBlockView = mRunningBlock.getView();
        for (int j = 0; j < runningBlockView.getChildCount(); j++) {
            View runningSquare = runningBlockView.getChildAt(j);
            for (View busySquare : mBusySquares) {
                float xBetweenRunningAndBusySquare = busySquare.getX() - (runningSquare.getX() + runningBlockView.getX());
                float yBetweenRunningAndBusySquare = runningSquare.getY() + runningBlockView.getY() - busySquare.getY();
                if (yBetweenRunningAndBusySquare == 0
                        && xBetweenRunningAndBusySquare == mSquareSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBusySquareAtRunningBlockBottomEdge() {
        ViewGroup runningBlockView = mRunningBlock.getView();
        for (View busySquare : mBusySquares) {
            for (int j = 0; j < runningBlockView.getChildCount(); j++) {
                View runningSquare = runningBlockView.getChildAt(j);
                float xBetweenSquareAndBusySquare = Math.abs(runningSquare.getX() + runningBlockView.getX() - busySquare.getX());
                float yBetweenSquareAndBusySquare = busySquare.getY() - runningSquare.getY() - runningBlockView.getY();
                if (xBetweenSquareAndBusySquare == 0
                        && yBetweenSquareAndBusySquare == mSquareSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRunningBlockAtScreenLeftEdge() {
        return mRunningBlock.getView().getX() == 0;
    }

    private boolean isRunningBlockAtScreenRightEdge() {
        ViewGroup view = mRunningBlock.getView();
        return view.getX() + view.getWidth() == mBinding.screen.getWidth();
    }

    private boolean isRunningBlockAtScreenBottomEdge() {
        ViewGroup view = mRunningBlock.getView();
        return view.getY() + view.getHeight() == mBinding.screen.getHeight()
                && mBinding.screen.getHeight() != 0;
    }

    private boolean isStoppedBlocksTooHigh() {
        for (View square : mBusySquares) {
            if (square.getY() < mSquareSize * 3) {
                return true;
            }
        }
        return false;
    }

    private List<Point> getBlockSquarePoints(ViewGroup blockView) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < blockView.getChildCount(); i++) {
            View squareView = blockView.getChildAt(i);
            float x = blockView.getX() + squareView.getX();
            float y = blockView.getY() + squareView.getY();
            Point point = new Point(x, y);
            points.add(point);
        }
        return points;
    }

    private List<View> getSquaresOnLine(float lineY) {
        return mBusySquares.stream()
                .filter(square -> square.getY() == lineY)
                .collect(Collectors.toList());
    }

    private void normalizeSquaresSize(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View blockSquare = view.getChildAt(i);
            ConstraintLayout.LayoutParams squareLayoutParams = (ConstraintLayout.LayoutParams) blockSquare.getLayoutParams();
            squareLayoutParams.height = mSquareSize;
            squareLayoutParams.width = mSquareSize;
            blockSquare.setLayoutParams(squareLayoutParams);
        }
    }

    private void addBlockToScreen(Block block) {
        int maxWidthInSquares = block.getMaxWidthInSquares();
        int res = block.getCurrentRes();
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(res, null, false);
        mBinding.screen.addView(view);
        normalizeSquaresSize(view);
        float x = maxWidthInSquares % 2 == 0
                ? (SCREEN_WIDTH_MULTIPLIER - maxWidthInSquares) * mSquareSize / 2f
                : SCREEN_WIDTH_MULTIPLIER * mSquareSize / 2f - mSquareSize;
        mRunningBlock.setView(view, new Point(x, 0));
    }

    private boolean rotateBlockIfPossible(List<Point> possiblePoints, int newRes) {
        ViewGroup newView = (ViewGroup) getLayoutInflater().inflate(newRes, null, false);
        for (Point point : possiblePoints) {
            if (canRotateBlockAt(point)) {
                rotateBlock(newView, point);
                return true;
            }
        }
        return false;
    }

    private void rotateBlock(ViewGroup newView, Point pointForNextRes) {
        mBinding.screen.removeView(mRunningBlock.getView());
        mBinding.screen.addView(newView);
        mRunningBlock.setView(newView, pointForNextRes);
        newView.setX(pointForNextRes.getX());
        newView.setY(pointForNextRes.getY());
        normalizeSquaresSize(newView);
    }

    private void moveRunningBlockSquaresToBusySquares() {
        ViewGroup view = mRunningBlock.getView();
        List<Point> squarePoints = getBlockSquarePoints(view);
        createBusySquares(squarePoints);
        mBinding.screen.removeView(view);
    }

    private void moveSquaresToBottom(float lineY) {
        mBusySquares.stream()
                .filter(square -> square.getY() < lineY)
                .forEach(square -> square.setY(square.getY() + mSquareSize));
    }

    private void createBusySquares(List<Point> points) {
        ViewGroup blockView = mRunningBlock.getView();
        blockView.removeAllViews();
        for (Point point : points) {
            View square = new View(new ContextThemeWrapper(getContext(), R.style.BusySquareView), null, 0);
            square.setId(View.generateViewId());
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(mSquareSize, mSquareSize);
            square.setLayoutParams(layoutParams);
            mBinding.screen.addView(square);
            square.setX(point.getX());
            square.setY(point.getY());
            mBusySquares.add(square);
        }
    }

    private void deleteLineIfNeed() {
        for (int i = 0; i < SCREEN_HEIGHT_MULTIPLIER; i++) {
            float lineY = i * mSquareSize;
            List<View> squaresOnLine = getSquaresOnLine(lineY);
            if (squaresOnLine.size() == SCREEN_WIDTH_MULTIPLIER) {
                deleteBusySquares(squaresOnLine);
                moveSquaresToBottom(lineY);
                mViewModel.incrementScore();
                mViewModel.increaseSpeedPermanent();
                deleteLineIfNeed();
                return;
            }
        }
    }

    private void deleteBusySquares(List<View> squares) {
        squares.forEach(squareView -> {
            mBusySquares.removeIf(square -> square.getX() == squareView.getX()
                    && square.getY() == squareView.getY());
            mBinding.screen.removeView(squareView);
        });
    }

    class ActionObserver implements Observer<GameViewModel.Action> {

        @Override
        public void onChanged(GameViewModel.Action action) {
            if (!mViewModel.isRunning()) {
                return;
            }
            switch (action) {
                case MOVE_LEFT:
                    if (!isRunningBlockAtScreenLeftEdge() && !isBusySquareAtLeftEdgeFromRunningBlock()) {
                        mRunningBlock.moveLeft();
                    }
                    break;
                case MOVE_RIGHT:
                    if (!isRunningBlockAtScreenRightEdge() && !isBusySquareAtRunningBlockRightEdge()) {
                        mRunningBlock.moveRight();
                    }
                    break;
                case MOVE_BOTTOM:
                    if (!isRunningBlockAtScreenBottomEdge() && !isBusySquareAtRunningBlockBottomEdge()) {
                        mRunningBlock.moveBottom();
                        break;
                    }
                    moveRunningBlockSquaresToBusySquares();
                    deleteLineIfNeed();
                    if (isStoppedBlocksTooHigh()) {
                        mViewModel.endGame();
                        break;
                    }
                    mViewModel.updateRunningAndNextBlocks();
                    break;
                case ROTATE_LEFT:
                    boolean isRotatedToLeft = rotateBlockIfPossible(mRunningBlock.getPossiblePointsForPreviousRes(), mRunningBlock.getPreviousRes());
                    if (isRotatedToLeft) mRunningBlock.decrementCurrentResPosition();
                    break;
                case ROTATE_RIGHT:
                    boolean isRotatedToRight = rotateBlockIfPossible(mRunningBlock.getPossiblePointsForNextRes(), mRunningBlock.getNextRes());
                    if (isRotatedToRight) mRunningBlock.incrementCurrentResPosition();
                    break;
            }
        }
    }
}