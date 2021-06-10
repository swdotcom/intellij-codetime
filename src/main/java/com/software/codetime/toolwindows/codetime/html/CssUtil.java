package com.software.codetime.toolwindows.codetime.html;

public class CssUtil {

    public static String getGlobalStyle() {
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
