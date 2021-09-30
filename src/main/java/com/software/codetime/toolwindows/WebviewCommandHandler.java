package com.software.codetime.toolwindows;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.managers.*;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import com.software.codetime.toolwindows.dashboard.DashboardWindowFactory;
import swdc.java.ops.http.PreferencesClient;
import swdc.java.ops.manager.*;
import swdc.java.ops.model.IntegrationConnection;
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
            case "showOrgDashboard":
                ApplicationManager.getApplication().invokeLater(() -> {
                    UtilManager.launchUrl(ConfigManager.app_url + "/dashboard?org_name=" + data.get("payload").getAsString());
                });
                break;
            case "switchAccount":
                ApplicationManager.getApplication().invokeLater(() -> {
                    AuthPromptManager.initiateSwitchAccountFlow();
                });
                break;
            case "displayReadme":
                ApplicationManager.getApplication().invokeLater(() -> {
                    ReadmeManager.openReadmeFile(UIInteractionType.click);
                });
                break;
            case "configureSettings":
                ApplicationManager.getApplication().invokeLater(() -> {
                    DashboardWindowFactory.displayConfigSettings();
                });
                break;
            case "submitAnIssue":
                ApplicationManager.getApplication().invokeLater(() -> {
                    UtilManager.submitIntellijIssue();
                });
                break;
            case "toggleStatusBar":
                ApplicationManager.getApplication().invokeLater(() -> {
                    StatusBarManager.toggleStatusBar(UIInteractionType.click);
                });
                break;
            case "viewDashboard":
                ApplicationManager.getApplication().invokeLater(() -> {
                    DashboardWindowFactory.displayDashboard();
                });
                break;
            case "softwareKpmDashboard":
                ApplicationManager.getApplication().invokeLater(() -> {
                    UserSessionManager.launchWebDashboard(UIInteractionType.click);
                });
                break;
            case "enableFlowMode":
                ApplicationManager.getApplication().invokeLater(() -> {
                    FlowManager.enterFlowMode(false);
                });
                break;
            case "exitFlowMode":
                ApplicationManager.getApplication().invokeLater(() -> {
                    FlowManager.exitFlowMode();
                });
                break;
            case "toggle_flow":
                ApplicationManager.getApplication().invokeLater(() -> {
                    FlowManager.toggleFlowMode(false);
                });
                break;
            case "manageSlackConnection":
                ApplicationManager.getApplication().invokeLater(() -> {
                    SlackManager.manageSlackConnections();
                });
                break;
            case "connectSlack":
                ApplicationManager.getApplication().invokeLater(() -> {
                    SlackManager.connectSlackWorkspace(() -> {
                        CodeTimeWindowFactory.refresh(false);
                    });
                });
                break;
            case "disconnectSlackWorkspace":
                ApplicationManager.getApplication().invokeLater(() -> {
                    long id = data.get("payload").getAsLong();
                    IntegrationConnection integration = SlackManager.getSlackWorkspaceById(id);
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
            case "createOrg":
                ApplicationManager.getApplication().invokeLater(() -> {
                    UtilManager.launchUrl(ConfigManager.create_org_url);
                });
                break;
            case "skipSlackConnect":
                ApplicationManager.getApplication().invokeLater(() -> {
                    FileUtilManager.setBooleanItem("intellij_CtskipSlackConnect", true);
                    CodeTimeWindowFactory.refresh(false);
                });
                break;
            case "refreshCodeTimeView":
                ApplicationManager.getApplication().invokeLater(() -> {
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
