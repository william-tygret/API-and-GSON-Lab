package com.example.myapplication;

/**
 * Created by williamtygret on 3/1/16.
 */
public class MarvelCharacter {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
