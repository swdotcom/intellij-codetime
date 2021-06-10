package com.software.codetime.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import org.jetbrains.annotations.NotNull;
import swdc.java.ops.manager.SlackManager;

public class SlackDisconnectMenuAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        SlackManager.disconnectSlackWorkspace(() -> {
            CodeTimeWindowFactory.refresh(false);
        });
    }

    @Override
    public void update(AnActionEvent event) {
        boolean showMenuItem = SlackManager.hasSlackWorkspaces();
        event.getPresentation().setVisible(showMenuItem);
    }
}
