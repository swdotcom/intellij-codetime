package com.software.codetime.toolwindows.codetime.html;

import com.google.gson.JsonObject;
import swdc.java.ops.manager.ConfigManager;
import swdc.java.ops.model.Team;

import java.util.List;

public class DomBuilder {

    public static String getTeamList() {
        List<Team> teams = Teams.getTeams();
        StringBuilder sb = new StringBuilder();
        for (Team team : teams) {
            sb.append(getTeamButtonListItem("fa-user-friends", team.name, team.org_name, team.id));
        }
        return getButtonListItemContainer(sb.toString());
    }

    public static String getGlobalStyle() {
        return "<style type=\"text/css\">\n" +
                "  .list-group-item { border: 0 none; }\n" +
                "  button:focus, button:active { outline: none; border-style: none; }\n" +
                "'</style>\n";
    }

    public static String getJsDependencies() {
        return "    <!-- jQuery first, then Popper.js, then Bootstrap JS -->\n" +
                "    <script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\"></script>\n" +
                "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\"></script>\n" +
                customJavascript();
    }

    public static String customJavascript() {
        return "      <script>\n" +
                "       const windowFeatures = \"menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes\";\n" +
                "       function teamClickHandler(org_name, team_id) {\n" +
                "         window.open(`" + ConfigManager.app_url + "/dashboard?org_name=${org_name}&team_id=${team_id}`, '_new', 'resizable,location,scrollbars,status,menubar');\n" +
                "       }\n" +
                "     </script>\n";
    }

    public static String getMetaDataHeader() {
        return "<head>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
                "    <script src=\"https://kit.fontawesome.com/ef435e26ef.js\" crossorigin=\"anonymous\"></script>\n" +
                "\n" +
                DomBuilder.getGlobalStyle() +
                "    <title>Code Time</title>\n" +
                "  </head>";
    }

    public static String getMainHtml() {
        return "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                getMetaDataHeader() +
                "  <body>\n" +
                GettingStarted.getHtml() +
                FlowMode.getHtml() +
                Stats.getHtml() +
                Account.getHtml() +
                DomBuilder.getTeamList() +
                getJsDependencies() +
                "  </body>\n" +
                "</html>";
    }

    /** PRIVATE METHODS **/

    private static String getTeamButtonListItem(String fontAwesomeClass, String label, String org_name, long team_id) {
        return "     <button type=\"button\" class=\"list-group-item list-group-item-action shadow-none\" onclick=\"teamClickHandler('" + org_name + "', " + team_id + ")\">\n" +
                "      <div class=\"md-v-line\"></div><i class=\"fas " + fontAwesomeClass + " mr-4 pr-3\"></i>" + label + "\n" +
                "    </button>\n";
    }

    private static String getButtonListItemContainer(String listItems) {
        return "<div class=\"list-group mx-2\">\n" +
                listItems +
                "</div>\n";
    }
}
