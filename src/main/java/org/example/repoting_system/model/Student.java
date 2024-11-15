package org.example.repoting_system.model;

public class Student {
    private int id;
    private String name;
    private String program;
    private String email;

    public Student(int id, String name, String program, String email) {
        this.id = id;
        this.name = name;
        this.program = program;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
