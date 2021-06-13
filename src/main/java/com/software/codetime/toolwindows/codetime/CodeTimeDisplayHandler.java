package com.software.codetime.toolwindows.codetime;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.managers.*;
import com.software.codetime.toolwindows.dashboard.DashboardWindowFactory;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandler;
import swdc.java.ops.manager.*;
import swdc.java.ops.model.Integration;
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
                ApplicationManager.getApplication().invokeLater(() -> {
                    UtilManager.launchUrl(ConfigManager.app_url + "/dashboard?org_name=" + data.get("org_name").getAsString() + "&team_id=" + data.get("team_id").getAsInt());
                });
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
                ApplicationManager.getApplication().invokeLater(() -> {
                    UtilManager.submitIntellijIssue();
                });
                break;
            case "toggle_status":
                ApplicationManager.getApplication().invokeLater(() -> {
                    StatusBarManager.toggleStatusBar(UIInteractionType.click);
                });
                break;
            case "dashboard":
                ApplicationManager.getApplication().invokeLater(() -> {
                    DashboardWindowFactory.displayDashboard();
                });
                break;
            case "web_dashboard":
                ApplicationManager.getApplication().invokeLater(() -> {
                    UserSessionManager.launchWebDashboard(UIInteractionType.click);
                });
                break;
            case "toggle_flow":
                ApplicationManager.getApplication().invokeLater(() -> {
                    FlowManager.toggleFlowMode(false);
                });
                break;
            case "add_workspace":
                ApplicationManager.getApplication().invokeLater(() -> {
                    SlackManager.connectSlackWorkspace(() -> {
                        CodeTimeWindowFactory.refresh(false);
                    });
                });
                break;
            case "remove_workspace":
                ApplicationManager.getApplication().invokeLater(() -> {
                    long id = data.get("id").getAsLong();
                    Integration integration = SlackManager.getSlackWorkspaceById(id);
                    SlackManager.disconnectSlackAuth(integration, () -> {CodeTimeWindowFactory.refresh(false);});
                });
                break;
            case "register":
                ApplicationManager.getApplication().invokeLater(() -> {
                    AuthPromptManager.initiateSignupFlow();
                });
                break;
            case "login":
                ApplicationManager.getApplication().invokeLater(() -> {
                    AuthPromptManager.initiateLoginFlow();
                });
                break;
            case "create_team":
                ApplicationManager.getApplication().invokeLater(() -> {
                    UtilManager.launchUrl(ConfigManager.create_team_url);
                });
                break;
            case "skip_slack_connect":
                ApplicationManager.getApplication().invokeLater(() -> {
                    FileUtilManager.setBooleanItem("intellij_CtskipSlackConnect", true);
                    CodeTimeWindowFactory.refresh(false);
                });
                break;
            case "refresh_workspaces":
                ApplicationManager.getApplication().invokeLater(() -> {
                    AccountManager.getUser();
                });
                break;
            default:
                break;
        }
    }
}
