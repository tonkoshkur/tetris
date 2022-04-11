package ua.tonkoshkur.tetris.ui.fragment;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static ua.tonkoshkur.tetris.utils.Constants.COUNT_OF_BLOCK_TYPES;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_HEIGHT_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_WIDTH_MULTIPLIER;

import android.os.Bundle;
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

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.databinding.FragmentGameBinding;
import ua.tonkoshkur.tetris.model.*;
import ua.tonkoshkur.tetris.ui.fragment.base.BaseFragment;
import ua.tonkoshkur.tetris.utils.SharedPrefs;
import ua.tonkoshkur.tetris.viewmodel.GameViewModel;

public class GameFragment extends BaseFragment {

    private final Random mRandom = new Random();
    private Class[] blockClasses = new Class[] {
            BlockT.class,
            BlockO.class,
            BlockQ.class,
            BlockP.class,
            BlockL.class,
            BlockZ.class,
            BlockBackZ.class
    };

    private GameViewModel mViewModel;
    private FragmentGameBinding binding;
    private LayoutInflater inflater;
    private List<Point> busyPoints;
    private Block runningBlock;
    private boolean isMoveLeftBtnPressed;
    private boolean isMoveRightBtnPressed;
    private int blockSquareSize;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        isMoveLeftBtnPressed = false;
        isMoveRightBtnPressed = false;
        busyPoints = new ArrayList<>();
        blockSquareSize = getResources().getDimensionPixelSize(R.dimen.square_size);

        binding = FragmentGameBinding.inflate(inflater, null, false);
        setupScreenSize();
        setupListeners();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        mViewModel.getActionLiveData().observe(getViewLifecycleOwner(), action -> {
            switch (action) {
                case MOVE_LEFT:
                    if (canMoveToSide()) {
                        runningBlock.moveLeft();
                    }
                    break;
                case MOVE_RIGHT:
                    if (canMoveToSide()) {
                        runningBlock.moveRight();
                    }
                    break;
                case MOVE_BOTTOM:
                    if (!isCrashedIntoStoppedBlocks()) {
                        boolean result = runningBlock.moveBottom();
                        if (result)
                            break;
                    }
                    moveBlockSquaresToParent();
                    deleteLineIfNeed();
                    addNewBlock();
                    endGameIfStoppedBlocksTooHigh();
                    break;
                case ROTATE_LEFT:
                    runningBlock.rotate(false, busyPoints);
                    break;
                case ROTATE_RIGHT:
                    runningBlock.rotate(true, busyPoints);
                    break;
            }
        });
        addNewBlock();
        mViewModel.startGame();
    }

    private void addNewBlock() {
        int blockTypeId = mRandom.nextInt(COUNT_OF_BLOCK_TYPES);
        switch (blockTypeId) {
            case 0:
                runningBlock = new BlockZ(requireContext(), inflater, binding.screen);
                break;
            case 1:
                runningBlock = new BlockL(requireContext(), inflater, binding.screen);
                break;
            case 2:
                runningBlock = new BlockO(requireContext(), inflater, binding.screen);
                break;
            case 3:
                runningBlock = new BlockP(requireContext(), inflater, binding.screen);
                break;
            case 4:
                runningBlock = new BlockQ(requireContext(), inflater, binding.screen);
                break;
            case 5:
                runningBlock = new BlockT(requireContext(), inflater, binding.screen);
                break;
            case 6:
                runningBlock = new BlockBackZ(requireContext(), inflater, binding.screen);
                break;
        }
    }

    private void moveBlockSquaresToParent() {
        ViewGroup blockView = runningBlock.getView();
        List<Point> newPoints = new ArrayList<>();
        for (int i = 0; i < blockView.getChildCount(); i++) {
            View square = blockView.getChildAt(i);
            float x = blockView.getX() + square.getX();
            float y = blockView.getY() + square.getY();
            newPoints.add(new Point(x, y));
        }
        blockView.removeAllViews();
        for (Point busyPoint : newPoints) {
            View square = new View(new ContextThemeWrapper(requireContext(), R.style.SquareView), null, 0);
            square.setId(View.generateViewId());
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(blockSquareSize, blockSquareSize);
            layoutParams.startToStart = binding.screen.getId();
            layoutParams.topToTop = binding.screen.getId();
            square.setLayoutParams(layoutParams);
            binding.screen.addView(square);
            square.setX(busyPoint.getX());
            square.setY(busyPoint.getY());
        }

        busyPoints.addAll(newPoints);
        binding.screen.removeView(blockView);
    }

    private boolean canMoveToSide() {
        ViewGroup runningBlockView = runningBlock.getView();
        for (Point busyPoint : busyPoints) {
            for (int j = 0; j < runningBlockView.getChildCount(); j++) {
                View square = runningBlockView.getChildAt(j);
                float distanceX = Math.abs(square.getX() + runningBlockView.getX() - busyPoint.getX());
                float distanceY = Math.abs(square.getY() + runningBlockView.getY() - busyPoint.getY());
                if (distanceY == 0
                        && distanceX <= blockSquareSize) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isCrashedIntoStoppedBlocks() {
        ViewGroup runningBlockView = runningBlock.getView();
        for (Point busyPoint : busyPoints) {
            for (int j = 0; j < runningBlockView.getChildCount(); j++) {
                View runningBlockSquare = runningBlockView.getChildAt(j);
                float distanceX = Math.abs(runningBlockSquare.getX() + runningBlockView.getX() - busyPoint.getX());
                float distanceY = busyPoint.getY() - runningBlockSquare.getY() - runningBlockView.getY();
                if (distanceX == 0
                        && distanceY == blockSquareSize) {
                    return true;
                }
            }
        }
        return false;
    }

    private void endGameIfStoppedBlocksTooHigh() {
        for (Point point : busyPoints) {
            if (point.getY() < blockSquareSize * 3) {
                endGame();
                return;
            }
        }
    }

    private void deleteLineIfNeed() {
        for (int i = 0; i < SCREEN_HEIGHT_MULTIPLIER; i++) {
            float lineY = i * blockSquareSize;
            List<View> squaresOnLine = new ArrayList<>();
            for (int j = 0; j < binding.screen.getChildCount(); j++) {
                View square = binding.screen.getChildAt(j);
                if (square.getY() == lineY) {
                    squaresOnLine.add(square);
                }
            }
            if (squaresOnLine.size() == SCREEN_WIDTH_MULTIPLIER) {
                squaresOnLine.forEach(square -> {
                    busyPoints.removeIf(point -> point.getX() == square.getX()
                            && point.getY() == square.getY());
                    binding.screen.removeView(square);
                });
                for (int j = 0; j < binding.screen.getChildCount(); j++) {
                    View square = binding.screen.getChildAt(j);
                    if (square.getY() <= lineY) {
                        square.setY(square.getY() + blockSquareSize);
                    }
                }
                for (Point point : busyPoints) {
                    if (point.getY() < lineY) {
                        point.setY(point.getY() + blockSquareSize);
                    }
                }
                int currentScore = Integer.parseInt(binding.score.getText().toString());
                String newScoreStr = String.valueOf(++currentScore);
                binding.score.setText(newScoreStr);
                mViewModel.increaseSpeedPermanent();
            }
        }
    }

    private void setupScreenSize() {
        ConstraintLayout.LayoutParams screenLayoutParams = new ConstraintLayout.LayoutParams
                (blockSquareSize * SCREEN_WIDTH_MULTIPLIER, blockSquareSize * SCREEN_HEIGHT_MULTIPLIER);
        screenLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        screenLayoutParams.bottomToTop = binding.gamepad.getId();
        screenLayoutParams.setMargins(10, 10, 10, 10);
        binding.screen.setLayoutParams(screenLayoutParams);
    }

    private void setupListeners() {
        //binding.rotateLeftBtn.setOnClickListener(view -> mViewModel.rotateToLeft());
        binding.rotateRightBtn.setOnClickListener(view -> mViewModel.rotateToRight());
        /*binding.leftBtn.setOnClickListener(view -> mViewModel.moveToLeft());
        binding.rightBtn.setOnClickListener(view -> mViewModel.moveToRight());*/
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
                    isMoveLeftBtnPressed = true;
                    new Thread(() -> {
                        while (isMoveLeftBtnPressed) {
                            mViewModel.moveToLeft();
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case ACTION_UP:
                    isMoveLeftBtnPressed = false;
                    break;
            }
            return false;
        });
        binding.rightBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case ACTION_DOWN:
                    isMoveRightBtnPressed = true;
                    new Thread(() -> {
                        while (isMoveRightBtnPressed) {
                            mViewModel.moveToRight();
                            try {
                                Thread.sleep(150L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case ACTION_UP:
                    isMoveRightBtnPressed = false;
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

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                endGame();
            }
        });
    }

    private void endGame() {
        mViewModel.endGame();
        int score = Integer.parseInt(binding.score.getText().toString());
        if (score > SharedPrefs.getIntProperty(getResources().getString(R.string.best_score_key))) {
            SharedPrefs.addIntProperty(getResources().getString(R.string.best_score_key), score);
        }
        binding.screen.removeAllViews();
        binding.score.setText(String.valueOf(0));
        busyPoints.clear();
        mNavController.popBackStack();
    }
}