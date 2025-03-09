package org.server.dao;

import org.server.entity.Task;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface TasksDAO {
    Task getTasksByDate(Date date) throws SQLException;
    void addTask(Task task) throws SQLException;
    void deleteTask(Task task) throws SQLException;
    List<Task> getAllTasksOfYear(int year) throws SQLException;
    List<Task> getAllTasksOfMonth(int year, int month) throws SQLException;
}
