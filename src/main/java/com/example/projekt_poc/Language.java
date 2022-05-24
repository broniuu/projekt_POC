package com.example.projekt_poc;

public class Language {
    public int id;
    public String name;

    public Language(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;

    }
}
