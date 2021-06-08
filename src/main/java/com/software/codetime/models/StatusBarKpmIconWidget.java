package com.software.codetime.models;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import com.software.codetime.managers.UserSessionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class StatusBarKpmIconWidget implements StatusBarWidget {

    public static final String KPM_ICON_ID = "software.kpm.icon";
    public static final String FLOW_ICON_ID = "software.flow.icon";

    private UserSessionManager sessionMgr = UserSessionManager.getInstance();

    private Icon icon = null;
    private String tooltip = "";
    private String id;

    private final IconPresentation presentation = new IconPresentation();
    private Consumer<MouseEvent> eventHandler;

    public StatusBarKpmIconWidget(String id, final Runnable callback) {
        this.id = id;
        eventHandler = new Consumer<MouseEvent>() {
            @Override
            public void consume(MouseEvent mouseEvent) {
                if (callback != null) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        callback.run();
                    });
                }
            }
        };
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void updateIcon(String iconName) {
        Icon icon = IconLoader.findIcon("/assets/" + iconName);
        this.setIcon(icon);
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    class IconPresentation implements StatusBarWidget.IconPresentation {

        @NotNull
        @Override
        public Icon getIcon() {
            return StatusBarKpmIconWidget.this.icon;
        }

        @Nullable
        @Override
        public String getTooltipText() {
            return StatusBarKpmIconWidget.this.tooltip;
        }

        @Nullable
        @Override
        public Consumer<MouseEvent> getClickConsumer() {
            return eventHandler;
        }
    }

    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return presentation;
    }

    @NotNull
    @Override
    public String ID() {
        return id;
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
    }

    @Override
    public void dispose() {
    }
}
