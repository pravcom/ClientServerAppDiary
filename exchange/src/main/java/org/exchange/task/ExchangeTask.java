package org.exchange.task;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Getter @Setter
public class ExchangeTask implements Serializable {
    private Task task;
    private List<Task> taskList;
    public ExchangeTask(Task task, List<Task> taskList) {
        this.task = task;
        this.taskList = taskList;
    }

    public ExchangeTask(Task task) {
        this.task = task;
    }

    public ExchangeTask(List<Task> tasks) {
        this.taskList = tasks;
    }
}
