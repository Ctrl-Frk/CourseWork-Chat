package server;

import resources.JsonParser;
import resources.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server {
    private static final String SERVER_NAME = "SERVER"; //Имя сервера
    private static final Logger LOGGER = Logger.getInstance(); //Создаем логгер
    private static final String SERVER_LOGS = "src/main/java/resources/server_logs.log"; //Путь для логирования сервера
    private static final int PORT = Integer.parseInt(Objects.requireNonNull(JsonParser.parseJson("port"))); //Получаем порт из файла

    private static final String STARTING_MESSAGE = "Server is running. Waiting for connections"; //Сообщение о старте сервера
    private static final String NEW_CONNECTION_MESSAGE = "New connection accepted. Port: %d %n"; //Сообщение о новом подключении
    private static final String NEW_USER_MESSAGE = "New user on server. Port: %d"; //Сообщение о новом пользователе (после добавлении имени)
    private static final String USER_DISCONNECT_MESSAGE = "User %s (Port: %d) was disconnected %n"; //Сообщение о дисконнекте

    public static String getServerName() {
        return SERVER_NAME;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT); //Создаем сервер сокет
            System.out.println(STARTING_MESSAGE);
            LOGGER.log(SERVER_LOGS, SERVER_NAME, STARTING_MESSAGE); //Логируем в файл сервера

            while (true) { //Ждем подключения в бесконечном цикле
                Socket clientSocket = serverSocket.accept(); //Подключаем клиента
                Thread newClientThread = getNewClientThread(clientSocket); //Для каждого нового клиента создаем отдельный поток
                newClientThread.start(); //Запускаем поток клиента, а основной поток ждет нового клиента
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Thread getNewClientThread(Socket clientSocket) {
        return new Thread(() -> {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); //Поток вывода
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) //Входной поток
            ) {
                int clientPort = clientSocket.getPort(); //Получаем порт клиента
                System.out.printf(NEW_CONNECTION_MESSAGE, clientPort);
                LOGGER.log(SERVER_LOGS, SERVER_NAME, String.format(NEW_CONNECTION_MESSAGE, clientPort));
                out.println("Connection successful! Write your name");

                final String USER_NAME = in.readLine(); //Получаем имя клиента
                out.printf("Hi, %s, your port: %d", USER_NAME, clientPort);
                LOGGER.log(SERVER_LOGS, USER_NAME, String.format(NEW_USER_MESSAGE, clientPort));

                while (!clientSocket.isClosed()) { //Пока сокет клиента работает
                    out.println("\nPrint message for logging. Print to exit: /exit");

                    String msgFromClient = in.readLine(); //Получаем сообщения
                    LOGGER.log(SERVER_LOGS, USER_NAME, ("Send message: " + msgFromClient)); //И логируем

                    if (msgFromClient.equalsIgnoreCase("/exit")) { //Если клиент ввел '/exit'
                        out.println("Disconnecting from server"); //Отдаем сообщение о дисконнекте
                        in.close(); //Закрываем входной поток
                        out.close(); //Закрываем поток вывода
                        clientSocket.close(); //Закрываем сокет клиента
                        LOGGER.log(SERVER_LOGS, SERVER_NAME, String.format(USER_DISCONNECT_MESSAGE, USER_NAME, clientPort));
                        break;
                    }

                    out.println("Saved message: " + msgFromClient);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}