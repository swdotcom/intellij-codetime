package com.software.codetime.toolwindows.codetime;

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

import javax.swing.*;


public class CodeTimeWindowFactory implements ToolWindowFactory {

    private static CodeTimeToolWindow ctWindow;
    public static Project windowProject;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        init(project, toolWindow);
    }

    private void init(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ctWindow = new CodeTimeToolWindow(toolWindow, project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(ctWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        windowProject = project;
    }

    private static void checkIfInitialized() {
        if (ctWindow == null) {
            Project p = ProjectActivateListener.getCurrentProject();
            if (p == null) {
                p = IntellijProjectManager.getFirstActiveProject();
            }
            if (p != null) {
                ToolWindow toolWindow = ToolWindowManager.getInstance(p).getToolWindow("Code Time");
                new CodeTimeWindowFactory().createToolWindowContent(p, toolWindow);
            }
        }
    }

    public static void refresh(boolean open) {
        checkIfInitialized();
        if (ctWindow != null) {
            ctWindow.refresh();
        }
        if (open) {
            // open it
            openToolWindow();
        }
    }

    public static void openToolWindow() {
        checkIfInitialized();
        if (windowProject != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                ToolWindow toolWindow = ToolWindowManager.getInstance(windowProject).getToolWindow("Code Time");
                if (toolWindow != null) {
                    toolWindow.show();
                }
            });
        }
    }
}
