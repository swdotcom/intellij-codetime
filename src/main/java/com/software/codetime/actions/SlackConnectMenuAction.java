package com.software.codetime.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import org.jetbrains.annotations.NotNull;
import swdc.java.ops.manager.SlackManager;

public class SlackConnectMenuAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        SlackManager.connectSlackWorkspace(() -> { CodeTimeWindowFactory.refresh(false);});
    }
}
