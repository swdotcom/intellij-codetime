package com.software.codetime.managers;

import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.http.ClientResponse;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.manager.UtilManager;
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

    public static void updateFileSummaryAndStatsBar(SessionSummary sessionSummary) {
        if (sessionSummary != null) {
            StatusBarManager.updateStatusBar(sessionSummary);

            ApplicationManager.getApplication().invokeLater(() -> {
                CodeTimeWindowFactory.refresh(false);
            });
        }
    }
}
