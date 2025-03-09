package org.server.dao;

import org.server.entity.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TasksDAOImpl implements TasksDAO {
    private Connection conn;

    public TasksDAOImpl(Connection conn) {
        this.conn = conn;
    }
    @Override
    public Task getTasksByDate(Date date) throws SQLException {
        String sql = "select * from tasks where date_key = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setDate(1,date);
            ResultSet resultSet = statement.executeQuery(sql);
            Task task = new Task();
            while(resultSet.next()){
                task.setDateKey(resultSet.getDate("date_key"));
                task.setProject(resultSet.getString("project"));
                task.setDescription(resultSet.getString("description"));
                task.setHoursSpent(resultSet.getFloat("hours_spent"));
            }
            return task;
        }

    }

    @Override
    public void addTask(Task task) throws SQLException {
        String sql = "insert into tasks (date_key, project, description, hours_spent) values (?,?,?,?)";
        try(PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setDate(1, task.getDateKey());
            statement.setString(2, task.getProject());
            statement.setString(3, task.getDescription());
            statement.setFloat(4, task.getHoursSpent());

            statement.executeUpdate();
        }
    }

    @Override
    public void deleteTask(Task task) throws SQLException {
        String sql = "delete from tasks where id = ? and date_key = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, task.getId());
            statement.setDate(2, task.getDateKey());

            statement.executeUpdate();
        }
    }

    @Override
    public List<Task> getAllTasksOfYear(int year) throws SQLException {
        String sql = "select * from tasks where extract(YEAR from date_key) = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, year);
            ResultSet resultSet = statement.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getInt("id"));
                task.setDateKey(resultSet.getDate("date_key"));
                task.setProject(resultSet.getString("project"));
                task.setDescription(resultSet.getString("description"));
                task.setHoursSpent(resultSet.getFloat("hours_spent"));
                tasks.add(task);
            }
            return tasks;
        }
    }

    @Override
    public List<Task> getAllTasksOfMonth(int year, int month) throws SQLException {
        String sql = "select * from tasks where extract(YEAR from date_key) = ? and extract(MONTH from date_key) = ?";
        try(PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setInt(1, year);
            statement.setInt(2, month);
            ResultSet resultSet = statement.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getInt("id"));
                task.setDateKey(resultSet.getDate("date_key"));
                task.setProject(resultSet.getString("project"));
                task.setDescription(resultSet.getString("description"));
                task.setHoursSpent(resultSet.getFloat("hours_spent"));
                tasks.add(task);
            }
            return tasks;
        }
    }
}
