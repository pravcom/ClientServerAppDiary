package org.exchange.calendar;

import lombok.Getter;
import lombok.Setter;
import org.exchange.task.Task;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
@Setter @Getter
public class ExchangeCalendar implements Serializable {
    private LocalDate date;
//    private List<List<Integer>> daysOfMonth;
    private List<Task> tasks;

    public ExchangeCalendar(LocalDate calendar, List<Task> tasks) {
        this.date = calendar;
//        this.daysOfMonth = daysOfMonth;
        this.tasks = tasks;
    }

    public ExchangeCalendar(LocalDate calendar) {
        this.date = calendar;
//        this.daysOfMonth = daysOfMonth;
    }

}
