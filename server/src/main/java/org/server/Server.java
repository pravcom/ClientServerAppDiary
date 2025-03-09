package org.server;

import lombok.Getter;
import lombok.Setter;
import org.exchange.Clients;
import org.exchange.task.Task;
import org.flywaydb.core.Flyway;
import org.server.config.Config;
import org.server.dao.TasksDAO;
import org.server.dao.TasksDAOImpl;
import org.server.handler.ServerHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class Server {
    private HashMap<Clients,ServerHandler> clients = new HashMap<>();
//    private final List<List<Integer>> daysInYear = new ArrayList<>();
    private final LocalDate currDate = LocalDate.now();
    private List<Task> tasks = new ArrayList<>();

    public void start() {
        ServerSocket srvSocket = null;
        ServerHandler serverHandler;
        try {
            try {
                // Подключение сокета к localhost
                InetAddress inetAddress = InetAddress.getLocalHost();
                srvSocket = new ServerSocket(8080, 0, inetAddress);
                System.out.println("Server started\n\n");

                //Загрузка конфигурации
                Config config = new Config();

                // Подключение к БД
                Connection connection = DriverManager
                        .getConnection(config.getDbUrl(),config.getDbUser(),config.getDbPassword());
                TasksDAO tasksDAO = new TasksDAOImpl(connection);

                //Миграция БД с Flyway
                Flyway flyway = Flyway.configure()
                        .dataSource("jdbc:postgresql://localhost:8090/diary","admin","admin")
                        .load();
                flyway.migrate(); //Запуск миграции

                while (true) {
                    // ожидание подключения
                    Socket socket = srvSocket.accept();
                    System.err.println("Client accepted");
                    // Стартуем обработку клиента
                    // в отдельном потоке
                    serverHandler = new ServerHandler(this, tasksDAO);
                    serverHandler.setSocket(socket);
                    new Thread(serverHandler).start();
                }
            } catch (Exception e) {
                System.out.println("Exception : " + e);
            }
        } finally {
            try {
                if (srvSocket != null)
                    srvSocket.close();
                System.out.println(clients.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

//    public void setDaysInYear(List<List<Integer>> calendarDays) {
//        daysInYear.clear();
//        daysInYear.addAll(calendarDays);
//    }
}
