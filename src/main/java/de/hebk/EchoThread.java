package de.hebk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class EchoThread extends Thread {
    private Socket socket;
    private Gson gson;

    String dir = "./Aktienverwaltung/data/";

    public EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        OutputStreamWriter outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = new OutputStreamWriter(socket.getOutputStream());
            bufferedWriter = new BufferedWriter(outputStream);   
        } catch (IOException e) {
            return;
        }

        String line;
        gson = new Gson();

        while (true) {
            try {
                line = bufferedReader.readLine();
                String[] message = gson.fromJson(line, String[].class);
                
                if (message[0].equals("getusers")) {
                    System.out.println("Sending all user to client");

                    File[] usersFiles = new File(dir + "users/").listFiles();
                    String[] users = new String[usersFiles.length];

                    BufferedReader reader;
                    for (int i = 0; i < usersFiles.length; i++) {
                        reader = new BufferedReader(new FileReader(usersFiles[i]));
                        users[i] = reader.readLine();
                    }

                    bufferedWriter.write(gson.toJson(users));
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                else if (message[0].equals("getstocks") || message[0].equals("getstonks")) {
                    System.out.println("Senden all stocks to client");

                    File[] stockFiles = new File(dir + "stocks/").listFiles();
                    Stock[] stocks = new Stock[stockFiles.length];
                    BufferedReader reader;

                    for (int i = 0; i < stockFiles.length; i++) {
                        reader = new BufferedReader(new FileReader(stockFiles[i]));
                        stocks[i] = gson.fromJson(reader.readLine(), Stock.class);
                    }

                    String stocksSting = gson.toJson(stocks);

                    bufferedWriter.write(stocksSting);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                else if (message[0].equals("adduser") && message.length > 1) {
                    System.out.println("adding new user");

                    User newUser = gson.fromJson(message[1], User.class);

                    File newUserFile = new File(dir + "users/" + newUser.getEmail() + ".json");

                    if (!newUserFile.exists()) {
                        newUserFile.createNewFile();

                        BufferedWriter writer = new BufferedWriter(new FileWriter(newUserFile));
                        writer.write(message[1]);
                        writer.close();

                        System.out.println("Added new user");
                    }
                }
                else if (message[0].equals("updateuser") && message.length > 1) {
                    User user = gson.fromJson(message[1], User.class);
                    File userFile = new File(dir + "users/" + user.getEmail() + ".json");

                    BufferedWriter writer = new BufferedWriter(new FileWriter(userFile));
                    writer.write(message[1]);
                    writer.close();

                    System.out.println("Updated user '" + user.getEmail() + "'");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}