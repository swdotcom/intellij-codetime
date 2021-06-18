package com.software.codetime.toolwindows.dashboard;

import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.software.codetime.toolwindows.WebviewCommandHandler;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandler;
import swdc.java.ops.http.PreferencesClient;
import swdc.java.ops.manager.AccountManager;
import swdc.java.ops.manager.AsyncManager;
import swdc.java.ops.manager.UtilManager;

public class SettingsDisplayHandler implements CefDisplayHandler {

    @Override
    public void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String s) {
    }

    @Override
    public void onTitleChange(CefBrowser cefBrowser, String s) {
    }

    @Override
    public boolean onTooltip(CefBrowser cefBrowser, String s) {
        return false;
    }

    @Override
    public void onStatusMessage(CefBrowser cefBrowser, String s) {
    }

    public boolean onCursorChange(CefBrowser cefBrowser, int i) {
        return false;
    }

    @Override
    public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String commandData, String s1, int i) {
        return WebviewCommandHandler.onConsoleCommand(commandData);
    }
}
