package chat.app;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Введите 2 для запуска сервера");
        String choise = reader.nextLine();
        if (choise.equals("2")) {
            new Client().run();
        }
    }
}
