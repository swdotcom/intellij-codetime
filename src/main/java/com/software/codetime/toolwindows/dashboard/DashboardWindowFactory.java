package com.software.codetime.toolwindows.dashboard;

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
    }

    private void init(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        dashboardToolWindow = new DashboardToolWindow(toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(dashboardToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        windowProject = project;
    }

    private static void checkIfInitialized() {
        if (dashboardToolWindow == null) {
            Project p = ProjectActivateListener.getCurrentProject();
            if (p == null) {
                p = IntellijProjectManager.getFirstActiveProject();
            }
            if (p != null) {
                ToolWindow toolWindow = ToolWindowManager.getInstance(p).getToolWindow("Dashboard");
                new DashboardWindowFactory().createToolWindowContent(p, toolWindow);
            }
        }
    }

    public static void displayConfigSettings() {
        DashboardResourceHandler.updateHtmlApi(DashboardResourceHandler.config_settings_api);
        checkIfInitialized();
        refresh(true);
    }

    public static void displayDashboard() {
        DashboardResourceHandler.updateHtmlApi(DashboardResourceHandler.dashboard_api);
        checkIfInitialized();
        refresh(true);
    }

    public static void refresh(boolean open) {
        if (dashboardToolWindow != null) {
            dashboardToolWindow.refresh();
        }
        if (open) {
            // open it
            openToolWindow();
        }
    }

    public static void openToolWindow() {
        checkIfInitialized();
        if (windowProject != null) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(windowProject).getToolWindow("Dashboard");
            if (toolWindow != null) {
                toolWindow.show();
            }
        }
    }
}
