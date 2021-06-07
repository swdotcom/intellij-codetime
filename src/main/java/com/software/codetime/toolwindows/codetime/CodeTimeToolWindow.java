package com.software.codetime.toolwindows.codetime;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefApp;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CodeTimeToolWindow {

    private JBCefBrowser browser;

    public CodeTimeToolWindow(@NotNull ToolWindow toolWindow, @NotNull Project project) {
        browser = new JBCefBrowser();
        browser.getJBCefClient().addDisplayHandler(new CodeTimeDisplayHandler(), browser.getCefBrowser());
        registerAppSchemeHandler();
        Disposer.register(project, browser);
        browser.loadURL("http://myapp/index.html");

    }

    private void registerAppSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory("http", "myapp", new CodeTimeSchemeHandlerFactory());
    }

    public JComponent getContent() {
        return browser.getComponent();
    }

    public static void refresh() {
        //
    }

    private synchronized void init() {
        //
    }

}
