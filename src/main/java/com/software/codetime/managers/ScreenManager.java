package com.software.codetime.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.manager.AsyncManager;

import javax.swing.*;

public class ScreenManager {

    private static IdeFrameImpl ideFrame = null;
    private static double fullScreenHeight = 0;
    private static double fullScreenWidth = 0;

    private static IdeFrameImpl getIdeWindow() {
        // Retrieve the AWT window
        Project p = IntellijProjectManager.getOpenProject();
        if (p == null) {
            return null;
        }

        return ideFrame;
    }

    public static boolean isFullScreen() {
        IdeFrameImpl win = getIdeWindow();

        if (win != null) {

            // maximized both is actually maximized screen, which we
            // consider full screen as well
            if (win.getExtendedState() == JFrame.MAXIMIZED_BOTH || win.getState() == JFrame.MAXIMIZED_BOTH) {
                fullScreenHeight = win.getBounds().getHeight();
                fullScreenWidth = win.getBounds().getWidth();
                return true;
            } else if (win.getX() > 0) {
                return false;
            }

            // it may be full screen
            if (win.getBounds().getHeight() >= fullScreenHeight && win.getBounds().getWidth() >= fullScreenWidth) {
                return true;
            }
        }
        return false;
    }

    public static boolean enterFullScreen() {
        IdeFrameImpl win = getIdeWindow();
        if (win == null) {
            return false;
        }
        if (!isFullScreen()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    win.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    win.setBounds(win.getGraphicsConfiguration().getBounds());
                    win.setVisible(true);
                } catch (Exception e) {}

                AsyncManager.getInstance().executeOnceInSeconds(
                        () -> {
                            CodeTimeWindowFactory.refresh(false);}, 1);
            });
            return true;
        }
        return false;
    }

    public static boolean exitFullScreen() {
        IdeFrameImpl win = getIdeWindow();
        if (win == null) {
            return false;
        }
        if (isFullScreen()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    win.setExtendedState(JFrame.NORMAL);
                    win.setBounds(win.getGraphicsConfiguration().getBounds());
                    win.setVisible(true);
                } catch (Exception e) {}
                AsyncManager.getInstance().executeOnceInSeconds(
                        () -> {CodeTimeWindowFactory.refresh(false);}, 1);
            });
            return true;
        }
        return false;
    }
}

