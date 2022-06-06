package ua.tonkoshkur.tetris.ui.fragment;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_HEIGHT_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_WIDTH_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SIDE_MOVEMENT_DELAY;
import static ua.tonkoshkur.tetris.utils.Constants.SIDE_MOVEMENT_PERIOD;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.databinding.FragmentGameButtonBinding;
import ua.tonkoshkur.tetris.factory.BlockFactory;
import ua.tonkoshkur.tetris.model.Block;

public class GameButtonFragment extends GameFragment {

    private final String TAG = this.getClass().getSimpleName();
    private Timer mMoveAsideTimer;
    private FragmentGameButtonBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentGameButtonBinding.inflate(inflater, null, false);
        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        resetMoveAsideTimer();
        super.onPause();
    }

    @Override
    protected void onRunning() {
        mBinding.pauseTglBtn.setChecked(false);
    }

    @Override
    protected void onPaused() {
        mBinding.pauseTglBtn.setChecked(true);
    }

    @Override
    protected void onFinished() {
        resetMoveAsideTimer();
        showResultDialog(mBinding.score.getText().toString());
    }

    @Override
    protected void setupSquareSize() {
        int paddings = getResources().getDimensionPixelSize(R.dimen.default_padding);
        int radiusWidth = getResources().getDimensionPixelSize(R.dimen.radius_width);
        int pauseTbSize = getResources().getDimensionPixelSize(R.dimen.icon_btn_toggle_size);
        int busyField = (paddings * 4) + (pauseTbSize * 3) + (radiusWidth * 2);
        mSquareSize = (mDisplayMetrics.heightPixels - busyField) / SCREEN_HEIGHT_MULTIPLIER;
        mBinding.screen.setSquareSize(mSquareSize);
    }

    @Override
    protected void setupScreenSize() {
        int radiusWidth = getResources().getDimensionPixelSize(R.dimen.radius_width);
        ViewGroup.LayoutParams screenLayoutParams = mBinding.screen.getLayoutParams();
        screenLayoutParams.width = mSquareSize * SCREEN_WIDTH_MULTIPLIER + (radiusWidth * 2);
        screenLayoutParams.height = mSquareSize * SCREEN_HEIGHT_MULTIPLIER + (radiusWidth * 2);
        mBinding.screen.setLayoutParams(screenLayoutParams);
    }

    @Override
    protected void setupListeners() {
        //binding.rotateLeftBtn.setOnClickListener(view -> mViewModel.rotateToLeft());
        mBinding.rotateRightBtn.setOnClickListener(view -> mViewModel.rotateToRight());
        mBinding.leftBtn.setOnClickListener(view -> mViewModel.moveLeft());
        mBinding.rightBtn.setOnClickListener(view -> mViewModel.moveRight());
        mBinding.downBtn.setOnTouchListener((v, event) -> {
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
        mBinding.leftBtn.setOnTouchListener((v, event) -> {
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
        mBinding.rightBtn.setOnTouchListener((v, event) -> {
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
        mBinding.pauseTglBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mViewModel.pause();
            } else {
                mViewModel.resume();
            }
        });
    }

    @Override
    protected void updateNextBlockView(Block.BlockShape blockShape) {
        mBinding.nextBlockScreen.removeAllViews();
        Block block = BlockFactory.getBlock(blockShape, mNextBlockSquareSize);
        int res = block.getCurrentRes();
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(res, null, false);
        view.setLayoutParams(mBinding.nextBlockScreen.getLayoutParams());
        block.setView(view);
        mBinding.nextBlockScreen.addView(view);
    }

    @Override
    protected void updateScore(Integer score) {
        mBinding.score.setText(String.valueOf(score));
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
