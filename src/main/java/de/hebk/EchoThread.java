package de.hebk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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
                
                if (user == null) {
                    if (message[0].equals("login")) {
                        String username = message[1];
                        String hashedPassword = message[2];
    
                        if (userExists(username, hashedPassword)) {
                            File userFile = getUserfile(username);
                            BufferedReader reader = new BufferedReader(new FileReader(userFile));
                            user = gson.fromJson(reader.readLine(), User.class);
    
                            String[] response = { "successfull" };
                            String msg = gson.toJson(response);
    
                            bufferedWriter.write(msg);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
    
                            reader.close();
                        }
                        else {
    
                        }
                    }
                    else if (message[0].equals("register")) {
                        String userName = message[1];
                        String hashedPasword = message[2];
                    }
                }
                else if (user != null) {
                    if (message[0].equals("getstocknames")) {
                        BufferedReader reader;
                        File[] files = new File("./Aktienverwaltung/data/stocks/").listFiles();
                
                        String[] stocks = new String[files.length];
                        stocks[0] = "getstocknames";
                        for (int i = 0; i < files.length; i++) {
                            try {
                                reader = new BufferedReader(new FileReader(files[i]));
                                String stockName = reader.readLine();
                                String stock = gson.fromJson(stockName, Stock.class).getName();
                                stocks[i + 1] = stock;
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }
                
                        bufferedWriter.write(gson.toJson(stocks));
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    else if (message[0].equals("getstock")) {
                        String stockName = message[1];
    
                        String stockJson = "";
                        boolean checker = false;
                        File[] files = new File("./Aktienverwaltung/data/stocks/").listFiles();
                        for (File file : files) {
                            if (file.getName().equals(stockName + ".json")) {
                                checker = true;
                                
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                stockJson = reader.readLine();
                            }
                        }
    
                        String[] msgString = { null, null };
                        if (checker == true) {
                            msgString[0] = "getstock";
                            msgString[1] = stockJson;
                        }
                        else {
                            msgString[0] = "getstock";
                            msgString[1] = "error";
                        }

                        bufferedWriter.write(gson.toJson(msgString));
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
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