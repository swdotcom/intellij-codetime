package com.software.codetime.managers;

import swdc.java.ops.manager.AsyncManager;
import swdc.java.ops.model.User;

public class EndOfDayManager {

    private static final String eod_msg = "It's the end of your work day! Would you like to see your code time stats for today?";
    private static final int ONE_MIN_SEC = 60 * 60;

    public static void setEndOfDayNotification(User softwareUser) {

        int delayInHours = 5;
        int delayInSeconds = ONE_MIN_SEC * delayInHours;
        AsyncManager.getInstance().executeOnceInSeconds(() -> showEndOfDayNotification(), delayInSeconds);
    }

    private static void showEndOfDayNotification() {

    }
}
