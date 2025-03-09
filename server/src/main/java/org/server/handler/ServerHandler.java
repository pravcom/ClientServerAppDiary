package org.server.handler;

import lombok.Setter;
import org.exchange.Action;
import org.exchange.Clients;
import org.exchange.Commands;
import org.exchange.MessageExchange;
import org.exchange.calendar.ActionCalendar;
import org.exchange.calendar.ExchangeCalendar;
import org.exchange.task.ActionTask;
import org.exchange.task.ExchangeTask;
import org.exchange.task.Task;
import org.server.Server;
import org.server.dao.TasksDAO;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ServerHandler implements Runnable {
    @Setter
    private Socket socket;
    @Setter
    private Server server;
    private ObjectInputStream oin;
    private ObjectOutputStream oout;
    private TasksDAO tasksDAO;

    public ServerHandler(Server server) {
        this.server = server;
    }
    public ServerHandler(Server server, TasksDAO tasksDAO) {
        this.server = server;
        this.tasksDAO = tasksDAO;
    }

    @Override
    public void run() {
        try {
            // Определяем входной и выходной потоки сокета
            oin = new ObjectInputStream(socket.getInputStream());
            oout = new ObjectOutputStream(socket.getOutputStream());

            while (true) {
                MessageExchange me = (MessageExchange) oin.readObject();
                switch (me.getCommands()) {
                    case CONNECT -> {
                        Clients message = (Clients) me.getMessage();
                        server.getClients().put(message, this);
                        System.out.println(message + " - Connected");

                        if (message.equals(Clients.CLIENT)) {
//                            server.getClients().get(Clients.CALENDAR).sendCalendar();
//                            while (server.getDaysInYear().isEmpty()) {
//                                Thread.sleep(1000);
//                                System.out.println("Get days is empty ");
//                            }
//                            System.out.println(server.getDaysInYear());

                            //Получаем записи из БД за текущий год
                            List<Task> tasks = getTasksOfMonth(server.getCurrDate());
                            oout.writeObject(new ActionCalendar(Commands.CONNECT, new ExchangeCalendar(server.getCurrDate(), tasks)));
                            oout.flush();

                        }

                    }
                    case DISCONNECT -> {
                        System.out.println("Disconnected");
                        oout.writeObject(new Action(Commands.DISCONNECT, Clients.CLIENT));
                        oout.flush();
                        close((Clients) me.getMessage());
                    }
                    case CALENDAR -> {
                        //Получаем данные сервиса календаря на сервер
                        //ОТКЛЮЧЕНО
//                        getCalendar(me);
                    }
                    case LISTOFCLIENTS -> {
                        System.out.println(server.getClients().toString());
                    }
                    case UPDATE_DIARY -> {
                        org.server.entity.Task taskDB = getTaskDB((ActionTask) me);

                        //Добавляем запись Tasks в бд
                        tasksDAO.addTask(taskDB);
                        System.out.println("Add to Tasks: " + taskDB.toString());
                        List<Task> tasks = getTasksOfYear(taskDB.getDateKey().toLocalDate().getYear());
                        oout.writeObject(new ActionTask(Commands.UPDATE_DIARY,new ExchangeTask(tasks)));
                        oout.flush();
                    }
                    case DELETE_TASK -> {
                        org.server.entity.Task taskDB = getTaskDB((ActionTask) me);
                        //Удаляем запись Task в БД
                        tasksDAO.deleteTask(taskDB);
                        System.out.println("Delete from Tasks: " + taskDB.toString());

                        List<Task> tasks = getTasksOfYear(taskDB.getDateKey().toLocalDate().getYear());
                        oout.writeObject(new ActionTask(Commands.DELETE_TASK,new ExchangeTask(tasks)));
                        oout.flush();
                    }
                    case PREVIOS -> {

                    }
                    case GET_DATA_MONTH -> {
                        ActionCalendar actionCalendar = (ActionCalendar) me;
                        LocalDate date = actionCalendar.getMessage().getDate();
                        List<Task> taskList = getTasksOfMonth(date);

                        oout.writeObject(new ActionTask(Commands.GET_DATA_MONTH,new ExchangeTask(taskList)));
                        oout.flush();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            throw new RuntimeException(e);
        } finally {

        }
    }

    private static org.server.entity.Task getTaskDB(ActionTask me) {
        Task task = me.getMessage().getTask();
        org.server.entity.Task taskDB = new org.server.entity.Task();

        //Перекладываем из Tasks Exchange в Tasks Entity
        taskDB.setId(task.getId());
        taskDB.setDateKey(task.getDate_key());
        taskDB.setProject(task.getProject());
        taskDB.setDescription(task.getDescription());
        taskDB.setHoursSpent(task.getHoursSpent());
        return taskDB;
    }

//    public void sendCalendar() throws IOException, ClassNotFoundException {
//        ExchangeCalendar exCalendar = new ExchangeCalendar(server.getCurrDate());
//        oout.writeObject(new ActionCalendar(Commands.CALENDAR, exCalendar));
//        oout.flush();
//    }

//    public void getCalendar(MessageExchange me) throws IOException, ClassNotFoundException {
//        ActionCalendar actionCalendar = (ActionCalendar) me;
//        List<List<Integer>> calendarDays = actionCalendar.getMessage().getDaysOfMonth();
//        server.setDaysInYear(calendarDays);
//    }

    public void close(Clients msg) throws IOException {
        server.getClients().remove(msg);
        socket.close();
        oout.close();
        oin.close();
    }

    private List<Task> getTasksOfYear(int year) {
        try {
            List<org.server.entity.Task> taskDB = tasksDAO.getAllTasksOfYear(year);
            return taskDB.stream().map(task -> {
                Task taskExchange = new Task();
                taskExchange.setId(task.getId());
                taskExchange.setDate_key(task.getDateKey());
                taskExchange.setProject(task.getProject());
                taskExchange.setDescription(task.getDescription());
                taskExchange.setHoursSpent(task.getHoursSpent());
                return taskExchange;
            }).toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Task> getTasksOfMonth(LocalDate date) {
        try {
            int month = date.getMonthValue();
            int year = date.getYear();
            List<org.server.entity.Task> tasksDB = tasksDAO.getAllTasksOfMonth(year,month);
            return tasksDB.stream().map(task -> {
                Task taskExchange = new Task();
                taskExchange.setId(task.getId());
                taskExchange.setDate_key(task.getDateKey());
                taskExchange.setProject(task.getProject());
                taskExchange.setDescription(task.getDescription());
                taskExchange.setHoursSpent(task.getHoursSpent());
                return taskExchange;
            }).toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
