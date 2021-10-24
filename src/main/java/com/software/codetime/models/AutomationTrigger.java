package com.software.codetime.models;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AutomationTrigger {
    public long id = 0;
    public String name = "";
    public String description = "";
    public boolean enabled = true;
    public JsonObject configuration = new JsonObject();
    public AutomationTriggerType automation_trigger_type = new AutomationTriggerType();
    public List<AutomationAction> automation_actions = new ArrayList<>();
}
