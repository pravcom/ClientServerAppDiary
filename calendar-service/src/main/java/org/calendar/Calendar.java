package org.calendar;

import org.calendar.server.ServerCalendar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Calendar {
    public static void main(String[] args) {
        ServerCalendar serverCalendar = new ServerCalendar();
    }
}