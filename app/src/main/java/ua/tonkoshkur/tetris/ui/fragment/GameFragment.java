package ua.tonkoshkur.tetris.ui.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.model.Block;
import ua.tonkoshkur.tetris.ui.fragment.base.BaseFragment;
import ua.tonkoshkur.tetris.viewmodel.GameViewModel;

public abstract class GameFragment extends BaseFragment {

    private final String TAG = GameFragment.class.getSimpleName();

    protected DisplayMetrics mDisplayMetrics;
    protected AlertDialog mEndGameQuestionDialog;
    protected GameViewModel mViewModel;
    protected int mSquareSize;
    protected int mNextBlockSquareSize;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNextBlockSquareSize = getResources().getDimensionPixelSize(R.dimen.next_block_square_size);
        mDisplayMetrics = getResources().getDisplayMetrics();
        mEndGameQuestionDialog = createEndGameQuestionDialog();

        mViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        mViewModel.getGameStatusLiveData().observe(getViewLifecycleOwner(), status -> {
            switch (status) {
                case RUNNING:
                    onRunning();
                    break;
                case PAUSED:
                    onPaused();
                    break;
                case FINISHED:
                    onFinished();
                    break;
            }
        });
        mViewModel.getScoreLiveData().observe(getViewLifecycleOwner(), this::updateScore);
        mViewModel.getNextBlockShapeLiveData().observe(getViewLifecycleOwner(), nextBlockShape -> {
            if (nextBlockShape == null) return;
            updateNextBlockView(nextBlockShape);
            mViewModel.turnOffQuickMoving();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setupSquareSize();
        setupScreenSize();
        setupListeners();
        mViewModel.startGame();
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mViewModel.pause();
                mEndGameQuestionDialog.show();
            }
        });
    }

    @Override
    public void onPause() {
        mViewModel.pause();
        super.onPause();
    }

    private AlertDialog createEndGameQuestionDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(
                requireContext(),
                com.google.android.material.R.style.MaterialAlertDialog_Material3
        );
        builder.setIcon(R.drawable.ic_warning_24)
                .setTitle(R.string.end_game_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> mViewModel.endGame())
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    dialog.dismiss();
                    mViewModel.resume();
                });
        return builder.create();
    }

    protected void showResultDialog(String score) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(
                requireContext(),
                com.google.android.material.R.style.MaterialAlertDialog_Material3
        );
        builder.setIcon(R.drawable.ic_info_24)
                .setTitle(getString(R.string.result, score))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> mNavController.popBackStack());
        builder.create()
                .show();
    }

    protected abstract void onRunning();

    protected abstract void onPaused();

    protected abstract void onFinished();

    protected abstract void setupScreenSize();

    protected abstract void setupSquareSize();

    protected abstract void setupListeners();

    protected abstract void updateNextBlockView(Block.BlockShape blockShape);

    protected abstract void updateScore(Integer score);
}