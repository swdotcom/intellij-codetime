package com.software.codetime.main;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.util.PlatformUtils;

public class PluginInfo {

    // set the api endpoint to use
    public final static String api_endpoint = "https://api.software.com";
    // set the launch url to use
    public final static String launch_url = "https://app.software.com";
    public final static String software_dir = ".software";

    public static String IDE_NAME = "";
    public static String IDE_VERSION = "";

    // sublime = 1, vs code = 2, eclipse = 3, intellij = 4, visual studio = 6, atom = 7
    private static int pluginId = 4;
    public static String VERSION = null;
    public static String pluginName = null;

    private static int APPCODE_ID = 22;
    private static int CLION_ID = 24;
    private static int DATAGRIP_ID = 26;
    private static int GOLAND_ID = 28;
    private static int PHPSTORM_ID = 30;
    private static int PYCHARM_ID = 32;
    private static int RIDER_ID = 34;
    private static int RUBYMINE_ID = 36;
    private static int WEBSTORM_ID = 38;

    static {
        try {
            IDE_NAME = PlatformUtils.getPlatformPrefix();
            IDE_VERSION = ApplicationInfo.getInstance().getFullVersion();
        } catch (Exception e) {
            System.out.println("Unable to retrieve IDE name and version info: " + e.getMessage());
        }
    }

    public static int getPluginId() {
        if (PlatformUtils.isIntelliJ()) {
            return pluginId;
        } else if (PlatformUtils.isPyCharm()) {
            return PYCHARM_ID;
        } else if (PlatformUtils.isAppCode()) {
            return APPCODE_ID;
        } else if (PlatformUtils.isCLion()) {
            return CLION_ID;
        } else if (PlatformUtils.isDataGrip()) {
            return DATAGRIP_ID;
        } else if (PlatformUtils.isGoIde()) {
            return GOLAND_ID;
        } else if (PlatformUtils.isPhpStorm()) {
            return PHPSTORM_ID;
        } else if (PlatformUtils.isRubyMine()) {
            return RUBYMINE_ID;
        } else if (PlatformUtils.isRider()) {
            return RIDER_ID;
        } else if (PlatformUtils.isWebStorm()) {
            return WEBSTORM_ID;
        }
        return pluginId;
    }

    public static String getVersion() {
        if (VERSION == null) {
            IdeaPluginDescriptor pluginDescriptor = getIdeaPluginDescriptor();
            if (pluginDescriptor != null) {
                VERSION = pluginDescriptor.getVersion();
            } else {
                return "2.6.9";
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
