package com.example.adamo.cooksterapp;

/**
 * Created by adamo on 01.03.2018.
 */

public class EmployeeModel {
    private String name;
    private String id;
    private int resid;

    public EmployeeModel(String name, String id) {
        this.name = name;
        this.id = id;
    }
    public EmployeeModel(String name, int id) {
        this.name = name;
        this.resid = id;
    }
    public String getName() {
        return name;
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
