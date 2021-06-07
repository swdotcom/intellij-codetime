package com.software.codetime.toolwindows.codetime;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.software.codetime.managers.IntellijProjectManager;
import org.jetbrains.annotations.NotNull;


public class CodeTimeWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CodeTimeToolWindow ctWindow = new CodeTimeToolWindow(toolWindow, project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(ctWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public static void openToolWindow() {
        Project p = IntellijProjectManager.getFirstActiveProject();
        if (p != null) {
            ToolWindow tw = ToolWindowManager.getInstance(p).getToolWindow("Code Time");
            if (tw != null) {
                tw.show(null);
            }
        }
    }

    public static boolean isToolWindowVisible() {
        Project p = IntellijProjectManager.getFirstActiveProject();
        if (p != null) {
            ToolWindow tw = ToolWindowManager.getInstance(p).getToolWindow("Code Time");
            if (tw != null) {
                return tw.isVisible();
            }
        }
        return false;
    }
}
