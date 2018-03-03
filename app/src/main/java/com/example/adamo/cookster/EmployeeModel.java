package com.example.adamo.cookster;

/**
 * Created by adamo on 01.03.2018.
 */

class EmployeeModel {
    private String name;
    private int id;

    protected EmployeeModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return name;
    }
}
