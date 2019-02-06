package com.example.adamo.cooksterapp;

/**
 * Created by adamo on 01.03.2018.
 */

public class EmployeeModel {
    private String name, ab, id, value;




    private int resid;

    public EmployeeModel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public EmployeeModel(String name, String id, String ab) {
        this.name = name;
        this.id = id;
        this.ab = ab;
    }
    public EmployeeModel(String name, int id) {
        this.name = name;
        this.resid = id;
    }

    public EmployeeModel(String name, String ab, String id, String value) {
        this.name = name;
        this.id = id;
        this.ab = ab;
        this.value = value;
    }
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getAb() {
        return ab;
    }

    public String getId() {
        return id;
    }

    public int getResId() {
        return resid;
    }

    public String toString() {
        return name;
    }
}
