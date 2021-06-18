package com.software.codetime.toolwindows;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.managers.*;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import com.software.codetime.toolwindows.dashboard.DashboardWindowFactory;
import swdc.java.ops.http.PreferencesClient;
import swdc.java.ops.manager.*;
import swdc.java.ops.model.Integration;
import swdc.java.ops.snowplow.events.UIInteractionType;

public class WebviewCommandHandler {

    public static boolean onConsoleCommand(String commandData) {
        try {
            JsonObject jsonObject = UtilManager.gson.fromJson(commandData, JsonObject.class);
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

    private static void executeJavascriptCommands(String cmd, JsonObject data) {
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
                        AsyncManager.getInstance().executeOnceInSeconds(() -> {
                            AccountManager.getUser();
                        }, 0);

                        // close the tool window
                        AsyncManager.getInstance().executeOnceInSeconds(() -> {
                            DashboardWindowFactory.closeToolWindow();
                        }, 3);
                    }
                });
                break;
            default:
                break;
        }
    }
}
