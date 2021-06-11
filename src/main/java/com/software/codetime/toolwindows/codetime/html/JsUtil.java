package com.software.codetime.toolwindows.codetime.html;

import com.software.codetime.managers.SessionDataManager;

public class JsUtil {

    public static String getJsDependencies() {
        return "    <!-- Popper.js then Bootstrap JS -->\n" +
                "    <script src=\"https://kit.fontawesome.com/ef435e26ef.js\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js\" integrity=\"sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.min.js\" integrity=\"sha384-Atwg2Pkwv9vp0ygtn1JAojH0nYbwNJLPhwyoVbhoPwBhjQPR5VtM2+xf0Uwh9KtT\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/canvas-confetti@1.4.0/dist/confetti.browser.min.js\"></script>\n" +
                getConfettiJs() + "\n" +
                getButtonFunctionsJs() + "\n";
    }

    private static String getButtonFunctionsJs() {
        // this would start the fireworks when the window loads and would be
        // added after the workspaceRemoveClickHandler function if we wanted to use it.
        // String onloadFn = "      window.onload = function(e) { fireworks(); }\n";
        return "      <script language=\"javascript\">\n" +
                "       const windowFeatures = \"menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes\";\n" +
                "       function teamClickHandler(org_name, team_id) {\n" +
                "         console.log(JSON.stringify({cmd: 'launch_team', org_name, team_id}));\n" +
                "       }\n" +
                "       function onCmdClick(cmd) {\n" +
                "         console.log(JSON.stringify({cmd}));\n" +
                "         console.log('starting confetti');\n" +
                "       }\n" +
                "       function workspaceRemoveClickHandler(id) {\n" +
                "         console.log(JSON.stringify({cmd: 'remove_workspace', id}));\n" +
                "       }\n" +
                "       window.onload = function(e) {\n" +
                "         var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle=\"tooltip\"]'))\n" +
                "         var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {\n" +
                "           return new bootstrap.Tooltip(tooltipTriggerEl)\n" +
                "         })\n" +
                "       }\n" +
                "     </script>\n";
    }

    private static String getConfettiJs() {
        return "      <script language=\"javascript\">\n" +
                "function fireworks() {\n" +
                "  var duration = 5 * 1000;\n" +
                "  var animationEnd = Date.now() + duration;\n" +
                "  var defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 0 };\n" +
                "\n" +
                "  function randomInRange(min, max) {\n" +
                "    return Math.random() * (max - min) + min;\n" +
                "  }\n" +
                "\n" +
                "  var interval = setInterval(function() {\n" +
                "    var timeLeft = animationEnd - Date.now();\n" +
                "\n" +
                "    if (timeLeft <= 0) {\n" +
                "      return clearInterval(interval);\n" +
                "    }\n" +
                "\n" +
                "    var particleCount = 50 * (timeLeft / duration);\n" +
                "    // since particles fall down, start a bit higher than random\n" +
                "    confetti(Object.assign({}, defaults, { particleCount, origin: { x: randomInRange(0.1, 0.3), y: Math.random() - 0.2 } }));\n" +
                "    confetti(Object.assign({}, defaults, { particleCount, origin: { x: randomInRange(0.7, 0.9), y: Math.random() - 0.2 } }));\n" +
                "  }, 250);\n" +
                "}\n" +
                "     </script>\n";
    }
}
