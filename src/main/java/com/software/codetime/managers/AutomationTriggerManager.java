package com.software.codetime.managers;

import com.google.common.reflect.TypeToken;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.software.codetime.main.PluginInfo;
import com.software.codetime.models.AutomationTrigger;
import swdc.java.ops.http.ClientResponse;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.AsyncManager;
import swdc.java.ops.manager.UtilManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AutomationTriggerManager {

    public static final Logger log = Logger.getLogger("AutomationTriggerManager");

    private static List<AutomationTrigger> triggers = new ArrayList<>();
    private static ScheduledFuture refreshTriggersFuture = null;

    public static void refreshEditorOpsAutomations() {
        triggers = new ArrayList<>();

        if (refreshTriggersFuture != null) {
            refreshTriggersFuture.cancel(false);
        }

        if (PluginInfo.isEditorOpsInstalled()) {

            // get the automations
            ClientResponse resp = OpsHttpClient.appGet("/plugin/automation_triggers.json");
            if (resp.isOk()) {
                // get the json array
                try {
                    Type listType = new TypeToken<List<AutomationTrigger>>() {
                    }.getType();
                    triggers = UtilManager.gson.fromJson(resp.getJsonStr(), listType);
                } catch (Exception e) {
                    log.log(Level.WARNING, e.getMessage());
                }
            }
        }

        refreshTriggersFuture = AsyncManager.getInstance().executeOnceInSeconds(() -> {
            AutomationTriggerManager.refreshEditorOpsAutomations();
        }, 60 * 60 /* in 1 hour */);
    }

    public static boolean hasEditorOpsAutoFlowModeTrigger() {
        List<AutomationTrigger> flowModeEnabledTriggers = AutomationTriggerManager.findByType("flow_mode_enabled");
        AutomationTrigger trigger = flowModeEnabledTriggers.stream()
                .filter(t -> t.enabled)
                .findAny()
                .orElse(null);
        return (trigger != null);
    }

    public static boolean hasEditorOpsAutoFlowModeDisabledTrigger() {
        List<AutomationTrigger> flowModeEnabledTriggers = AutomationTriggerManager.findByType("flow_mode_disabled");
        AutomationTrigger trigger = flowModeEnabledTriggers.stream()
                .filter(t -> t.enabled)
                .findAny()
                .orElse(null);
        return (trigger != null);
    }

    public static List<AutomationTrigger> findByType(String name) {
        List<AutomationTrigger> typeTriggers = triggers.stream()
                .filter(s -> (s.automation_trigger_type != null && s.automation_trigger_type.name.equals(name)))
                .collect(Collectors.toList());
        return typeTriggers == null ? new ArrayList<>() : typeTriggers;
    }
}
