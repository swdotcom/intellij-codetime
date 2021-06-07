package com.software.codetime.main;

import swdc.java.ops.manager.AccountManager;

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
        AccountManager.getUser();
    }
}
