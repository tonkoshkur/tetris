package ua.tonkoshkur.tetris.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.databinding.FragmentStartBinding;
import ua.tonkoshkur.tetris.ui.fragment.base.BaseFragment;
import ua.tonkoshkur.tetris.utils.SharedPrefs;

public class StartFragment extends BaseFragment {

    private FragmentStartBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentStartBinding.inflate(inflater, container, false);
        mBinding.controlToggleGroup.check(mBinding.swipeControlBtn.getId());
        mBinding.startBtn.setOnClickListener(view -> {
            if (mBinding.controlToggleGroup.getCheckedButtonId() == mBinding.swipeControlBtn.getId()) {
                mNavController.navigate(R.id.action_startFragment_to_gameSwipeFragment);
            } else {
                mNavController.navigate(R.id.action_startFragment_to_gameFragment);
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setOnBackPressedCallback();
    }

    @Override
    public void onResume() {
        int bestScore = SharedPrefs.getInt(requireContext(), R.string.best_score_key);
        mBinding.bestScoreValue.setText(String.valueOf(bestScore));
        super.onResume();
    }

    private void setOnBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showQuitDialog();
            }
        });
    }

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setIcon(android.R.drawable.stat_sys_warning)
                .setMessage(R.string.quit_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) -> requireActivity().finish())
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.dismiss());
        builder.create()
                .show();
    }
}