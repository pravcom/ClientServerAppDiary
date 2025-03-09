package org.calendar.server;

import lombok.Setter;
import org.exchange.*;
import org.exchange.calendar.ActionCalendar;
import org.exchange.calendar.ExchangeCalendar;
import org.calendar.CalendarDiary;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;

public class ServerCalendar {
    @Setter
    private Socket socket = null;
    private ObjectOutputStream sout;
    private ObjectInputStream sin;

    public ServerCalendar() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 8080);
            System.out.println("Connected to: 8080");
            sout = new ObjectOutputStream(socket.getOutputStream());
            sin = new ObjectInputStream(socket.getInputStream());
            writeMessage();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeMessage() throws IOException, ClassNotFoundException {
        sout.writeObject(new Action(Commands.CONNECT, Clients.CALENDAR));
        sout.flush();

        while (true) {
            if (socket.isClosed()) {
                System.out.println("Socket is closed");
                break;
            }
            MessageExchange exchange = (MessageExchange) sin.readObject();
            switch (exchange.getCommands()) {
                case CALENDAR -> {
                    //получаем календарь от сервера и на его основе делаем CalendarDiary
                    ExchangeCalendar exchangeCalendar = (ExchangeCalendar) exchange.getMessage();
                    CalendarDiary calendarDiary = new CalendarDiary(exchangeCalendar.getDate());
                    System.out.println("Get calendar year: " + exchangeCalendar.getDate().get(Calendar.YEAR));

                    // Надо придумать какой объект вернуть обратно на сервер
                    sout.writeObject(new ActionCalendar(Commands.CALENDAR, new ExchangeCalendar(calendarDiary.getDaysOfMonth())));
                    sout.flush();
                    System.out.println("Write calendar days: " + calendarDiary.getDaysOfMonth());
                }
            }
        }
        close();
    }

    private void close() throws IOException {
        socket.close();
        sout.close();
        sin.close();
    }
}
