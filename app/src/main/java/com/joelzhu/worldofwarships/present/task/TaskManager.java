package com.joelzhu.worldofwarships.present.task;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.joelzhu.common.ArrayUtil;
import com.joelzhu.common.http.OnPostRequestListener;
import com.joelzhu.common.http.Parameter;
import com.joelzhu.worldofwarships.present.task.command.BaseHttpStrategy;
import com.joelzhu.worldofwarships.present.task.command.SearchPlayerStrategy;
import com.joelzhu.worldofwarships.present.task.impl.SearchPlayerTask;

import java.util.HashMap;
import java.util.Map;

public final class TaskManager {
    private static TaskManager instance;

    private final static Object LOCKER = new Object();

    private Map<Class<? extends BaseHttpStrategy>, OnPostRequestListener> listenerMap;

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (LOCKER) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    private TaskManager() {
        listenerMap = new HashMap<>();
    }

    public void subscribeListener(Class<? extends BaseHttpStrategy> clazz, OnPostRequestListener listener) {
        listenerMap.put(clazz, listener);
    }

    public void executeTask(@Nullable Intent intent) {
        BaseHttpStrategy command = new SearchPlayerStrategy();
        command.subscribeListener(listenerMap.get(SearchPlayerStrategy.class));
        command.executeStrategy(intent);
    }
}