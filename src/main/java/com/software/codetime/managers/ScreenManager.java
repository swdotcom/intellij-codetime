package com.software.codetime.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.openapi.wm.impl.IdeRootPane;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.manager.AsyncManager;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.logging.Logger;

public class ScreenManager {

    public static final Logger log = Logger.getLogger("ScreenManager");

    private static IdeFrameImpl ideFrame = null;
    private static double fullScreenHeight = 0;
    private static double fullScreenWidth = 0;

    private static IdeFrameImpl getIdeWindow() {
        // Retrieve the AWT window
        Project p = IntellijProjectManager.getOpenProject();
        if (p == null) {
            return null;
        }

        IdeRootPane rootPane = (IdeRootPane) WindowManager.getInstance().getFrame(p).getRootPane();
        if (rootPane != null && ideFrame == null) {
            ideFrame = (IdeFrameImpl) rootPane.getParent();
            ideFrame.addWindowStateListener(new WindowStateListener() {
                @Override
                public void windowStateChanged(WindowEvent e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (FlowManager.isInFlowMode()) {
                            // show the code time view as a reminder flow mode is still on
                            CodeTimeWindowFactory.openToolWindow();
                        }
                    });
                }
            });
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

