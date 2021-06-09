package com.software.codetime.managers;

import com.intellij.openapi.util.IconLoader;
import org.apache.commons.lang.StringUtils;
import swdc.java.ops.snowplow.events.UIInteractionType;

import javax.swing.*;

public class AuthPromptManager {

    public static void initiateSwitchAccountFlow() {
        initiateAuthFlow("Switch account", "Switch to a different account?");
    }

    public static void initiateSignupFlow() {
        initiateAuthFlow("Sign up", "Sign up using...");
    }

    public static void initiateLoginFlow() {
        initiateAuthFlow("Log in", "Log in using...");
    }

    private static void initiateAuthFlow(String title, String message) {
        String[] options = new String[]{ "Google", "GitHub", "Email" };
        Icon icon = IconLoader.getIcon("/assets/app-icon-blue.png");
        String input = (String) JOptionPane.showInputDialog(
                null,
                message,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                icon,
                options, // Array of choices
                options[0]); // Initial choice
        if (StringUtils.isNotBlank(input)) {
            UserSessionManager.launchLogin(input.toLowerCase(), UIInteractionType.click, true);
        }

    }
}
