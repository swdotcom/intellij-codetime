package com.software.codetime.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.ProjectFrameHelper;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import swdc.java.ops.manager.AsyncManager;

import javax.swing.*;
import java.awt.*;

public class ScreenManager {

    private static boolean inFullScreenMode = false;

    private static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    private static JFrame getIdeWindow() {
        // Retrieve the AWT window
        Project p = IntellijProjectManager.getOpenProject();
        if (p == null) {
            return null;
        } else {
            try {
                return ((ProjectFrameHelper)WindowManager.getInstance().getIdeFrame(p)).getFrame();
            } catch (Exception e) {
                //
            }
        }
        return null;
    }

    public static boolean enterFullScreen() {
        JFrame win = getIdeWindow();
        if (win == null) {
            return false;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            Dimension screenSize = getScreenSize();
            int w = (int) screenSize.getWidth();
            int h = (int) screenSize.getHeight();
            try {
                win.setBounds(0, 0, w, h);
                inFullScreenMode = true;
            } catch (Exception e) {
                //
            }

            AsyncManager.getInstance().executeOnceInSeconds(
                    () -> {
                        CodeTimeWindowFactory.refresh(false);}, 1);
        });
        return true;
    }

    public static boolean exitFullScreen() {
        JFrame win = getIdeWindow();
        if (win == null) {
            return false;
        }

        if (inFullScreenMode) {
            ApplicationManager.getApplication().invokeLater(() -> {
                Dimension screenSize = getScreenSize();
                try {
                    int w = (int) (screenSize.getWidth() - 200);
                    int h = (int) (screenSize.getHeight() - 100);
                    int x = Long.valueOf(Math.round((screenSize.getWidth() - w) / 2)).intValue();
                    int y = Long.valueOf(Math.round((screenSize.getHeight() - h) / 2)).intValue();
                    win.setBounds(x, y, w, h);
                } catch (Exception e) {}
                AsyncManager.getInstance().executeOnceInSeconds(
                        () -> {CodeTimeWindowFactory.refresh(false);}, 1);
            });
            return true;
        }
        return false;
    }
}

