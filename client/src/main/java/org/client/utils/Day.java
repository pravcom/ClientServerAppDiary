package org.client.utils;

import lombok.Data;
import org.exchange.task.Task;

import java.time.LocalDate;
import java.util.List;

@Data
public class Day {
    private LocalDate date;
    private String text;
    private List<Task> tasks;

    public Day(LocalDate date, List<Task> tasks) {
        this.date = date;
        this.tasks = tasks;
    }
}
