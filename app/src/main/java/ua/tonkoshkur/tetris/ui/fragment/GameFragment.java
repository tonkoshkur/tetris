package ua.tonkoshkur.tetris.ui.fragment;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static ua.tonkoshkur.tetris.utils.Constants.COUNT_OF_BLOCK_TYPES;
import static ua.tonkoshkur.tetris.utils.Constants.NEXT_BLOCK_SCREEN_HEIGHT_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.NEXT_BLOCK_SCREEN_WIDTH_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_HEIGHT_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_WIDTH_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SIDE_MOVEMENT_DELAY;
import static ua.tonkoshkur.tetris.utils.Constants.SIDE_MOVEMENT_PERIOD;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.databinding.FragmentGameBinding;
import ua.tonkoshkur.tetris.model.Block;
import ua.tonkoshkur.tetris.model.BlockBackZ;
import ua.tonkoshkur.tetris.model.BlockL;
import ua.tonkoshkur.tetris.model.BlockO;
import ua.tonkoshkur.tetris.model.BlockP;
import ua.tonkoshkur.tetris.model.BlockQ;
import ua.tonkoshkur.tetris.model.BlockT;
import ua.tonkoshkur.tetris.model.BlockZ;
import ua.tonkoshkur.tetris.model.Point;
import ua.tonkoshkur.tetris.ui.fragment.base.BaseFragment;
import ua.tonkoshkur.tetris.utils.SharedPrefs;
import ua.tonkoshkur.tetris.viewmodel.GameViewModel;

public class GameFragment extends BaseFragment {

    private final String TAG = GameFragment.class.getSimpleName();
    private final Random mRandom = new Random();

    private Timer mMoveAsideTimer;
    private AlertDialog endGameQuestionDialog;
    private GameViewModel mViewModel;
    private FragmentGameBinding binding;
    private int squareSize;

    private List<View> busySquares;
    private Block runningBlock;
    private Block nextBlock;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        busySquares = new ArrayList<>();
        squareSize = getResources().getDimensionPixelSize(R.dimen.square_size);
        endGameQuestionDialog = createEndGameQuestionDialog();

        binding = FragmentGameBinding.inflate(inflater, null, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        mViewModel.getGameStatusLiveData().observe(getViewLifecycleOwner(), status -> {
            switch (status) {
                case RUNNING:
                    binding.pauseTglBtn.setChecked(false);
                    break;
                case PAUSED:
                    binding.pauseTglBtn.setChecked(true);
                    break;
                case FINISHED:
                    saveIfBestScore();
                    binding.score.setText(String.valueOf(0));
                    busySquares.clear();
                    break;
            }
        });
        mViewModel.getActionLiveData().observe(getViewLifecycleOwner(), action -> {
            if (!mViewModel.isRunning()) {
                return;
            }
            switch (action) {
                case MOVE_LEFT:
                    if (!isRunningBlockAtLeftEdge() && !isBusySquareAtLeftEdge()) {
                        runningBlock.moveLeft();
                    }
                    break;
                case MOVE_RIGHT:
                    if (!isRunningBlockAtRightEdge() && !isBusySquareAtRightEdge()) {
                        runningBlock.moveRight();
                    }
                    break;
                case MOVE_BOTTOM:
                    if (!isRunningBlockAtBottomEdge() && !isBusySquareAtBottomEdge()) {
                        runningBlock.moveBottom();
                        break;
                    }
                    moveRunningBlockSquaresToBusySquares();
                    deleteLineIfNeed();
                    if (isStoppedBlocksTooHigh()) {
                        endGame();
                        break;
                    }
                    updateRunningAndNextBlocks();
                    break;
                case ROTATE_LEFT:
                    rotateBlockIfPossibleOn(runningBlock.getPossiblePointsForPreviousRes(), runningBlock.getPreviousRes());
                    break;
                case ROTATE_RIGHT:
                    rotateBlockIfPossibleOn(runningBlock.getPossiblePointsForNextRes(), runningBlock.getNextRes());
                    break;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setupScreenSize();
        setupNextBlockScreenSize();
        setupListeners();
        GameViewModel.GameStatus gameStatus = mViewModel.getGameStatusLiveData().getValue();
        if (gameStatus != null
                && (gameStatus.equals(GameViewModel.GameStatus.WAITING)
                || gameStatus.equals(GameViewModel.GameStatus.FINISHED))) {
            updateRunningAndNextBlocks();
            mViewModel.startGame();
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mViewModel.pause();
                endGameQuestionDialog.show();
            }
        });
    }

    @Override
    public void onPause() {
        mViewModel.pause();
        resetMoveAsideTimer();
        super.onPause();
    }

    private void setupScreenSize() {
        ConstraintLayout.LayoutParams screenLayoutParams = new ConstraintLayout.LayoutParams
                (squareSize * SCREEN_WIDTH_MULTIPLIER, squareSize * SCREEN_HEIGHT_MULTIPLIER);
        screenLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.bottomToTop = binding.gamepad.getId();
        screenLayoutParams.setMargins(0, 10, 0, 10);
        binding.screen.setLayoutParams(screenLayoutParams);
    }

    private void setupNextBlockScreenSize() {
        ConstraintLayout.LayoutParams screenLayoutParams = new ConstraintLayout.LayoutParams
                (squareSize * NEXT_BLOCK_SCREEN_WIDTH_MULTIPLIER, squareSize * NEXT_BLOCK_SCREEN_HEIGHT_MULTIPLIER);
        screenLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.endToStart = binding.pauseTglBtn.getId();
        screenLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.bottomToTop = binding.rotateRightBtn.getId();
        screenLayoutParams.setMargins(0, 10, 0, 10);
        binding.nextBlockScreen.setLayoutParams(screenLayoutParams);
    }

    private void setupListeners() {
        //binding.rotateLeftBtn.setOnClickListener(view -> mViewModel.rotateToLeft());
        binding.rotateRightBtn.setOnClickListener(view -> mViewModel.rotateToRight());
        binding.leftBtn.setOnClickListener(view -> mViewModel.moveLeft());
        binding.rightBtn.setOnClickListener(view -> mViewModel.moveRight());
        binding.downBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case ACTION_DOWN:
                    mViewModel.turnOnQuickMoving();
                    break;
                case ACTION_UP:
                    mViewModel.turnOffQuickMoving();
                    break;
            }
            return false;
        });
        binding.leftBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case ACTION_DOWN:
                    resetMoveAsideTimer();
                    MoveLeftTimerTask moveLeftTimerTask = new MoveLeftTimerTask();
                    mMoveAsideTimer.schedule(moveLeftTimerTask, SIDE_MOVEMENT_DELAY, SIDE_MOVEMENT_PERIOD);
                    break;
                case ACTION_UP:
                    resetMoveAsideTimer();
                    break;
            }
            return false;
        });
        binding.rightBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case ACTION_DOWN:
                    resetMoveAsideTimer();
                    MoveRightTimerTask moveRightTimerTask = new MoveRightTimerTask();
                    mMoveAsideTimer.schedule(moveRightTimerTask, SIDE_MOVEMENT_DELAY, SIDE_MOVEMENT_PERIOD);
                    break;
                case ACTION_UP:
                    resetMoveAsideTimer();
                    break;
            }
            return false;
        });

        binding.pauseTglBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mViewModel.pause();
            } else {
                mViewModel.resume();
            }
        });
    }

    private void updateRunningAndNextBlocks() {
        Log.i(TAG, "addNewBlock started");
        if (nextBlock != null) {
            binding.gamepad.removeView(nextBlock.getView());
        }
        runningBlock = runningBlock == null
                ? createRandomBlock()
                : nextBlock;
        nextBlock = createRandomBlock();
        addBlockToScreen(runningBlock);
        addNextBlockToGamePad(nextBlock);
    }

    private Block createRandomBlock() {
        int blockTypeId = mRandom.nextInt(COUNT_OF_BLOCK_TYPES);
        switch (blockTypeId) {
            case 0:
                return new BlockZ(squareSize);
            case 1:
                return new BlockL(squareSize);
            case 2:
                return new BlockO(squareSize);
            case 3:
                return new BlockP(squareSize);
            case 4:
                return new BlockQ(squareSize);
            case 5:
                return new BlockT(squareSize);
            case 6:
            default:
                return new BlockBackZ(squareSize);
        }
    }

    private void addBlockToScreen(Block block) {
        int maxWidthInSquares = block.getMaxWidthInSquares();
        int res = block.getCurrentRes();
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(res, null, false);
        binding.screen.addView(view);
        float x = maxWidthInSquares % 2 == 0
                ? (SCREEN_WIDTH_MULTIPLIER - maxWidthInSquares) * squareSize / 2f
                : SCREEN_WIDTH_MULTIPLIER * squareSize / 2f - squareSize;
        runningBlock.setView(view, new Point(x, 0));
    }

    private void addNextBlockToGamePad(Block block) {
        int res = block.getCurrentRes();
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(res, null, false);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.startToStart = binding.gamepad.getId();
        layoutParams.topToTop = binding.gamepad.getId();
        layoutParams.endToStart = binding.pauseTglBtn.getId();
        layoutParams.bottomToTop = binding.downBtn.getId();
        view.setLayoutParams(layoutParams);
        block.setView(view);
        binding.gamepad.addView(view);
    }

    private void rotateBlockIfPossibleOn(List<Point> possiblePoints, int newRes) {
        ViewGroup newView = (ViewGroup) getLayoutInflater().inflate(newRes, null, false);
        for (Point point : possiblePoints) {
            if (canRotateBlockAt(point)) {
                rotateBlock(newView, point);
                return;
            }
        }
    }

    private boolean canRotateBlockAt(Point point) {
        ViewGroup runningBlockView = runningBlock.getView();
        for (int i = 0; i < runningBlockView.getChildCount(); i++) {
            View square = runningBlockView.getChildAt(i);
            float squareX = point.getX() + runningBlockView.getHeight() - square.getY() - squareSize;
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
                || binding.screen.getWidth() <= point.getX()
                || binding.screen.getHeight() <= point.getY() + squareSize;
    }

    private boolean isBusySquareAtPoint(Point point) {
        return busySquares.stream()
                .anyMatch(square -> square.getX() == point.getX() && square.getY() == point.getY());
    }

    private void rotateBlock(View newView, Point pointForNextRes) {
        binding.screen.removeView(runningBlock.getView());
        binding.screen.addView(newView);
        runningBlock.setView(newView, pointForNextRes);
        runningBlock.incrementCurrentResPosition();
        newView.setX(pointForNextRes.getX());
        newView.setY(pointForNextRes.getY());
    }

    private void moveRunningBlockSquaresToBusySquares() {
        ViewGroup view = runningBlock.getView();
        List<Point> squarePoints = getBlockSquarePoints(view);
        createBusySquares(squarePoints);
        binding.screen.removeView(view);
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

    private void createBusySquares(List<Point> points) {
        //Log.i(TAG, "moveBlockSquaresToParent started");
        ViewGroup blockView = runningBlock.getView();
        blockView.removeAllViews();
        for (Point point : points) {
            View square = new View(new ContextThemeWrapper(requireContext(), R.style.SquareView), null, 0);
            square.setId(View.generateViewId());
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(squareSize, squareSize);
            layoutParams.startToStart = binding.screen.getId();
            layoutParams.topToTop = binding.screen.getId();
            square.setLayoutParams(layoutParams);
            binding.screen.addView(square);
            square.setX(point.getX());
            square.setY(point.getY());
            busySquares.add(square);
        }
    }

    private boolean isRunningBlockAtLeftEdge() {
        return runningBlock.getView().getX() == 0;
    }

    private boolean isRunningBlockAtRightEdge() {
        ViewGroup view = runningBlock.getView();
        return view.getX() + view.getWidth() == binding.screen.getWidth();
    }

    private boolean isRunningBlockAtBottomEdge() {
        ViewGroup view = runningBlock.getView();
        return view.getY() + view.getHeight() == binding.screen.getHeight()
                && binding.screen.getHeight() != 0;
    }

    private boolean isBusySquareAtLeftEdge() {
        //Log.i(TAG, "isBusySquareAtLeftEdge started");
        ViewGroup runningBlockView = runningBlock.getView();
        for (int j = 0; j < runningBlockView.getChildCount(); j++) {
            View runningSquare = runningBlockView.getChildAt(j);
            for (View busySquare : busySquares) {
                float xBetweenRunningAndBusySquare = runningSquare.getX() + runningBlockView.getX() - busySquare.getX();
                float yBetweenRunningAndBusySquare = runningSquare.getY() + runningBlockView.getY() - busySquare.getY();
                if (yBetweenRunningAndBusySquare == 0
                        && xBetweenRunningAndBusySquare == squareSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBusySquareAtRightEdge() {
        //Log.i(TAG, "isBusySquareAtRightEdge started");
        ViewGroup runningBlockView = runningBlock.getView();
        for (int j = 0; j < runningBlockView.getChildCount(); j++) {
            View runningSquare = runningBlockView.getChildAt(j);
            for (View busySquare : busySquares) {
                float xBetweenRunningAndBusySquare = busySquare.getX() - (runningSquare.getX() + runningBlockView.getX());
                float yBetweenRunningAndBusySquare = runningSquare.getY() + runningBlockView.getY() - busySquare.getY();
                if (yBetweenRunningAndBusySquare == 0
                        && xBetweenRunningAndBusySquare == squareSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBusySquareAtBottomEdge() {
        //Log.i(TAG, "isBusySquareAtBottomEdge started");
        ViewGroup runningBlockView = this.runningBlock.getView();
        for (View busySquare : busySquares) {
            for (int j = 0; j < runningBlockView.getChildCount(); j++) {
                View runningSquare = runningBlockView.getChildAt(j);
                float xBetweenSquareAndBusySquare = Math.abs(runningSquare.getX() + runningBlockView.getX() - busySquare.getX());
                float yBetweenSquareAndBusySquare = busySquare.getY() - runningSquare.getY() - runningBlockView.getY();
                if (xBetweenSquareAndBusySquare == 0
                        && yBetweenSquareAndBusySquare == squareSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteLineIfNeed() {
        //Log.i(TAG, "deleteLineIfNeed started");
        for (int i = 0; i < SCREEN_HEIGHT_MULTIPLIER; i++) {
            float lineY = i * squareSize;
            List<View> squaresOnLine = getSquaresOnLine(lineY);
            if (squaresOnLine.size() == SCREEN_WIDTH_MULTIPLIER) {
                deleteBusySquares(squaresOnLine);
                moveSquaresToBottom(lineY);
                incrementScore();
                mViewModel.increaseSpeedPermanent();
                deleteLineIfNeed();
                return;
            }
        }
    }

    private List<View> getSquaresOnLine(float lineY) {
        return busySquares.stream()
                .filter(square -> square.getY() == lineY)
                .collect(Collectors.toList());
    }

    private void deleteBusySquares(List<View> squares) {
        squares.forEach(squareView -> {
            busySquares.removeIf(square -> square.getX() == squareView.getX()
                    && square.getY() == squareView.getY());
            binding.screen.removeView(squareView);
        });
    }

    private void moveSquaresToBottom(float lineY) {
        busySquares.stream()
                .filter(square -> square.getY() < lineY)
                .forEach(square -> square.setY(square.getY() + squareSize));
    }

    private void incrementScore() {
        int currentScore = Integer.parseInt(binding.score.getText().toString());
        String newScoreStr = String.valueOf(++currentScore);
        binding.score.setText(newScoreStr);
    }

    private void saveIfBestScore() {
        int score = Integer.parseInt(binding.score.getText().toString());
        if (score > SharedPrefs.getIntProperty(getResources().getString(R.string.best_score_key))) {
            SharedPrefs.addIntProperty(getResources().getString(R.string.best_score_key), score);
        }
    }

    private boolean isStoppedBlocksTooHigh() {
        for (View square : busySquares) {
            if (square.getY() < squareSize * 3) {
                return true;
            }
        }
        return false;
    }

    private void endGame() {
        mViewModel.endGame();
        mNavController.popBackStack();
    }

    private AlertDialog createEndGameQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setIcon(android.R.drawable.stat_sys_warning)
                .setMessage(R.string.end_game_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) -> endGame())
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    dialog.dismiss();
                    mViewModel.resume();
                });
        return builder.create();
    }

    private void resetMoveAsideTimer() {
        if (mMoveAsideTimer != null) {
            mMoveAsideTimer.cancel();
        }
        mMoveAsideTimer = new Timer();
    }

    private class MoveLeftTimerTask extends TimerTask {
        @Override
        public void run() {
            mViewModel.moveLeft();
        }
    }

    private class MoveRightTimerTask extends TimerTask {
        @Override
        public void run() {
            mViewModel.moveRight();
        }
    }
}