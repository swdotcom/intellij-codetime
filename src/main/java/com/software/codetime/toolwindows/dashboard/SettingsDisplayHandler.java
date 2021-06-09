package com.software.codetime.toolwindows.dashboard;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandler;
import swdc.java.ops.http.PreferencesClient;
import swdc.java.ops.manager.AsyncManager;
import swdc.java.ops.manager.UtilManager;

public class SettingsDisplayHandler implements CefDisplayHandler {

    @Override
    public void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String s) {
    }

    @Override
    public void onTitleChange(CefBrowser cefBrowser, String s) {
    }

    @Override
    public boolean onTooltip(CefBrowser cefBrowser, String s) {
        return false;
    }

    @Override
    public void onStatusMessage(CefBrowser cefBrowser, String s) {
    }

    @Override
    public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String s, String s1, int i) {
        try {
            JsonObject jsonObject = UtilManager.gson.fromJson(s, JsonObject.class);
            if (!jsonObject.isJsonNull() && jsonObject.has("cmd")) {
                String cmd = jsonObject.get("cmd").getAsString();
                jsonObject.remove("cmd");
                executeJavascriptCommands(cmd, jsonObject);
            }
        } catch (Exception e) {
            System.out.println("Console message error: " + e.getMessage());
        }
        return false;
    }

    private void executeJavascriptCommands(String cmd, JsonObject data) {
        switch (cmd) {
            case "close_settings":
                ApplicationManager.getApplication().invokeLater(() -> {
                    DashboardWindowFactory.closeToolWindow();
                });
                break;
            case "submit_settings":
                // post to /users/me/preferences
                // i.e. {notifications: {endOfDayNotification: true}, flowMode: {durationMinutes: 120, editor: {autoEnterFlowMode: true...}
                ApplicationManager.getApplication().invokeLater(() -> {
                    boolean updated = PreferencesClient.updatePreferences(data);
                    if (updated) {
                        // close the tool window
                        AsyncManager.getInstance().executeOnceInSeconds(() -> {DashboardWindowFactory.closeToolWindow();}, 3);
                    }
                });
                break;
            default:
                break;
        }
    }
}
