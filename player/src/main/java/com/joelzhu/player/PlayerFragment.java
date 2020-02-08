package com.joelzhu.player;

import android.view.View;
import android.widget.Toast;

import com.joelzhu.base.view.BaseFragment;
import com.joelzhu.bindview.annotations.RootView;
import com.joelzhu.bindview.annotations.OnClick;

@RootView(resName = "fragment_player")
public class PlayerFragment extends BaseFragment {
    @Override
    protected void createFragment(View rootView) {
    }

    @OnClick(resName = "fragment_button")
    public void onButtonClicked() {
        Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
    }
}