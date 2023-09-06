package resources;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger { //Класс логгера - синглтона
    private static Logger INSTANCE = null; //Ленивая реализация

    private Logger() { //Приватный конструктор, никто не сможет создавать объекты логгера через new Logger();
    }

    public static Logger getInstance() { //Позволит создать логгер в единственном экземпляре
        if (INSTANCE == null) {
            synchronized (Logger.class) {
                if (INSTANCE == null) { //Вторая проверка, чтобы не попасть в ловушку многопоточки и не создать два логгера
                    INSTANCE = new Logger();
                }
            }
        }
        return INSTANCE;
    }

    public void log(String logPath, String userName, String msg) { //Метод логирования в файл с указанием имени и текста сообщения
        String log = LocalDateTime.now() + "[" + userName + "]: " + msg + "\n";
        try (FileWriter logWriter = new FileWriter(logPath, true)) {
            logWriter.write(log);
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
