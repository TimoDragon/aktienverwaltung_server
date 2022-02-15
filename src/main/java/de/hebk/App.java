package de.hebk;

import java.io.File;

public class App {
    public static void main(String[] args) {
        String dir = "/home/" + System.getProperty("user.name") + "/Dokumente/Aktienverwaltung/data/";
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

        Server server = new Server();
    }
}