package com.joelzhu.base.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.joelzhu.bindview.JZBindView;

public abstract class AbsBaseFragment extends Fragment {
    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Bind fragment view when it being created.
        View rootView = JZBindView.bindView(this, inflater, container);

        createFragment(rootView);
        return rootView;
    }

    @Override
    public final void onAttach(@NonNull Context context) {
        super.onAttach(context);

        attachFragment();
    }

    @Override
    public final void onPause() {
        super.onPause();

        pauseFragment();
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();

        destroyFragment();
    }

    protected void createFragment(View rootView) {
    }

    protected void attachFragment() {
    }

    protected void pauseFragment() {
    }

    protected void destroyFragment() {
    }
}