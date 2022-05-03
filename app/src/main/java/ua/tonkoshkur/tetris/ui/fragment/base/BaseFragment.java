package ua.tonkoshkur.tetris.ui.fragment.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import ua.tonkoshkur.tetris.R;

public class BaseFragment extends Fragment {

    protected NavController mNavController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
}
