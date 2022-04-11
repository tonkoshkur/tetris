package ua.tonkoshkur.tetris.ui.fragment.base;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import ua.tonkoshkur.tetris.R;

public class BaseFragment extends Fragment {

    protected NavController mNavController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        super.onCreate(savedInstanceState);
    }

    protected void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setIcon(android.R.drawable.stat_sys_warning)
                .setMessage(R.string.quit_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, which) -> requireActivity().finish())
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.dismiss());
        builder.create()
                .show();
    }
}
