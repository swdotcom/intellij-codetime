package com.software.codetime.managers;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.software.codetime.models.StatusBarKpmIconWidget;
import com.software.codetime.models.StatusBarKpmTextWidget;
import com.software.codetime.toolwindows.codetime.CodeTimeToolWindow;
import swdc.java.ops.manager.ConfigManager;
import swdc.java.ops.manager.EventTrackerManager;
import swdc.java.ops.snowplow.entities.UIElementEntity;
import swdc.java.ops.snowplow.events.UIInteractionType;

import javax.swing.*;
import java.io.*;

public class StatusBarManager {

    private static boolean showStatusText = true;

    public static boolean showingStatusText() {
        return showStatusText;
    }

    public static void toggleStatusBar(UIInteractionType interactionType) {
        String cta_text = !showStatusText ? "Show status bar metrics" : "Hide status bar metrics";
        showStatusText = !showStatusText;

        WallClockManager.getInstance().dispatchStatusViewUpdate();

        // refresh the tree
        CodeTimeToolWindow.refresh();

        UIElementEntity elementEntity = new UIElementEntity();
        elementEntity.element_name = interactionType == UIInteractionType.click ? "ct_toggle_status_bar_metrics_btn" : "ct_toggle_status_bar_metrics_cmd";
        elementEntity.element_location = interactionType == UIInteractionType.click ? "ct_menu_tree" : "ct_command_palette";
        elementEntity.color = interactionType == UIInteractionType.click ? "blue" : null;
        elementEntity.cta_text = cta_text;
        elementEntity.icon_name = interactionType == UIInteractionType.click ? "slash-eye" : null;
        EventTrackerManager.getInstance().trackUIInteraction(interactionType, elementEntity);
    }

    public static void updateStatusBar(final String kpmIcon, final String kpmMsg, final String tooltip) {

        // build the status bar text information
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                ProjectManager pm = ProjectManager.getInstance();
                if (pm != null && pm.getOpenProjects() != null && pm.getOpenProjects().length > 0) {
                    try {
                        Project p = pm.getOpenProjects()[0];
                        final StatusBar statusBar = WindowManager.getInstance().getStatusBar(p);

                        String kpmmsgId = StatusBarKpmTextWidget.KPM_TEXT_ID + "_kpmmsg";
                        String kpmiconId = StatusBarKpmIconWidget.KPM_ICON_ID + "_kpmicon";

                        String kpmMsgVal = kpmMsg != null ? kpmMsg : ConfigManager.plugin_name;

                        String kpmIconVal = kpmIcon;
                        if (!showStatusText) {
                            kpmMsgVal = "";
                            kpmIconVal = "status-clock.svg";
                        }

                        // icon first
                        StatusBarKpmIconWidget kpmIconWidget = (StatusBarKpmIconWidget) statusBar.getWidget(kpmiconId);
                        if (kpmIconWidget == null) {
                            kpmIconWidget = buildStatusBarIconWidget(kpmIconVal, tooltip, kpmiconId);
                            statusBar.addWidget(kpmIconWidget, kpmiconId);
                        } else {
                            kpmIconWidget.updateIcon(kpmIconVal);
                            kpmIconWidget.setTooltip(tooltip);
                        }
                        statusBar.updateWidget(kpmiconId);

                        // text next
                        StatusBarKpmTextWidget kpmMsgWidget = (StatusBarKpmTextWidget) statusBar.getWidget(kpmmsgId);
                        if (kpmMsgWidget == null) {
                            kpmMsgWidget = buildStatusBarTextWidget(kpmMsgVal, tooltip, kpmmsgId);
                            statusBar.addWidget(kpmMsgWidget, kpmmsgId);
                        } else {
                            kpmMsgWidget.setText(kpmMsgVal);
                            kpmMsgWidget.setTooltip(tooltip);
                        }
                        statusBar.updateWidget(kpmmsgId);

                    } catch(Exception e){
                        //
                    }
                }
            }
        });
    }

    public static StatusBarKpmTextWidget buildStatusBarTextWidget(String msg, String tooltip, String id) {
        StatusBarKpmTextWidget textWidget =
                new StatusBarKpmTextWidget(id);
        textWidget.setText(msg);
        textWidget.setTooltip(tooltip);
        return textWidget;
    }

    public static StatusBarKpmIconWidget buildStatusBarIconWidget(String iconName, String tooltip, String id) {
        Icon icon = IconLoader.findIcon("/com/softwareco/intellij/plugin/assets/" + iconName);

        StatusBarKpmIconWidget iconWidget =
                new StatusBarKpmIconWidget(id);
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
