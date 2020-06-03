package com.hyd_coder.ppjoke.ui.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hyd_coder.libnavannotationa.FragmentDestination;
import com.hyd_coder.ppjoke.R;

@FragmentDestination(pageUrl = "main/tabs/find")
public class FindFragment extends Fragment {

    private FindViewModel mFindViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFindViewModel =
                ViewModelProviders.of(this).get(FindViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        mFindViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}