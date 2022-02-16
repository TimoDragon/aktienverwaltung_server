package de.hebk;

public class Stock {
    private Integer[] values;
    private String name;

    public Stock(String name, Integer[] values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Integer[] getValues() {
        return values;
    }

    public void setValues(Integer[] values) {
        this.values = values;
    }

    public void addValue(int value) {
        this.values[values.length] = value;
    }
}