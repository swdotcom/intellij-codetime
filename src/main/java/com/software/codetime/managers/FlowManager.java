package com.software.codetime.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.main.PluginInfo;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import org.apache.commons.lang.StringUtils;
import swdc.java.ops.event.SlackStateChangeModel;
import swdc.java.ops.http.FlowModeClient;
import swdc.java.ops.manager.*;
import swdc.java.ops.model.*;

import javax.swing.*;

public class FlowManager {
    public static boolean enabledFlow = false;

    public static void initFlowStatus() {
        enabledFlow = FlowModeClient.isFlowModeOn();
        updateFlowStateDisplay();
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
            updateFlowStateDisplay();
            return;
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

        boolean inFlow = FileUtilManager.getFlowChangeState();
        if ((automated || allowAutoFlowMode()) && !inFlow) {
            // go ahead and make the api call to enter flow mode
            FlowModeClient.enterFlowMode(automated);
        }

        if (fullScreeConfigured()) {
            ScreenManager.enterFullScreen();
        } else {
            ScreenManager.exitFullScreen();
        }

        SlackManager.clearSlackCache();

        enabledFlow = true;

        updateFlowStateDisplay();
    }

    public static void exitFlowMode() {
        if (!enabledFlow) {
            updateFlowStateDisplay();
            return;
        }

        if (allowAutoFlowModeDisable()) {
            FlowModeClient.exitFlowMode();
        }

        ScreenManager.exitFullScreen();

        SlackManager.clearSlackCache();

        enabledFlow = false;

        updateFlowStateDisplay();
    }

    private static void updateFlowStateDisplay() {
        ApplicationManager.getApplication().invokeLater(() -> {
            // at least update the status bar
            AsyncManager.getInstance().executeOnceInSeconds(() -> {
                CodeTimeWindowFactory.refresh(false);
            }, 2);
            StatusBarManager.updateStatusBar(null);
        });
    }

    public static boolean isFlowModeEnabled() {
        return enabledFlow;
    }

    public static boolean fullScreeConfigured() {
        String flowModePreferences = FileUtilManager.getItem("flowMode");
        if (StringUtils.isNotBlank(flowModePreferences)) {
            FlowMode flowMode = UtilManager.gson.fromJson(flowModePreferences, FlowMode.class);

            return flowMode.editor.intellij.screenMode.contains("Full Screen") ? true : false;
        }
        return false;
    }

    private static boolean allowAutoFlowMode() {
        return ( !PluginInfo.isEditorOpsInstalled() || !AutomationTriggerManager.hasEditorOpsAutoFlowModeTrigger() );
    }

    private static boolean allowAutoFlowModeDisable() {
        return ( !PluginInfo.isEditorOpsInstalled() || !AutomationTriggerManager.hasEditorOpsAutoFlowModeDisabledTrigger() );
    }
}
