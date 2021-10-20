package com.software.codetime.toolwindows.codetime.html;

import com.intellij.openapi.editor.colors.EditorColorsManager;

public class CssUtil {

    public static String getGlobalStyle() {
        if (EditorColorsManager.getInstance().isDarkEditor()) {
            return getDarkStyle();
        }
        return getLightStyle();
    }

    private static String getDarkStyle() {
        return "  <style type=\"text/css\">\n" +
                "    body { background-color: #2e2e2e; color: #fafafa; font-family: 'Inter', sans-serif; }\n" +
                "  </style>\n";
    }

    private static String getLightStyle() {
        return "  <style type=\"text/css\">\n" +
                "    body { font-family: 'Inter', sans-serif; }\n" +
                "  </style>\n";
    }
}
