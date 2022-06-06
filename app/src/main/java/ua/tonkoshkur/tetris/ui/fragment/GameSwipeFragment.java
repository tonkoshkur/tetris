package ua.tonkoshkur.tetris.ui.fragment;

import static android.view.MotionEvent.ACTION_UP;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_HEIGHT_MULTIPLIER;
import static ua.tonkoshkur.tetris.utils.Constants.SCREEN_WIDTH_MULTIPLIER;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.databinding.FragmentGameSwipeBinding;
import ua.tonkoshkur.tetris.factory.BlockFactory;
import ua.tonkoshkur.tetris.model.Block;

public class GameSwipeFragment extends GameFragment {

    private final String TAG = this.getClass().getSimpleName();

    private FragmentGameSwipeBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentGameSwipeBinding.inflate(inflater, null, false);
        return mBinding.getRoot();
    }

    @Override
    protected void setupSquareSize() {
        int paddings = getResources().getDimensionPixelSize(R.dimen.default_padding);
        int radiusWidth = getResources().getDimensionPixelSize(R.dimen.radius_width);
        int pauseTbSize = getResources().getDimensionPixelSize(R.dimen.icon_btn_toggle_size);
        int busyField = (paddings * 2) + (pauseTbSize * 2) + (radiusWidth * 2);
        mSquareSize = (mDisplayMetrics.heightPixels - busyField) / SCREEN_HEIGHT_MULTIPLIER;
        mBinding.screen.setSquareSize(mSquareSize);
    }

    protected void setupScreenSize() {
        int radiusWidth = getResources().getDimensionPixelSize(R.dimen.radius_width);
        ViewGroup.LayoutParams screenLayoutParams = mBinding.screen.getLayoutParams();
        screenLayoutParams.width = mSquareSize * SCREEN_WIDTH_MULTIPLIER + (radiusWidth * 2);
        screenLayoutParams.height = mSquareSize * SCREEN_HEIGHT_MULTIPLIER + (radiusWidth * 2);
        mBinding.screen.setLayoutParams(screenLayoutParams);
    }

    protected void setupListeners() {
        mBinding.touchZone.setOnTouchListener(new OnSwipeTouchListener(requireContext()));
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
        showResultDialog(mBinding.score.getText().toString());
    }

    class OnSwipeTouchListener implements View.OnTouchListener {

        private final float halfScreenWidth;
        private boolean isScrollOrFling = false;

        private final GestureDetector mGestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            halfScreenWidth = mDisplayMetrics.widthPixels / 2f;
            mGestureDetector = new GestureDetector(ctx, new OnSwipeTouchListener.GestureListener());
            mGestureDetector.setIsLongpressEnabled(true);
            mGestureDetector.setOnDoubleTapListener(null);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == ACTION_UP) {
                if (isScrollOrFling) {
                    isScrollOrFling = false;
                } else {
                    if (event.getRawX() < halfScreenWidth) {
                        mViewModel.rotateToLeft();
                    } else {
                        mViewModel.rotateToRight();
                    }
                }
                mViewModel.turnOffQuickMoving();
            }
            mGestureDetector.onTouchEvent(event);
            return true;
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private float savedX;

            @Override
            public boolean onDown(MotionEvent e) {
                savedX = e.getRawX();
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                isScrollOrFling = true;
                boolean result = false;
                try {
                    float savedY = e1.getRawY();
                    float currentX = e2.getRawX();
                    float currentY = e2.getRawY();
                    float diffX = Math.abs(savedX - currentX);
                    float diffY = Math.abs(savedY - currentY);
                    if (diffX > mSquareSize
                            && diffX > diffY) {
                        if (currentX < savedX) {
                            mViewModel.moveLeft();
                            savedX -= mSquareSize;
                        } else {
                            mViewModel.moveRight();
                            savedX += mSquareSize;
                        }
                        result = true;
                    } else if (diffY > mSquareSize * 2) {
                        if (currentY > savedY) {
                            mViewModel.turnOnQuickMoving();
                        } else {
                            mViewModel.turnOffQuickMoving();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        }
    }
}