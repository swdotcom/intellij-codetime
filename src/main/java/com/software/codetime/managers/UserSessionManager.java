package com.software.codetime.managers;

import com.google.gson.JsonObject;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.software.codetime.toolwindows.codetime.CodeTimeWindowFactory;
import org.apache.commons.lang.StringUtils;
import swdc.java.ops.http.ClientResponse;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.*;
import swdc.java.ops.model.UserState;
import swdc.java.ops.snowplow.entities.UIElementEntity;
import swdc.java.ops.snowplow.events.UIInteractionType;
import swdc.java.ops.websockets.handlers.AuthenticatedPluginUser;

import javax.swing.*;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Logger;

public class UserSessionManager {

    private static UserSessionManager instance = null;
    public static final Logger log = Logger.getLogger("SoftwareCoSessionManager");
    private static long lastAppAvailableCheck = 0;

    public static UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }


    public static String getReadmeFile() {
        String file = FileUtilManager.getSoftwareDir(true);
        if (UtilManager.isWindows()) {
            file += "\\jetbrainsCt_README.txt";
        } else {
            file += "/jetbrainsCt_README.txt";
        }
        return file;
    }

    public synchronized static boolean isServerOnline() {
        ClientResponse resp = OpsHttpClient.softwareGet("/ping", null);
        if (resp != null && resp.isOk()) {
            return true;
        }
        return false;
    }

    private Project getCurrentProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects != null && projects.length > 0) {
            return projects[0];
        }
        return null;
    }

    public void statusBarClickHandler() {
        UIElementEntity elementEntity = new UIElementEntity();
        elementEntity.element_name = "ct_status_bar_metrics_btn";
        elementEntity.element_location = "ct_status_bar";
        elementEntity.color = null;
        elementEntity.cta_text = "status bar metrics";
        elementEntity.icon_name = "clock";
        EventTrackerManager.getInstance().trackUIInteraction(UIInteractionType.click, elementEntity);
        CodeTimeWindowFactory.openToolWindow();
    }

    public static void launchLogin(String loginType, UIInteractionType interactionType, boolean switching_account) {

        String auth_callback_state = FileUtilManager.getAuthCallbackState(true);

        FileUtilManager.setBooleanItem("switching_account", switching_account);

        String plugin_uuid = FileUtilManager.getPluginUuid();

        JsonObject obj = new JsonObject();
        obj.addProperty("plugin", "codetime");
        obj.addProperty("plugin_uuid", plugin_uuid);

        obj.addProperty("pluginVersion", ConfigManager.plugin_version);
        obj.addProperty("plugin_id", ConfigManager.plugin_id);
        obj.addProperty("auth_callback_state", auth_callback_state);
        obj.addProperty("redirect", ConfigManager.app_url);

        String url = "";
        String element_name = "ct_sign_up_google_btn";
        String icon_name = "google";
        String cta_text = "Sign up with Google";
        String icon_color = null;
        if (loginType == null || loginType.equals("software") || loginType.equals("email")) {
            element_name = "ct_sign_up_email_btn";
            cta_text = "Sign up with email";
            icon_name = "envelope";
            icon_color = "gray";
            url = ConfigManager.app_url + "/email-signup";
        } else if (loginType.equals("google")) {
            url = ConfigManager.app_endpoint + "/auth/google";
        } else if (loginType.equals("github")) {
            element_name = "ct_sign_up_github_btn";
            cta_text = "Sign up with GitHub";
            icon_name = "github";
            url = ConfigManager.app_endpoint + "/auth/github";
        }

        StringBuffer sb = new StringBuffer();
        Iterator<String> keys = obj.keySet().iterator();
        while(keys.hasNext()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            String key = keys.next();
            String val = obj.get(key).getAsString();
            try {
                val = URLEncoder.encode(val, "UTF-8");
            } catch (Exception e) {
                log.info("Unable to url encode value, error: " + e.getMessage());
            }
            sb.append(key).append("=").append(val);
        }
        url += "?" + sb.toString();

        FileUtilManager.setItem("authType", loginType);

        BrowserUtil.browse(url);

        UIElementEntity elementEntity = new UIElementEntity();
        elementEntity.element_name = element_name;
        elementEntity.element_location = interactionType == UIInteractionType.click ? "ct_menu_tree" : "ct_command_palette";
        elementEntity.color = icon_color;
        elementEntity.cta_text = cta_text;
        elementEntity.icon_name = icon_name;
        EventTrackerManager.getInstance().trackUIInteraction(interactionType, elementEntity);

        AsyncManager.getInstance().executeOnceInSeconds(() -> {
            checkAuthCompletion(10);}, 15);
    }

    private static void checkAuthCompletion(int tries) {
        if (tries < 0) {
            return;
        }
        UserState userState = AccountManager.getUserLoginState(false /*isIntegrationRequest*/);
        if (!userState.loggedIn) {
            tries--;
            final int newTryCount = tries;
            AsyncManager.getInstance().executeOnceInSeconds(() -> {
                checkAuthCompletion(newTryCount);
            }, 15);
        } else {
            AuthenticatedPluginUser.handleAuthenticatedPluginUser(userState.user);
        }
    }

    public static void launchWebDashboard(UIInteractionType interactionType) {
        if (StringUtils.isBlank(FileUtilManager.getItem("name"))) {
            ApplicationManager.getApplication().invokeLater(() -> {
                String msg = "Sign up or log in to see more data visualizations.";

                Object[] options = {"Sign up"};
                int choice = JOptionPane.showOptionDialog(
                        null, msg, "Sign up", JOptionPane.OK_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice == 0) {
                    AuthPromptManager.initiateSignupFlow();
                }
            });
            return;
        }

        String url = ConfigManager.app_url + "/login";
        BrowserUtil.browse(url);

        UIElementEntity elementEntity = new UIElementEntity();
        elementEntity.element_name = interactionType == UIInteractionType.click ? "ct_web_metrics_btn" : "ct_web_metrics_cmd";
        elementEntity.element_location = interactionType == UIInteractionType.click ? "ct_menu_tree" : "ct_command_palette";
        elementEntity.color = interactionType == UIInteractionType.click ? "gray" : null;
        elementEntity.cta_text = "See advanced metrics";
        elementEntity.icon_name = interactionType == UIInteractionType.click ? "paw" : null;
        EventTrackerManager.getInstance().trackUIInteraction(interactionType, elementEntity);
    }
}

