package com.software.codetime.main;

public class Activator {

    private static Activator instance = null;

    public static Activator getInstance() {
        if (instance == null) {
            instance = new Activator();
        }
        return instance;
    }

    private Activator() {
        init();
    }

    private void init() {
        System.out.println("Initializing the plugin");
    }
}
