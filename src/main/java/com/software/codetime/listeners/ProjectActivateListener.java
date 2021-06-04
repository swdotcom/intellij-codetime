package com.software.codetime.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.software.codetime.main.Activator;
import org.jetbrains.annotations.NotNull;

public class ProjectActivateListener implements ProjectManagerListener {
    @Override
    public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);
        Activator.getInstance();
    }
}
