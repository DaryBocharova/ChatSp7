package chat.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    BlockingQueue<Connection> connectionsArrayList = new LinkedBlockingQueue<Connection>();

    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(777);
            System.out.println("Серверный сокет создан");

            do {
                Socket newSocket = serverSocket.accept();
                Connection connection = new Connection(newSocket);
                Thread thread = new Thread(connection);
                thread.start();
                connectionsArrayList.add(connection);

            } while (!connectionsArrayList.isEmpty());
            serverSocket.close();
            System.out.println("Сервер настроет на автоотлючение при  потере последнего клиента");

        } catch (IOException e) {
            System.out.println("Сервер не запустился на нужном порту");
        }

    }

    class Connection implements Runnable {

        Socket socket;
        String name;

        public Connection(Socket socket) {
            this.socket = socket;

        }

        @Override
        public void run() {

            try (InputStream inputStream = socket.getInputStream()) {

                Scanner scanner = new Scanner(inputStream, "utf-8");
                String message;
                this.name = scanner.nextLine();
                message = "Всем привет :)";
                sendBroadcast(message);

                while (socket.isConnected()) {

                    message = scanner.nextLine();
                    if ("exit".equals(message)) {
                        System.out.println(this.name + " покидает нас.");
                        message =  "отключился.";
                        sendBroadcast(message);
                        connectionsArrayList.remove(this);
                        break;
                    }
                    System.out.println("Новое сообщение от " + this.name + ": " + message);
                    sendBroadcast(message);

                }

            } catch (IOException e) {
                System.out.println(Thread.currentThread().toString() + " не инициализировался ");
            }

            System.out.println(Thread.currentThread() + " Потерял своего клиента");
        }

        private void sendBroadcast(String message) throws IOException {
            for (Connection connection : connectionsArrayList) {

                if (connection.equals(this)) continue;

                if (connection.socket.isConnected()) {
                    Writer writer = new OutputStreamWriter(connection.socket.getOutputStream(), "utf-8");
                    writer.write(this.name + ": " + message + "\n");
                    writer.flush();
                } else {
                    System.out.println("Клиент " + connection.name + " не доступен");
                }

            }
        }
    }
}
