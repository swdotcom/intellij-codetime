package com.software.codetime.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import com.software.codetime.toolwindows.dashboard.DashboardWindowFactory;
import org.jetbrains.annotations.Nullable;

public class ThemeColorChangeListener implements EditorColorsListener {

    @Override
    public void globalSchemeChange(@Nullable EditorColorsScheme scheme) {
        // reload the sidebar if its in view
        ApplicationManager.getApplication().invokeLater(() -> {
            DashboardWindowFactory.refresh(false);
            CodeTimeWindowFactory.refresh(false);
        });
    }
}
