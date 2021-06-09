package com.software.codetime.managers;

import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.manager.UtilManager;

import java.util.logging.Logger;

public class WallClockManager {

    public static final Logger LOG = Logger.getLogger("WallClockManager");

    private static final int SECONDS_INCREMENT = 30;

    private static WallClockManager instance = null;

    public static WallClockManager getInstance() {
        if (instance == null) {
            synchronized (LOG) {
                if (instance == null) {
                    instance = new WallClockManager();
                }
            }
        }
        return instance;
    }

    private WallClockManager() {
        //
    }

    public void newDayChecker() {
        if (UtilManager.isNewDay()) {
            // clear the wc time and the session summary and the file change info summary
            SessionDataManager.clearSessionSummaryData();
            TimeDataManager.clearTimeDataSummary();
            FileAggregateDataManager.clearFileChangeInfoSummaryData();

            // update the current day
            String day = UtilManager.getTodayInStandardFormat();
            FileUtilManager.setItem("currentDay", day);

            // update the last payload timestamp
            FileUtilManager.setNumericItem("latestPayloadTimestampEndUtc", 0);
        }
    }
}
