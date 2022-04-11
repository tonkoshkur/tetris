package ua.tonkoshkur.tetris.ui.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.tonkoshkur.tetris.R;
import ua.tonkoshkur.tetris.databinding.FragmentStartBinding;
import ua.tonkoshkur.tetris.ui.fragment.base.BaseFragment;
import ua.tonkoshkur.tetris.utils.SharedPrefs;
import ua.tonkoshkur.tetris.viewmodel.StartViewModel;

public class StartFragment extends BaseFragment implements View.OnClickListener {

    private StartViewModel mViewModel;
    private FragmentStartBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStartBinding.inflate(inflater, container, false);
        binding.startBtn.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel = new ViewModelProvider(this).get(StartViewModel.class);
    }

    @Override
    public void onResume() {
        SharedPrefs.init(requireContext());
        int bestScore = SharedPrefs.getIntProperty(getResources().getString(R.string.best_score_key));
        binding.bestScoreValue.setText(String.valueOf(bestScore));
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startBtn:
                mNavController.navigate(R.id.action_startFragment_to_gameFragment);
                break;
        }
    }
}