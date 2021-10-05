package com.software.codetime.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.http.ClientResponse;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.manager.UtilManager;
import swdc.java.ops.model.ElapsedTime;
import swdc.java.ops.model.SessionSummary;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionDataManager {

    public static final Logger log = Logger.getLogger("SessionDataManager");

    public static void clearSessionSummaryData() {
        SessionSummary summary = new SessionSummary();
        FileUtilManager.writeData(FileUtilManager.getSessionDataSummaryFile(), summary);
    }

    public static void updateSessionSummaryFromServer() {
        SessionSummary summary = fetchSessionSummary();
        updateFileSummaryAndStatsBar(summary);
    }

    public static SessionSummary fetchSessionSummary() {
        String jwt = FileUtilManager.getItem("jwt");
        String api = "/sessions/summary";
        ClientResponse resp = OpsHttpClient.softwareGet(api, jwt);
        if (resp.isOk()) {
            try {
                Type type = new TypeToken<SessionSummary>() {}.getType();
                return UtilManager.gson.fromJson(resp.getJsonObj(), type);
            } catch (Exception e) {
                log.log(Level.WARNING, "[CodeTime] error reading session summary: " + e.getMessage());
            }
        }
        return new SessionSummary();
    }

    public static SessionSummary getSessionSummaryData() {
        JsonObject jsonObj = FileUtilManager.getFileContentAsJson(FileUtilManager.getSessionDataSummaryFile());
        if (jsonObj == null) {
            clearSessionSummaryData();
            jsonObj = FileUtilManager.getFileContentAsJson(FileUtilManager.getSessionDataSummaryFile());
        }
        JsonElement lastUpdatedToday = jsonObj.get("lastUpdatedToday");
        if (lastUpdatedToday != null) {
            // make sure it's a boolean and not a number
            if (!lastUpdatedToday.getAsJsonPrimitive().isBoolean()) {
                // set it to boolean
                boolean newVal = lastUpdatedToday.getAsInt() != 0;
                jsonObj.addProperty("lastUpdatedToday", newVal);
            }
        }
        JsonElement inFlow = jsonObj.get("inFlow");
        if (inFlow != null) {
            // make sure it's a boolean and not a number
            if (!inFlow.getAsJsonPrimitive().isBoolean()) {
                // set it to boolean
                boolean newVal = inFlow.getAsInt() != 0;
                jsonObj.addProperty("inFlow", newVal);
            }
        }
        Type type = new TypeToken<SessionSummary>() {}.getType();
        SessionSummary summary = UtilManager.gson.fromJson(jsonObj, type);
        return summary;
    }

    public static ElapsedTime getTimeBetweenLastPayload() {
        ElapsedTime eTime = new ElapsedTime();

        // default of 1 minute
        long sessionSeconds = 60;
        long elapsedSeconds = 60;

        long lastPayloadEnd = FileUtilManager.getNumericItem("latestPayloadTimestampEndUtc", 0L);
        if (lastPayloadEnd > 0) {
            UtilManager.TimesData timesData = UtilManager.getTimesData();
            elapsedSeconds = Math.max(60, timesData.now - lastPayloadEnd);
            long sessionThresholdSeconds = 60 * 15;
            if (elapsedSeconds > 0 && elapsedSeconds <= sessionThresholdSeconds) {
                sessionSeconds = elapsedSeconds;
            }
            sessionSeconds = Math.max(60, sessionSeconds);
        }

        eTime.sessionSeconds = sessionSeconds;
        eTime.elapsedSeconds = elapsedSeconds;

        return eTime;
    }

    public static void updateFileSummaryAndStatsBar(SessionSummary sessionSummary) {
        if (sessionSummary != null) {
            StatusBarManager.updateStatusBar(sessionSummary);

            ApplicationManager.getApplication().invokeLater(() -> {
                CodeTimeWindowFactory.refresh(false);
            });
        }
    }

    public static boolean isCloseToOrAboveAverage() {
        SessionSummary summary = SessionDataManager.getSessionSummaryData();
        double threshold = summary.averageDailyMinutes - (summary.averageDailyMinutes * .15);
        if (summary.currentDayMinutes >= threshold) {
            return true;
        }
        return false;
    }
}
