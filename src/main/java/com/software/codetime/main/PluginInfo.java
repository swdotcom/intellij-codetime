package com.software.codetime.main;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.util.PlatformUtils;

public class PluginInfo {

    // set the api endpoint to use
    public final static String api_endpoint = "http://localhost:5000";//"https://api.software.com";
    // set the launch url to use
    public final static String launch_url = "http://localhost:3000";//"https://app.software.com";
    public final static String software_dir = ".software-local";//".software";

    public static String IDE_NAME = "";
    public static String IDE_VERSION = "";

    // sublime = 1, vs code = 2, eclipse = 3, intellij = 4, visual studio = 6, atom = 7
    public static int pluginId = 4;
    public static String VERSION = null;
    public static String pluginName = null;

    static {
        try {
            IDE_NAME = PlatformUtils.getPlatformPrefix();
            IDE_VERSION = ApplicationInfo.getInstance().getFullVersion();
        } catch (Exception e) {
            System.out.println("Unable to retrieve IDE name and version info: " + e.getMessage());
        }
    }

    public static String getVersion() {
        if (VERSION == null) {
            IdeaPluginDescriptor pluginDescriptor = getIdeaPluginDescriptor();
            if (pluginDescriptor != null) {
                VERSION = pluginDescriptor.getVersion();
            } else {
                return "2.0.1";
            }
        }
        return VERSION;
    }

    public static String getPluginName() {
        if (pluginName == null) {
            IdeaPluginDescriptor pluginDescriptor = getIdeaPluginDescriptor();
            if (pluginDescriptor != null) {
                pluginName = pluginDescriptor.getName();
            } else {
                return "CodeTime";
            }
        }
        return pluginName;
    }

    private static IdeaPluginDescriptor getIdeaPluginDescriptor() {
        IdeaPluginDescriptor[] descriptors = PluginManager.getPlugins();
        if (descriptors != null && descriptors.length > 0) {
            for (IdeaPluginDescriptor descriptor : descriptors) {
                if (descriptor.getPluginId().getIdString().equals("com.softwareco.intellij.plugin")) {
                    return descriptor;
                }
            }
        }
        return null;
    }
}
