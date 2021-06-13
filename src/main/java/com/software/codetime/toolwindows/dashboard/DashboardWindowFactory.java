package com.software.codetime.toolwindows.dashboard;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.software.codetime.listeners.ProjectActivateListener;
import com.software.codetime.managers.IntellijProjectManager;
import org.jetbrains.annotations.NotNull;

public class DashboardWindowFactory implements ToolWindowFactory {
    private static DashboardToolWindow dashboardToolWindow;
    public static Project windowProject;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        init(project, toolWindow);
        toolWindow.hide();
    }

    private static void init(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        dashboardToolWindow = new DashboardToolWindow(toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(dashboardToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        windowProject = project;
    }

    private static void initialize() {
        if (dashboardToolWindow == null) {
            // get the project
            Project p = ProjectActivateListener.getCurrentProject();
            if (p == null) {
                p = IntellijProjectManager.getFirstActiveProject();
            }
            ToolWindow toolWindow = ToolWindowManager.getInstance(p).getToolWindow("Code Time Dashboard");
            if (toolWindow != null) {
                init(p, toolWindow);
            }
        }
    }

    public static void displayConfigSettings() {
        DashboardResourceHandler.updateHtmlApi(DashboardResourceHandler.config_settings_api);
        refresh(true);
    }

    public static void displayDashboard() {
        DashboardResourceHandler.updateHtmlApi(DashboardResourceHandler.dashboard_api);
        refresh(true);
    }

    public static void refresh(boolean open) {
        initialize();

        if (dashboardToolWindow != null) {
            dashboardToolWindow.refresh();
            if (open) {
                // open it
                openToolWindow();
            }
        }
    }

    public static void openToolWindow() {
        if (windowProject != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                ToolWindow toolWindow = ToolWindowManager.getInstance(windowProject).getToolWindow("Code Time Dashboard");
                if (toolWindow != null) {
                    toolWindow.show();
                }
            });
        }
    }

    public static void closeToolWindow() {
        if (windowProject != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                ToolWindow toolWindow = ToolWindowManager.getInstance(windowProject).getToolWindow("Code Time Dashboard");
                if (toolWindow != null) {
                    toolWindow.hide();
                }
            });
        }
    }
}
