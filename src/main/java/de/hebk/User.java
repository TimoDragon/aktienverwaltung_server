package de.hebk;

public class User extends Person {
    private String username;
    private String hashedPassword;
    private Stock[] stocks;
    
    public User(String name, String birthdate, String email, String phonenumber, String username, String hashedPassword) {
        super(name, birthdate, email, phonenumber);
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Stock[] getStocks() {
        return stocks;
    }

    public void setStocks(Stock[] stocks) {
        this.stocks = stocks;
    }

    public void addStock(int length, Stock[] oldArray, Stock stock) {
        Stock[] newStocks = new Stock[length + 1];
        for (int i = 0; i < length; i++) {
            newStocks[i] = oldArray[i];
        }
        newStocks[length] = stock;

        this.stocks = newStocks;
    }
}