package com.joelzhu.worldofwarships.view.activity;

import com.joelzhu.base.view.AbsBaseActivity;
import com.joelzhu.bindview.annotations.OnClick;
import com.joelzhu.bindview.annotations.RootView;
import com.joelzhu.worldofwarships.R;
import com.joelzhu.worldofwarships.presenter.DynamicLoadUtil;

@RootView(R.layout.activity_main)
public class MainActivity extends AbsBaseActivity {
    @OnClick(R.id.main_player)
    public void onMainPlayerClicked() {
        DynamicLoadUtil.startRemoteActivity(this, "player.apk", "com.joelzhu.player.MainActivity");
    }
}