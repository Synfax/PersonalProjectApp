package com.example.paul.project.datatypes;

import java.io.Serializable;

/**
 * Created by Paul on 1/22/18.
 */

public class Subject implements Serializable{

    String name = "";
    String teacher = "";
    String room = "";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getAll() {
        return "Name: " + name + "\n" + "Teacher: " + teacher + "\n" + "Room: " + room + "\n";
    }

    public Subject() {

    }

}
