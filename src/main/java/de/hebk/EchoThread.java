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
    private User user = null;
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
                    File[] usersFiles = new File(dir + "users/").listFiles();
                    User[] users = new User[usersFiles.length];

                    BufferedReader reader;

                    for (int i = 0; i < usersFiles.length; i++) {
                        reader = new BufferedReader(new FileReader(usersFiles[i]));
                        users[i] = gson.fromJson(reader.readLine(), User.class);
                    }

                    String usersString = gson.toJson(users);

                    bufferedWriter.write(usersString);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                else if (message[0].equals("getstocks") || message[0].equals("getstonks")) {
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
                else if (message[0].equals("adduser") && message.length > 2) {
                    User newUser = gson.fromJson(message[1], User.class);

                    File newUserFile = new File(dir + "users/" + newUser.getUsername() + ".json");

                    if (!newUserFile.exists()) {
                        newUserFile.createNewFile();

                        BufferedWriter writer = new BufferedWriter(new FileWriter(newUserFile));
                        writer.write(message[1]);
                        writer.close();

                        System.out.println("Added new user '" + newUser.getUsername() + "'");
                    }
                }
                else if (message[0].equals("updateuser") && message.length > 2) {
                    User user = gson.fromJson(message[1], User.class);
                    File userFile = new File(dir + "users/" + user.getUsername() + ".json");

                    BufferedWriter writer = new BufferedWriter(new FileWriter(userFile));
                    writer.write(message[1]);
                    writer.close();

                    System.out.println("Updated user '" + user.getUsername() + "'");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private boolean userExists(String username, String hashedPassword) {
        File userFile = getUserfile(username);

        if (userFile != null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(userFile));
                String content = reader.readLine();
    
                User user = gson.fromJson(content, User.class);
                reader.close();
                
                return user.getHashedPassword() == hashedPassword;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public File getUserfile(String username) {
        File[] files = new File("./Aktienverwaltung/data/users/").listFiles();

        for (File file : files) {
            if (file.getName().equals(username + ".json")) {
                return file;
            }
        }

        return null;
    }
}