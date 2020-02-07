package com.joelzhu.worldofwarships.present.task.command;

import android.content.Intent;

import com.joelzhu.base.present.HttpRequestTask;
import com.joelzhu.base.present.LogUtil;
import com.joelzhu.common.http.OnPostRequestListener;

public abstract class BaseHttpStrategy implements IHttpStrategy {
    private final static String TAG = BaseHttpStrategy.class.getSimpleName();

    private HttpRequestTask task;

    abstract Class<? extends HttpRequestTask> bindingTask();

    public BaseHttpStrategy() {
        createTaskInstance();
    }

    @Override
    public void executeStrategy(Intent intent) {
        if (task != null) {
            task.execute(task.requestParam(intent));
        } else {
            LogUtil.e(TAG, "Task is null, ignore executing.");
        }
    }

    public void subscribeListener(OnPostRequestListener listener) {
        if (task != null) {
            task.subscribeRequestListener(listener);
        }
    }

    private void createTaskInstance() {
        try {
            task = bindingTask().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            LogUtil.e(TAG, "Create the instance of task failed.");
            LogUtil.e(TAG, e.getMessage());
        }
    }
}