package com.joelzhu.player;

import android.content.Intent;

import com.joelzhu.base.presenter.network.HttpRequestTask;
import com.joelzhu.base.presenter.network.Parameter;

public class SearchPlayerTask extends HttpRequestTask {
    @Override
    public String logTAG() {
        return SearchPlayerTask.class.getSimpleName();
    }

    @Override
    public String requestUrl() {
        return "https://api.worldofwarships.asia/wows/account/list/";
    }

    @Override
    public Parameter[] requestParam(Intent intent) {
        return new Parameter[]{
                new Parameter("application_id", "b84d6b18ddc72a301e1caa59b632187e"),
                new Parameter("search", "JoelZhu")
        };
    }
}