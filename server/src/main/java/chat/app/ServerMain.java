package chat.app;

import java.util.Scanner;

public class ServerMain {

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Введите 1 для запуска сервера");
        String choise = reader.nextLine();
        if (choise.equals("1")) {
            new Server().run();
        }
    }
}
