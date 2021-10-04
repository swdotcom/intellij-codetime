package com.software.codetime.managers;

import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.event.SlackStateChangeModel;
import swdc.java.ops.http.FlowModeClient;
import swdc.java.ops.manager.*;
import swdc.java.ops.model.*;

import javax.swing.*;

public class FlowManager {
    public static boolean enabledFlow = false;

    public static void initFlowStatus() {
        enabledFlow = FlowModeClient.isFlowModeOn();
    }

    public static void toggleFlowMode(boolean automated) {
        if (!enabledFlow) {
            enterFlowMode(automated);
        } else {
            exitFlowMode();
        }
    }

    public static void enterFlowMode(boolean automated) {
        if (enabledFlow) {
            return;
        } else {
            // check if its enabled via the api in case another editor has performed this request
            enabledFlow = FlowModeClient.isFlowModeOn();
            if (enabledFlow) {
                updateFlowStateDisplay();
                return;
            }
        }

        boolean isRegistered = AccountManager.checkRegistration(false, null);
        if (!isRegistered) {
            // show the flow mode prompt
            AccountManager.showModalSignupPrompt("To use Flow Mode, please first sign up or login.", () -> { CodeTimeWindowFactory.refresh(true);});
            return;
        }

        boolean intellij_CtskipSlackConnect = FileUtilManager.getBooleanItem("intellij_CtskipSlackConnect");
        boolean workspaces = SlackManager.hasSlackWorkspaces();
        if (!workspaces && !intellij_CtskipSlackConnect) {
            String msg = "Connect a Slack workspace to pause\nnotifications and update your status?";

            Object[] options = {"Connect", "Skip"};
            Icon icon = UtilManager.getResourceIcon("app-icon-blue.png", FlowManager.class.getClassLoader());

            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showOptionDialog(
                        null, msg, "Slack connect", JOptionPane.OK_OPTION,
                        JOptionPane.QUESTION_MESSAGE, icon, options, options[0]);

                if (choice == 0) {
                    SlackStateChangeModel changeModel = new SlackStateChangeModel();
                    SlackManager.connectSlackWorkspace(() -> {
                        CodeTimeWindowFactory.refresh(true);
                    });
                } else {
                    FileUtilManager.setBooleanItem("intellij_CtskipSlackConnect", true);
                    FlowManager.enterFlowMode(automated);
                }
            });
            return;
        }

        FlowModeClient.enterFlowMode(automated);

        if (fullScreeConfigured()) {
            ScreenManager.enterFullScreen();
        } else {
            ScreenManager.exitFullScreen();
        }

        SlackManager.clearSlackCache();

        updateFlowStateDisplay();

        enabledFlow = true;
    }

    public static void exitFlowMode() {
        if (!enabledFlow) {
            return;
        } else {
            // check if its disabled via the api in case another editor has performed this request
            enabledFlow = FlowModeClient.isFlowModeOn();
            if (!enabledFlow) {
                updateFlowStateDisplay();
                return;
            }
        }

        FlowModeClient.exitFlowMode();

        ScreenManager.exitFullScreen();

        SlackManager.clearSlackCache();

        updateFlowStateDisplay();

        enabledFlow = false;
    }

    private static void updateFlowStateDisplay() {
        // at least update the status bar
        CodeTimeWindowFactory.refresh(false);
        StatusBarManager.updateStatusBar();
    }

    public static boolean isFlowModeEnabled() {
        return enabledFlow;
    }

    public static boolean fullScreeConfigured() {
        FlowMode flowMode = UtilManager.gson.fromJson(FileUtilManager.getItem("flowMode"), FlowMode.class);

        return flowMode.editor.intellij.screenMode.contains("Full Screen") ? true : false;
    }
}
