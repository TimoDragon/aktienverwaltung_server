package de.hebk;

import java.io.File;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Server");

        String dir = "./Aktienverwaltung/data/";

        File accounts = new File(dir + "users/");
        File stocks = new File(dir + "stocks/");

        if (!accounts.exists()) {
            accounts.mkdirs();
            System.out.println("Created Accounts Folder");
        }
        if (!stocks.exists()) {
            stocks.mkdirs();
            System.out.println("Created Stocks Folder");
        }

        Server server = new Server(dir);
    }
}