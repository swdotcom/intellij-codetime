package com.software.codetime.toolwindows.codetime;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.managers.AuthPromptManager;
import com.software.codetime.managers.ReadmeManager;
import com.software.codetime.managers.StatusBarManager;
import com.software.codetime.managers.UserSessionManager;
import com.software.codetime.toolwindows.dashboard.DashboardWindowFactory;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandler;
import swdc.java.ops.manager.ConfigManager;
import swdc.java.ops.manager.UtilManager;
import swdc.java.ops.snowplow.events.UIInteractionType;

public class CodeTimeDisplayHandler implements CefDisplayHandler {
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
            case "launch_team":
                UtilManager.launchUrl(ConfigManager.app_url + "/dashboard?org_name=" + data.get("org_name").getAsString() + "&team_id=" + data.get("team_id").getAsInt());
                break;
            case "switch_account":
                ApplicationManager.getApplication().invokeLater(() -> {
                    AuthPromptManager.initiateSwitchAccountFlow();
                });
                break;
            case "readme":
                ApplicationManager.getApplication().invokeLater(() -> {
                    ReadmeManager.openReadmeFile(UIInteractionType.click);
                });
                break;
            case "configure":
                ApplicationManager.getApplication().invokeLater(() -> {
                    DashboardWindowFactory.displayConfigSettings();
                });
                break;
            case "submit_issue":
                UtilManager.submitIntellijIssue();
                break;
            case "toggle_status":
                StatusBarManager.toggleStatusBar(UIInteractionType.click);
                break;
            case "dashboard":
                ApplicationManager.getApplication().invokeLater(() -> {
                    DashboardWindowFactory.displayDashboard();
                });
                break;
            case "web_dashboard":
                UserSessionManager.launchWebDashboard(UIInteractionType.click);
                break;
            default:
                break;
        }
    }
}
