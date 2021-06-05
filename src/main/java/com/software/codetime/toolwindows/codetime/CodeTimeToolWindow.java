package com.software.codetime.toolwindows.codetime;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.CefApp;

import javax.swing.*;

public class CodeTimeToolWindow {

    private JBCefBrowser browser;

    public CodeTimeToolWindow(ToolWindow toolWindow) {
        browser = new JBCefBrowser();
        registerAppSchemeHandler();
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
