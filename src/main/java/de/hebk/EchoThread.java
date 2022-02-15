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
    private User user;
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
                Message message = gson.fromJson(line, Message.class);
                
                if (message.getType().equals("login")) {
                    String username = message.getMessage()[0];
                    String hashedPassword = message.getMessage()[1];

                    if (login(username, hashedPassword)) {
                        

                        String[] response = { "successfull" };
                        String msg = gson.toJson(new Message("login", response));

                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                    else {

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private boolean login(String username, String hashedPassword) {
        File userFile = getUserfile(username);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(userFile));
            String content = reader.readLine();

            User user = gson.fromJson(content, User.class);
            
            return user.getHashedPassword() == hashedPassword;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public File getUserfile(String username) {
        File[] files = new File("/home/" + System.getProperty("user.name") + "/Dokumente/Aktienverwaltung/data/users/").listFiles();

        for (File file : files) {
            if (file.getName().equals(username + ".json")) {
                return file;
            }
        }

        return null;
    }
}