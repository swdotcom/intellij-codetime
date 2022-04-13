package com.software.codetime.toolwindows.codetime;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.JBCefApp;
import com.software.codetime.listeners.ProjectActivateListener;
import com.software.codetime.managers.IntellijProjectManager;
import org.jetbrains.annotations.NotNull;

public class SidebarToolWindow implements ToolWindowFactory {
    private static CodeTimeToolWindow ctWindow;
    private static TreeView tv;
    public static Project windowProject;
    private static boolean JcefSupported = JBCefApp.isSupported();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull com.intellij.openapi.wm.ToolWindow toolWindow) {
        if (JcefSupported) {
            initWebView(project, toolWindow);
        } else {
            initTreeView(project, toolWindow);
        }
    }

    @Override
    public void init(@NotNull com.intellij.openapi.wm.ToolWindow toolWindow) {
        ToolWindowFactory.super.init(toolWindow);
    }

    private static void initWebView(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ctWindow = new CodeTimeToolWindow(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(ctWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        windowProject = project;
    }

    private static void initTreeView(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        tv = new TreeView();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(tv.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        windowProject = project;
    }

    private static void initialize() {
        if (JcefSupported) {
            if (ctWindow == null) {
                // get the project
                Project p = ProjectActivateListener.getCurrentProject();
                if (p == null) {
                    p = IntellijProjectManager.getFirstActiveProject();
                }
                com.intellij.openapi.wm.ToolWindow toolWindow = ToolWindowManager.getInstance(p).getToolWindow("Code Time");
                if (toolWindow != null) {
                    initWebView(p, toolWindow);
                }
            }
        } else {
            if (tv == null) {
                Project p = ProjectActivateListener.getCurrentProject();
                if (p == null) {
                    p = IntellijProjectManager.getFirstActiveProject();
                }
                com.intellij.openapi.wm.ToolWindow toolWindow = ToolWindowManager.getInstance(p).getToolWindow("Code Time");
                if (toolWindow != null) {
                    initTreeView(p, toolWindow);
                }
            }
        }
    }

    public static void refresh(boolean open) {
        initialize();
        if (JcefSupported) {
            if (ctWindow != null) {
                ctWindow.refresh();
            }
        } else {
            if (tv != null) {
                tv.refresh();
            }
        }
        if (open) {
            // open it
            openToolWindow();
        }
    }

    public static void openToolWindow() {
        if (windowProject != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                com.intellij.openapi.wm.ToolWindow toolWindow = ToolWindowManager.getInstance(windowProject).getToolWindow("Code Time");
                if (toolWindow != null) {
                    toolWindow.show();
                }
            });
        }
    }
}
