package com.software.codetime.toolwindows.codetime.html;

public class LoadError {
    public static String get404Html() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" shrink-to-fit=no\">\n" +
                "    <title>Code Time</title>\n" +
                CssUtil.getGlobalStyle() +
                "    <style>\n" +
                "            * {\n" +
                "              box-sizing: border-box;\n" +
                "            }\n" +
                "\n" +
                "            *, ::before, ::after {\n" +
                "                --tw-shadow: 0 0 #0000;\n" +
                "            }\n" +
                "\n" +
                "            *, ::before, ::after {\n" +
                "                --tw-border-opacity: 1;\n" +
                "                border-color: rgba(228, 228, 231, var(--tw-border-opacity));\n" +
                "            }\n" +
                "            *, ::before, ::after {\n" +
                "                box-sizing: border-box;\n" +
                "                border-width: 0;\n" +
                "                border-style: solid;\n" +
                "                border-color: currentColor;\n" +
                "            }\n" +
                "\n" +
                "            h1,\n" +
                "            h2,\n" +
                "            h3,\n" +
                "            h4,\n" +
                "            p {\n" +
                "              margin: 0;\n" +
                "              padding: 0;\n" +
                "            }\n" +
                "\n" +
                "            .wrapper {\n" +
                "              display: flex;\n" +
                "              flex-direction: column;\n" +
                "              justify-content: center;\n" +
                "              align-items: center;\n" +
                "              padding-top: 8px;\n" +
                "              padding-bottom: 8px;\n" +
                "            }\n" +
                "\n" +
                "            .header {\n" +
                "              margin-bottom: 1rem;\n" +
                "            }\n" +
                "\n" +
                "            .dialog {\n" +
                "              margin-bottom: 2rem;\n" +
                "            }\n" +
                "\n" +
                "            .body-text {\n" +
                "              margin-bottom: 1rem;\n" +
                "            }\n" +
                "\n" +
                "            img {\n" +
                "              border-radius: 10px;\n" +
                "              margin-bottom: 1rem;\n" +
                "            }\n" +
                "            .link {\n" +
                "              --tw-text-opacity: 1;\n" +
                "              color: rgba(37, 99, 235, var(--tw-text-opacity));\n" +
                "            }\n" +
                "          </style>\n" +
                "    <script language=\"javascript\">\n" +
                "        function onCmdClick(action, payload = {}) {\n" +
                "            console.log(JSON.stringify({cmd: action, ...payload}));\n" +
                "        }\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"wrapper\">\n" +
                "    <h4 class=\"header\">Oops! Something went wrong.</h4>\n" +
                "    <div class=\"dialog\">\n" +
                "        <p class=\"body-text\" style=\"margin-top: 10px\">\n" +
                "            Please check your connection and try again later.\n" +
                "        </p>\n" +
                "    </div>\n" +
                "    <div style=\"margin-bottom: 10px\">\n" +
                "        <a href=\"#\" class=\"link\" onclick=\"onCmdClick('refreshCodeTimeView')\">Refresh</a>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }
}
