package com.hyd_coder.ppjoke.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hyd_coder.libnavannotationa.FragmentDestination;
import com.hyd_coder.ppjoke.R;

@FragmentDestination(pageUrl = "main/tabs/mine", needLogin = true)
public class MineFragment extends Fragment {

    public MineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mineragment, container, false);
    }

}
