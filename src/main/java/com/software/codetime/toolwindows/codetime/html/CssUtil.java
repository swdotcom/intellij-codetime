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
                "    body { background-color: #2e2e2e; color: #fafafa; line-height: 1; font-size: .9rem; margin: 0; padding: 0; }\n" +
                "    canvas { display: block; position: relative; zindex: 1; pointer-events: none; }\n" +
                "    #content { background-color: #2e2e2e; position: absolute; top: 0; bottom: 0; width: 100%; height: 100%; }\n" +
                "    .right-padding-4 { padding-right: 4px; }\n" +
                "    .card { background-color: #2e2e2e; border-radius: 0; }\n" +
                "    .list-group { background-color: #2e2e2e; }\n" +
                "    .list-group-item, .list-group-item:hover { border: 0 none; background-color: #2e2e2e; color: #fafafa; }\n" +
                "    .accordion { background-color: #2e2e2e; color: #fafafa; }\n" +
                "    .accordion-item, .accordion-item:hover { border: 0 none; background-color: #2e2e2e; color: #fafafa; }\n" +
                "    .accordion-button, .accordion-button:hover { font-size: inherit; background-color: #2e2e2e; color: #fafafa; }\n" +
                "    .accordion-button:not(.collapsed) { background-color: #2e2e2e; color: #fafafa }\n" +
                "    .accordion-button::after { background-image: url(\"data:image/svg+xml;charset=UTF-8,%3c?xml version='1.0' encoding='iso-8859-1'?%3e%3c!-- Generator: Adobe Illustrator 16.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0) --%3e%3c!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' 'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'%3e%3csvg version='1.1' id='Capa_1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' x='0px' y='0px' width='12px' height='12px' viewBox='0 0 306 306' style='enable-background:new 0 0 306 306;' xml:space='preserve' fill='%23fafafa'%3e%3cg%3e%3cg id='expand-more'%3e%3cpolygon points='270.3,58.65 153,175.95 35.7,58.65 0,94.35 153,247.35 306,94.35 '/%3e%3c/g%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3c/svg%3e \"); }\n" +
                "    .accordion-button:not(.collapsed)::after { background-image: url(\"data:image/svg+xml;charset=UTF-8,%3c?xml version='1.0' encoding='iso-8859-1'?%3e%3c!-- Generator: Adobe Illustrator 16.0.0, SVG Export Plug-In . SVG Version: 6.00 Build 0) --%3e%3c!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' 'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'%3e%3csvg version='1.1' id='Capa_1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' x='0px' y='0px' width='12px' height='12px' viewBox='0 0 306 306' style='enable-background:new 0 0 306 306;' xml:space='preserve' fill='%23fafafa'%3e%3cg%3e%3cg id='expand-more'%3e%3cpolygon points='270.3,58.65 153,175.95 35.7,58.65 0,94.35 153,247.35 306,94.35 '/%3e%3c/g%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3cg%3e%3c/g%3e%3c/svg%3e \"); transform: rotate(-180deg); }\n" +
                "    .accordion-body { padding: 1px }\n" +
                "    .card > .list-group { border-style: none; }\n" +
                "    .bb-text { font-size: small; font-weight: 600; }\n" +
                "    .getting-started-bg { background-color: #2196f3; color: #fafafa; }\n" +
                "    .getting-started-button { background-color: #fafafa; color: #2196f3; }\n" +
                "    .pg-track-bg { height: 6px; background-color: #a1cbf5; }\n" +
                "    .pg-track { background-color: #fafafa; }\n" +
                "    button:focus, button:active { outline: none; border-style: none; background-color: #2e2e2e }\n" +
                "    a.white-link, a.white-link:active, a.white-link:hover {color: #fafafa; text-decoration: none; font-weight: bold; }\n" +
                "    .cursor-pointer { cursor: pointer; }\n" +
                "    .top-right { position: absolute; top: 18px; right: 16px; }\n" +
                "    .icon-button { padding: 0; background-color: transparent; border: 0; -webkit-appearance: none; cursor: pointer; }\n" +
                "    .fs-7 { font-size: .8rem !important; line-height: normal; }\n" +
                "  </style>\n";
    }

    private static String getLightStyle() {
        return "  <style type=\"text/css\">\n" +
                "    body { line-height: 1; font-size: .9rem; margin: 0; padding: 0; }\n" +
                "    canvas { display: block; position: relative; zindex: 1; pointer-events: none; }\n" +
                "    #content { position: absolute; top: 0; bottom: 0; width: 100%; height: 100%; }\n" +
                "    .right-padding-4 { padding-right: 4px; }\n" +
                "    .card { border-radius: 0; }\n" +
                "    .list-group-item { border: 0 none; }\n" +
                "    .accordion-item { border: 0 none; background-color: transparent; }\n" +
                "    .accordion-button { font-size: inherit; }\n" +
                "    .accordion-body { padding: 1px }\n" +
                "    .card > .list-group { border-style: none; }\n" +
                "    .bb-text { font-size: small; font-weight: 600; }\n" +
                "    .getting-started-bg { background-color: #2196f3; color: #ffffff; }\n" +
                "    .getting-started-button { background-color: #ffffff; color: #2196f3; }\n" +
                "    .pg-track-bg { height: 6px; background-color: #a1cbf5; }\n" +
                "    .pg-track { background-color: #ffffff; }\n" +
                "    button:focus, button:active { outline: none; border-style: none; }\n" +
                "    a.white-link, a.white-link:active, a.white-link:hover {color: #ffffff; text-decoration: none; font-weight: bold; }\n" +
                "    .cursor-pointer { cursor: pointer; }\n" +
                "    .top-right { position: absolute; top: 18px; right: 16px; }\n" +
                "    .icon-button { padding: 0; background-color: transparent; border: 0; -webkit-appearance: none; cursor: pointer; }\n" +
                "    .fs-7 { font-size: .8rem !important; line-height: normal; }\n" +
                "  </style>\n";
    }
}
