package org.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import lombok.Setter;
import org.client.utils.Day;
import org.client.utils.ElementCalendar;
import org.exchange.Action;
import org.exchange.Clients;
import org.exchange.Commands;
import org.exchange.MessageExchange;
import org.exchange.calendar.ActionCalendar;
import org.exchange.calendar.ExchangeCalendar;
import org.exchange.task.ActionTask;
import org.exchange.task.ExchangeTask;
import org.exchange.task.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class MainController {
    @FXML
    public Label monthText;
    public VBox mainVbox;
    public VBox calendarMainVbox;
    Socket socket = null;
    private ObjectOutputStream sout;
    private ObjectInputStream sin;
//    private List<List<Integer>> daysInYear;
    private LocalDate currDate;
    private final HashMap<Integer, List<Day>> calendarFormated = new HashMap<>();
    private final List<Task> listTasks = new ArrayList<>();
    @Setter
    private FieldViewController fieldViewController;


    public void connect() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            socket = new Socket(ip, 8080);
            // Получаем входной и выходной потоки
            // сокета для обмена сообщениями с сервером
            MessageExchange messageExchange = new Action(Commands.CONNECT, Clients.CLIENT);
            sout = new ObjectOutputStream(socket.getOutputStream());
            sin = new ObjectInputStream(socket.getInputStream());

            sout.writeObject(messageExchange);
            sout.flush();
            //Получаем от сервера данные календаря
            ActionCalendar inputMsg = (ActionCalendar) sin.readObject();
//            daysInYear = inputMsg.getMessage().getDaysOfMonth();
            currDate = inputMsg.getMessage().getDate();
            listTasks.addAll(inputMsg.getMessage().getTasks());

            System.out.println(inputMsg.getCommands());
//            System.out.println("Days in year: " + daysInYear.toString());

            int month = LocalDate.now().getMonth().getValue();
            fillCalendar(month);
            drawCalendar(month);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            MessageExchange messageExchange = new Action(Commands.DISCONNECT, Clients.CLIENT);
            sout.writeObject(messageExchange);
            sout.flush();

            Action inputMsg = (Action) sin.readObject();
            System.out.println(inputMsg.getCommands());

            socket.close();
            //Чистим экран с отрисовкой календаря
            clearCalendar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    public void onPrev() {
        currDate = currDate.minusMonths(1);
        changeDate(currDate);
    }

    private void changeDate(LocalDate date) {
        try {
            MessageExchange me = new ActionCalendar(Commands.GET_DATA_MONTH, new ExchangeCalendar(currDate));
            sout.writeObject(me);
            sout.flush();

            ActionTask inputMsg = (ActionTask) sin.readObject();
            listTasks.clear();
            listTasks.addAll(inputMsg.getMessage().getTaskList());
            int month = date.getMonth().getValue();
            clearCalendar();
            fillCalendar(month);
            drawCalendar(month);
        }catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void onNext() {
        currDate = currDate.plusMonths(1);
        changeDate(currDate);
    }

    public void clientList(ActionEvent actionEvent) {
        try {
            MessageExchange messageExchange = new Action(Commands.LISTOFCLIENTS, Clients.CLIENT);
            sout.writeObject(messageExchange);
            sout.flush();

        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void fillCalendar(int month) {
            List<Day> dayList = new ArrayList<>();
            for (int j = 0; j < currDate.lengthOfMonth(); j++) {
                LocalDate date = LocalDate.of(currDate.getYear(), month, j + 1);

                // Используем Optional для того чтобы исключить нулевые записи
                Optional<List<Task>> tasksOfDate = Optional.of(listTasks.stream().filter(t -> {
                    LocalDate dateTask = t.getDate_key().toLocalDate();
                    return dateTask.equals(date);
                }).toList());

                Day day = new Day(date, tasksOfDate.orElse(null));
                dayList.add(day);
            }
            calendarFormated.put(month, dayList);
    }

    public void drawCalendar(int month) {
        double calendarWidth = calendarMainVbox.getPrefWidth();
        double calendarHeight = calendarMainVbox.getPrefHeight();
        double spacing = 10;
        double padding = 5;

        fillMonthText(month);
        ElementCalendar elementCalendar = new ElementCalendar.Builder()
                .setCalendarWidth(calendarWidth)
                .setCalendarHeight(calendarHeight)
                .setPadding(padding)
                .setSpacing(spacing)
                .setMainController(this)
                .build();

        List<Day> dayList = calendarFormated.get(month);
        HBox daysOfWeekHbox = new HBox();

        for (int i = 0; i < 7; i++) {
            // Добавляем StackPane в контейнер (например, HBox или VBox)
            StackPane stackPane = elementCalendar
                    .getField(
                            elementCalendar.getRectangle()
                            , new Label(DayOfWeek.of(i + 1).toString()));
            daysOfWeekHbox.getChildren().add(stackPane);
        }
        //Устанваливаем отступы
        daysOfWeekHbox.setSpacing(spacing);
        daysOfWeekHbox.setPadding(new Insets(padding));
        //Добавляем дни недели на экран
        calendarMainVbox.getChildren().add(daysOfWeekHbox);

        int dayNum = 0;
        boolean flag = false;
        do {
            HBox daysHbox = new HBox();
            //Цикл по дням недели
            for (int i = 0; i < 7; i++) {
                if (dayNum >= dayList.size()) {
                    flag = true;
                    break;
                }
                Label label = new Label();
                label.setText("");

                Popup popup = new Popup();
                List<Task> dayTasks = new ArrayList<>();
                Rectangle rectangle = elementCalendar.getRectangle();

                //Если день недели календаря совпал с днем недели на экране, то рисуем его
                if (dayList.get(dayNum).getDate().getDayOfWeek().equals(DayOfWeek.of(i + 1))) {
                    Day day = dayList.get(dayNum);
                    label.setText(String.valueOf(day.getDate().getDayOfMonth()));

                    //Создаем всплывающее POPUP окно
                    dayTasks = day.getTasks();
                    popup = elementCalendar.getPopup(dayTasks);

                    //Добавляем обработку события - наведение курсора на Rectangle
                    elementCalendar.setOnMouseShowPopup(rectangle, popup);
                    elementCalendar.clickOnRectangle(rectangle, day);
                    dayNum++;
                }

                StackPane stackPane = elementCalendar.getField(rectangle, label);

                //Устанваливаем отступы
                daysHbox.setSpacing(spacing);
                daysHbox.setPadding(new Insets(padding));

                // Добавляем StackPane в контейнер (например, HBox или VBox)
                daysHbox.getChildren().add(stackPane);
            }
            calendarMainVbox.getChildren().add(daysHbox);
        } while (!flag);
    }

    private void fillMonthText(int monthNum) {
        monthText.setText(Month.of(monthNum).toString());
    }

    public void clearCalendar() {
        calendarMainVbox.getChildren().clear();
        calendarFormated.clear();
    }

    public List<Task> addTasks(Task task) {
        System.out.println(task.toString());
        MessageExchange me = new ActionTask(Commands.UPDATE_DIARY, new ExchangeTask(task));
        try {
            sout.writeObject(me);
            sout.flush();

            //Получаем от сервера данные календаря
            ActionTask inputMsg = (ActionTask) sin.readObject();
            listTasks.clear();
            listTasks.addAll(inputMsg.getMessage().getTaskList());
            return listTasks;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteTasks(Task task) {
        System.out.println("Удалить запись: " + task.toString());
        MessageExchange me = new ActionTask(Commands.DELETE_TASK, new ExchangeTask(task));
        try {
            sout.writeObject(me);
            sout.flush();

            //Получаем от сервера данные календаря
            ActionTask inputMsg = (ActionTask) sin.readObject();
            listTasks.clear();
            listTasks.addAll(inputMsg.getMessage().getTaskList());
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
