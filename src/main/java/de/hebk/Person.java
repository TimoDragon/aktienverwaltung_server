package de.hebk;

public class Person {
    private String name;
    private String birtdate;
    private String email;
    private String phonenumber;

    public Person(String name, String birtdate, String email, String phonenumber) {
        this.name = name;
        this.birtdate = birtdate;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setBirthdate(String birthdate) {
        this.birtdate = birthdate;
    }

    public String getBirthdate() {
        return this.birtdate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() {
        return this.phonenumber;
    }
}