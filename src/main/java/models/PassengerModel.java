package models;

import abstractions.Model;

public class PassengerModel implements Model {
    public int passengerId;
    public boolean survived;
    public int pclass;
    public String name;
    public String sex;
    public int age;
    public int sibSp;
    public int parch;
    public String ticket;
    public float fare;
    public String cabin;
    public String embarked;
}
