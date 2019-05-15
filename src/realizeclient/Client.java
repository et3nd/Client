package realizeclient;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;

public class Client {
    private BufferedReader in;
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader readerFromConsol;
    private String login;

    private void closeConnection() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
        }
    }

    private void login() {
        System.out.println("Введите логин: ");
        try {
            login = readerFromConsol.readLine();
            out.write(login + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private String writeTimeMessage() {
        Calendar calendar = Calendar.getInstance();
        int dateOfMonth = calendar.get(Calendar.MONTH) + 1;
        return calendar.get(Calendar.DATE) + "." +
                dateOfMonth + "." +
                calendar.get(Calendar.YEAR) + " " +
                calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE);
    }

    private void clientWork() {
        try {
            clientSocket = new Socket("localhost", 29598);
        } catch (IOException e) {
            System.out.println("Не законнектилось");
        }
        try {
            readerFromConsol = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            login();
            writeMessageToServer.start();
            readMessageFromServer.start();
        } catch (IOException e) {
            closeConnection();
        } catch (NullPointerException ignored) {
        }
    }

    private Thread writeMessageToServer = new Thread(() -> {
        try {
            while (true) {
                String outgoingMessage = readerFromConsol.readLine();
                if (outgoingMessage.equals("Exit")) {
                    out.write(outgoingMessage + "\n");
                    closeConnection();
                    break;
                } else {
                    out.write("(" + writeTimeMessage() + ") " + login + ": " + outgoingMessage + "\n");
                }
                out.flush();
            }
        } catch (IOException e) {
            closeConnection();
        }
    });

    private Thread readMessageFromServer = new Thread(() -> {
        try {
            while (true) {
                String incomingMessage = in.readLine();
                if (incomingMessage.equals("Отказ")) {
                    throw new IncorrectLoginException("Имя пользователя уже занято. Для выхода введите любое сообщение: ");
                }
                if (incomingMessage.equals("Exit")) {
                    closeConnection();
                    break;
                }
                System.out.println(incomingMessage);
            }
        } catch (IncorrectLoginException e) {
            System.out.println(e.getMessage());
            closeConnection();
        } catch (IOException e) {
            closeConnection();
        } catch (NullPointerException ignored) {
        }
    });

    public void loader() {
        System.out.println("Вас приветствует ЖМЫХ-чат. Чтобы завершить работу, введите \"Exit\"");
        clientWork();
    }

}
