package org.calendar;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
@Data
public class CalendarDiary {
    private int year;
    private List<List<Integer>> daysOfMonth;

    public CalendarDiary(Calendar calendar) {
        this.year = year;
        daysOfMonth = new ArrayList<List<Integer>>();

        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH, i);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            List<Integer> daysOfWeek = new ArrayList<>();

            for (int j = 1; j <= maxDays; j++) {
                calendar.set(Calendar.DAY_OF_MONTH, j);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                daysOfWeek.add(dayOfWeek);
            }
            daysOfMonth.add(daysOfWeek);
        }

        LocalDate today = LocalDate.of(year, 12, 1);

        for (int i = 0; i < 12; i++) {

        }
    }
}
