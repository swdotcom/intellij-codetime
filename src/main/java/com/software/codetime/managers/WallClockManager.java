package com.software.codetime.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.listeners.CodeTimeProcessor;
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

    }

    public void newDayChecker() {
        if (UtilManager.isNewDay()) {
            // clear the wc time and the session summary and the file change info summary
            clearWcTime();
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

    private void updateWallClockTime() {
        // pass control from a background thread to the event dispatch thread,
        ApplicationManager.getApplication().invokeLater(() -> {
            boolean isActive = ApplicationManager.getApplication().isActive();
            if (isActive && CodeTimeProcessor.isCurrentlyActive) {
                long wctime = getWcTimeInSeconds() + SECONDS_INCREMENT;
                FileUtilManager.setNumericItem("wctime", wctime);

                // update the json time data file
                TimeDataManager.incrementEditorSeconds(SECONDS_INCREMENT);
            }
        });
    }

    private void clearWcTime() {
        setWcTime(0);
    }

    public long getWcTimeInSeconds() {
        return FileUtilManager.getNumericItem("wctime", 0L);
    }

    public void setWcTime(long seconds) {
        FileUtilManager.setNumericItem("wctime", seconds);
        updateWallClockTime();
    }

}
