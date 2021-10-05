package com.software.codetime.managers;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.software.codetime.models.StatusBarKpmIconWidget;
import com.software.codetime.models.StatusBarKpmTextWidget;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.http.FlowModeClient;
import swdc.java.ops.manager.ConfigManager;
import swdc.java.ops.manager.EventTrackerManager;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.manager.UtilManager;
import swdc.java.ops.model.SessionSummary;
import swdc.java.ops.snowplow.entities.UIElementEntity;
import swdc.java.ops.snowplow.events.UIInteractionType;

import javax.swing.*;
import java.io.*;

public class StatusBarManager {

    private static boolean showStatusText = true;

    private final static String kpmmsgId = StatusBarKpmTextWidget.KPM_TEXT_ID + "_kpmmsg";
    private final static String kpmiconId = StatusBarKpmIconWidget.KPM_ICON_ID + "_kpmicon";
    private final static String flowmsgId = StatusBarKpmTextWidget.FLOW_TEXT_ID + "_flowmsg";
    private final static String flowiconId = StatusBarKpmIconWidget.FLOW_ICON_ID + "_flowicon";

    private static SessionSummary summary = null;

    public static boolean showingStatusText() {
        return showStatusText;
    }

    public static void toggleStatusBar(UIInteractionType interactionType) {
        String cta_text = !showStatusText ? "Show status bar metrics" : "Hide status bar metrics";
        showStatusText = !showStatusText;

        updateStatusBar(null);

        // refresh the tree
        CodeTimeWindowFactory.refresh(false);

        UIElementEntity elementEntity = new UIElementEntity();
        elementEntity.element_name = interactionType == UIInteractionType.click ? "ct_toggle_status_bar_metrics_btn" : "ct_toggle_status_bar_metrics_cmd";
        elementEntity.element_location = interactionType == UIInteractionType.click ? "ct_menu_tree" : "ct_command_palette";
        elementEntity.color = interactionType == UIInteractionType.click ? "blue" : null;
        elementEntity.cta_text = cta_text;
        elementEntity.icon_name = interactionType == UIInteractionType.click ? "slash-eye" : null;
        EventTrackerManager.getInstance().trackUIInteraction(interactionType, elementEntity);
    }

    public static void updateStatusBar(SessionSummary sessionSummary) {
        if (sessionSummary == null) {
            sessionSummary = SessionDataManager.fetchSessionSummary();
        }

        FileUtilManager.writeData(FileUtilManager.getSessionDataSummaryFile(), sessionSummary);

        String currentDayTimeStr = UtilManager.humanizeMinutes(sessionSummary.currentDayMinutes);

        // build the status bar text information
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                ProjectManager pm = ProjectManager.getInstance();
                if (pm != null && pm.getOpenProjects() != null && pm.getOpenProjects().length > 0) {
                    try {
                        Project p = pm.getOpenProjects()[0];
                        final StatusBar statusBar = WindowManager.getInstance().getStatusBar(p);

                        String kpmMsgVal = currentDayTimeStr != null ? currentDayTimeStr : ConfigManager.plugin_name;

                        StatusBarKpmTextWidget kpmMsgWidget = (StatusBarKpmTextWidget) statusBar.getWidget(kpmmsgId);
                        if (kpmMsgWidget == null) {
                            // remove the flow widgets so this doesn't appear out of order
                            try { statusBar.removeWidget(flowiconId); } catch (Exception e) {}
                            try { statusBar.removeWidget(flowmsgId); } catch (Exception e) {}
                        }

                        // icon first
                        String metricIconTooltip = "";
                        String kpmIcon = "time-clock.png";
                        updateIconWidget(statusBar, kpmiconId, kpmIcon, metricIconTooltip, () -> {
                            CodeTimeWindowFactory.openToolWindow();
                        });

                        // text next
                        String kpmTextTooltip = "Active code time today. Click to see more from Code Time.";
                        updateTextWidget(statusBar, kpmmsgId, kpmMsgVal, kpmTextTooltip, () -> {
                            CodeTimeWindowFactory.openToolWindow();
                        });

                        // flow icon
                        String flowTooltip = "Enter Flow Mode";
                        String flowIcon = "open-circle.png";
                        try {
                            if (FlowModeClient.isFlowModeOn()) {
                                flowIcon = "closed-circle.png";
                                flowTooltip = "Exit Flow Mode";
                            }
                            updateIconWidget(statusBar, flowiconId, flowIcon, flowTooltip, () -> {
                                FlowManager.toggleFlowMode(false);
                            });

                            // flow text next
                            updateTextWidget(statusBar, flowmsgId, "Flow", flowTooltip, () -> {
                                FlowManager.toggleFlowMode(false);
                            });
                        } catch (Exception e) {
                            System.out.println("status bar update error: " + e.getMessage());
                        }
                    } catch(Exception e){
                        //
                    }
                }
            }
        });
    }

    private static void updateIconWidget(StatusBar statusBar, String widgetId, String icon, String tooltip, Runnable callback) {
        StatusBarKpmIconWidget kpmIconWidget = (StatusBarKpmIconWidget) statusBar.getWidget(widgetId);
        if (kpmIconWidget == null) {
            kpmIconWidget = buildStatusBarIconWidget(icon, tooltip, widgetId, callback);
            statusBar.addWidget(kpmIconWidget, widgetId, statusBar);
        } else {
            kpmIconWidget.updateIcon(icon);
            kpmIconWidget.setTooltip(tooltip);
        }
        statusBar.updateWidget(widgetId);
    }

    private static void updateTextWidget(StatusBar statusBar, String widgetId, String msg, String tooltip, Runnable callback) {
        StatusBarKpmTextWidget kpmMsgWidget = (StatusBarKpmTextWidget) statusBar.getWidget(widgetId);
        if (showStatusText || widgetId.equals(flowmsgId)) {
            if (kpmMsgWidget == null) {
                kpmMsgWidget = buildStatusBarTextWidget(msg, tooltip, widgetId, callback);
                statusBar.addWidget(kpmMsgWidget, widgetId, statusBar);
            } else {
                kpmMsgWidget.setText(msg);
                kpmMsgWidget.setTooltip(tooltip);
            }
            statusBar.updateWidget(widgetId);
        } else if (kpmMsgWidget != null) {
            statusBar.removeWidget(widgetId);
        }
    }

    public static StatusBarKpmTextWidget buildStatusBarTextWidget(String msg, String tooltip, String id, Runnable callback) {
        StatusBarKpmTextWidget textWidget =
                new StatusBarKpmTextWidget(id, callback);
        textWidget.setText(msg);
        textWidget.setTooltip(tooltip);
        return textWidget;
    }

    public static StatusBarKpmIconWidget buildStatusBarIconWidget(String iconName, String tooltip, String id, Runnable callback) {
        Icon icon = UtilManager.getResourceIcon(iconName, StatusBarManager.class.getClassLoader());

        StatusBarKpmIconWidget iconWidget =
                new StatusBarKpmIconWidget(id, callback);
        iconWidget.setIcon(icon);
        iconWidget.setTooltip(tooltip);
        return iconWidget;
    }

    public static void launchSoftwareTopForty() {
        BrowserUtil.browse("http://api.software.com/music/top40");
    }

    public static void submitGitIssue() {
        BrowserUtil.browse("https://github.com/swdotcom/swdc-intellij/issues");
    }

    public static void submitFeedback(UIInteractionType interactionType) {
        BrowserUtil.browse("mailto:cody@software.com");

        UIElementEntity elementEntity = new UIElementEntity();
        elementEntity.element_name = interactionType == UIInteractionType.click ? "ct_submit_feedback_btn" : "ct_submit_feedback_cmd";
        elementEntity.element_location = interactionType == UIInteractionType.click ? "ct_menu_tree" : "ct_command_palette";
        elementEntity.color = null;
        elementEntity.cta_text = "Submit feedback";
        elementEntity.icon_name = interactionType == UIInteractionType.click ? "text-bubble" : null;
        EventTrackerManager.getInstance().trackUIInteraction(interactionType, elementEntity);
    }

    public static void launchFile(String fsPath) {
        Project p = IntellijProjectManager.getOpenProject();
        if (p == null) {
            return;
        }
        File f = new File(fsPath);
        if (f.exists()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(f);
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(p, vFile);
                    FileEditorManager mgr = FileEditorManager.getInstance(p);
                    if (mgr != null) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            try {
                                mgr.openTextEditor(descriptor, true);
                            } catch (Exception e) {
                                System.out.println("Error opening file: " + e.getMessage());
                            }
                        });
                    }
                } catch (Exception e) {

                }
            });
        }
    }

}
