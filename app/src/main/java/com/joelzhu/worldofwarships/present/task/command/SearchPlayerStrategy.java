package com.joelzhu.worldofwarships.present.task.command;

import com.joelzhu.base.present.HttpRequestTask;
import com.joelzhu.worldofwarships.present.task.impl.SearchPlayerTask;

public class SearchPlayerStrategy extends BaseHttpStrategy {
    @Override
    Class<? extends HttpRequestTask> bindingTask() {
        return SearchPlayerTask.class;
    }
}