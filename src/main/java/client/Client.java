package client;

import resources.JsonParser;
import resources.Logger;
import server.Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static String userName;
    private static final Logger LOGGER = Logger.getInstance(); //Создаем логгер
    private static final String CLIENT_LOGS = "src/main/java/resources/client_logs.log"; //Путь для логирования клиентов
    private static final String HOST = JsonParser.parseJson("host"); //Получаем адрес хоста из файла
    private static final int PORT = Integer.parseInt(JsonParser.parseJson("port")); //Получаем порт из файла

    private static final String CONNECTION_MESSAGE = "Новое подключение к серверу. Порт: %d";
    private static final String SET_USER_NAME_MESSAGE = "Добавил имя пользователя";
    private static final String SEND_MESSAGE = "Отправил сообщение: %s";
    private static final String USER_DISCONNECT_MESSAGE = "Пользователь %s отключился от сервера (Порт: %d) %n";

    private static PrintWriter out;
    private static BufferedReader in;
    private static int serverPort;
    private static final Scanner scanner = new Scanner(System.in);

    public Client() {
        try {
            Socket clientSocket = new Socket(HOST, PORT); //Подключаемся к серверу
            out = new PrintWriter(clientSocket.getOutputStream(), true); //Поток вывода
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Входной поток

            serverPort = clientSocket.getPort(); //Получаем порт сервера
            LOGGER.log(CLIENT_LOGS, Server.getServerName(), String.format(CONNECTION_MESSAGE, serverPort)); //Логируем в файл клиента

            Thread readerThread = getReaderThread(); //Создаем поток для чтения с сервера
            readerThread.start(); //Запускаем

            Thread senderThread = getSenderThread(); //Создаем поток для отправки на сервер
            senderThread.start(); //Запускаем

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setUserName(String userName) {
        if (Client.userName == null) {
            Client.userName = userName;
        }
    }

    public static String getUserName() {
        return userName;
    }

    public static Thread getReaderThread() { //Поток для чтения с сервера
        return new Thread(() -> {
            String msg;
            while (!Thread.currentThread().isInterrupted()) { //Пока поток не прервали
                try {
                    msg = in.readLine(); //Читаем с сервера
                    System.out.println(msg);
                    if (msg.equals("Disconnecting from server")) { //Если сообщение о дисконнекте
                        Thread.currentThread().interrupt(); //Прерываем поток
                    }
                    Thread.sleep(500);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    return; //Если прервали во время сна - выходим
                }
            }
        });
    }

    public static Thread getSenderThread() { //Поток для отправки на сервер
        return new Thread(() -> {
            setUserName(scanner.nextLine()); //При первом обращении с сервером устанавливаем имя
            LOGGER.log(CLIENT_LOGS, getUserName(), SET_USER_NAME_MESSAGE);
            out.println(getUserName());

            while (!Thread.currentThread().isInterrupted()) { //Пока поток не прервали
                String msg = scanner.nextLine(); //Читаем с консоли
                out.println(msg); //Отправляем на сервер
                LOGGER.log(CLIENT_LOGS, getUserName(), String.format(SEND_MESSAGE, msg));

                if (msg.equalsIgnoreCase("/exit")) { //Если ввели '/exit'
                    LOGGER.log(CLIENT_LOGS, Server.getServerName(), String.format(USER_DISCONNECT_MESSAGE, userName, serverPort));
                    Thread.currentThread().interrupt(); //Прерываем поток
                }
            }
        });
    }
}
