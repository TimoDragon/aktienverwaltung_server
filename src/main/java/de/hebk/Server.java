package de.hebk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Server {
    Gson gson;
    String dir = "";

    public Server(String dir) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        
        this.dir = dir;

        gson = new Gson();

        cmdInput.start();
        generateStockValues.start();

        try {
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }

            System.out.println("New login");
            new EchoThread(socket).start();
        }
    }

    //this Thread generate new Values for the Stockmarket
    Thread generateStockValues = new Thread(new Runnable() {
        public void run() {
            Random rand = new Random();
            BufferedReader reader;
            BufferedWriter writer;

            try {
                Thread.sleep(10 * 60 * 1000);
            } catch (InterruptedException e) {
            }

            while(true) {
                File[] files = new File("./Aktienverwaltung/data/stocks/").listFiles();

                if (files.length > 0) {
                    for (File file : files) {
                        try {
                            reader = new BufferedReader(new FileReader(file));
                            Stock stock = gson.fromJson(reader.readLine(), Stock.class);

                            int lastValue = stock.getValues()[stock.getValues().length];
                            int newValue = rand.nextInt(20) - 10;

                            stock.addValue(lastValue + newValue);

                            writer = new BufferedWriter(new FileWriter(file));
                            writer.write(gson.toJson(stock));
                            
                            reader.close();
                            writer.close();

                            System.out.println("Updated Stock value from " + stock.getName() + " to " + lastValue + newValue);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    });

    //this Thread listens for Command Input
    Thread cmdInput = new Thread(new Runnable() {
        public void run()  {
            Scanner sc = new Scanner(System.in);

            while(true) {
                String[] input = sc.nextLine().split(" ");

                if (input[0].toLowerCase().equals("stock") || input[0].toLowerCase().equals("stocks")) {
                    //add a stock
                    if (input[1].toLowerCase().equals("add") && input.length == 4) {
                        String stockName = input[2];
                        Integer[] stockValue = { null };
                        stockValue[0] = Integer.parseInt(input[3]);

                        Stock stock = new Stock(stockName, stockValue);

                        //saves the new stock in a file
                        File file = new File(dir + "/stocks/" + stockName + ".json");
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        BufferedWriter writer;
                        try {
                            writer = new BufferedWriter(new FileWriter(file));
                            writer.write(gson.toJson(stock));
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        System.out.println("Created new Stock");
                    }
                    else if (input[1].toLowerCase().equals("remove") && input.length == 3) {
                        String stockName = input[2];

                        File file = new File(dir + "/stocks/" + stockName + ".json");

                        if (file.exists()) {
                            file.delete();
                            System.out.println("Die Aktie '" + stockName + "' wurde gelöscht");
                        }
                        else {
                            System.out.println("Die Aktie exestiert nicht");
                        }
                    }
                    else if (input[1].toLowerCase().equals("listvalues") && input.length == 3) {
                        String stockName = input[2];

                        File file = new File(dir + "/stocks/" + stockName + ".json");
                        String contents = "";
                        try {
                            contents = new BufferedReader(new FileReader(file)).readLine();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Stock stock = gson.fromJson(contents, Stock.class);
                        for (int value : stock.getValues()) {
                            System.out.println(value + " €");
                        }
                    }
                }
            }
        }
    });
}