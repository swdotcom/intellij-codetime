package com.software.codetime.toolwindows.codetime.html;

import com.software.codetime.managers.FlowManager;
import com.software.codetime.managers.SessionDataManager;
import com.software.codetime.managers.StatusBarManager;
import org.apache.commons.lang3.StringUtils;
import swdc.java.ops.http.FlowModeClient;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.manager.SlackManager;
import swdc.java.ops.model.Integration;
import swdc.java.ops.model.Org;
import swdc.java.ops.model.Team;
import swdc.java.ops.model.TeamMember;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DomBuilder {

    public static String getMainHtml() {

        return "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                getMetaDataHeader() +
                "  <body>\n" +
                "    <div id=\"content\">\n" +
                getFlowComponent() +
                getStatsComponent() +
                getAccountComponent() +
                getTeamComponent() +
                "    </div>\n" +
                "  <canvas id=\"canvas\"></canvas>\n" +
                JsUtil.getJsDependencies() +
                "  </body>\n" +
                "</html>";
    }

    private static String getMetaDataHeader() {
        return "<head>\n" +
                "    <!-- Required meta tags -->\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "    <!-- Bootstrap CSS -->\n" +
                "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x\" crossorigin=\"anonymous\">\n" +
                CssUtil.getGlobalStyle() +
                "    <title>Code Time</title>\n" +
                "  </head>\n";
    }

    private static String getFlowComponent() {
        // if not registered, get the getting started comp
        if (StringUtils.isBlank(FileUtilManager.getItem("name"))) {
            return getFlowModeGettingStartedComponent();
        } else if (!SlackManager.hasSlackWorkspaces() && !FileUtilManager.getBooleanItem("intellij_CtskipSlackConnect")) {
            return getSlackGettingStartedComponent();
        }
        return getFlowModeComponent();
    }

    private static String getStatsComponent() {
        String inFlowIndicator = SessionDataManager.isCloseToOrAboveAverage() ? getInFlowIndicator() : "\n";
        return "<div class=\"card mb-0 pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Stats</h6>\n" +
                "    <p class=\"card-text mb-1 text-muted text-nowrap\">Data in your editor</p>\n" +
                inFlowIndicator +
                "  </div>\n" +
                getStatsListItems() +
                "</div>\n";
    }

    private static String getInFlowIndicator() {
        return "    <div class=\"top-right\">\n" +
                "      <button type=\"button\" class=\"icon-button\" aria-label=\"In flow!\" onclick=\"fireworks()\" data-bs-toggle=\"tooltip\" data-bs-placement=\"left\" data-bs-html=\"true\" title=\"<div>Great job!</div><div>Active code time is above average.</div>\">\n" +
                "        <span aria-hidden=\"false\">\n" +
                IconUtil.getRocketFaIcon() +
                "        </span>\n" +
                "      </button>\n" +
                "    </div>\n";
    }

    private static String getTeamComponent() {
        List<Team> teams = getTeams();
        if (teams == null || teams.size() == 0) {
            return getTeamGettingStartedComponent();
        }
        return getTeamListComponent();
    }

    private static String getTeamGettingStartedComponent() {
        return "<div class=\"card m-3 bg-dark bg-gradient text-white\" style=\"border-radius: 4px;\">\n" +
                "  <div class=\"card-body\">\n" +
                "    <h6 class=\"card-title text-nowrap\">\n" +
                "      <span class=\"right-padding-4\">\n" +
                IconUtil.getRocketFaIcon() +
                "       </span>\n" +
                "       Software Teams</h6>\n" +
                "    <p class=\"card-text text-muted fs-7\">Discover your team's best day for coding, and more.</p>\n" +
                "  </div>\n" +
                "  <div class=\"d-grid gap-2 col-8 mx-auto\">\n" +
                "    <button type=\"button\" class=\"btn btn-primary text-nowrap bb-text\" onclick=\"onCmdClick('create_team')\">\n" +
                "      Create a team\n" +
                "    </button>\n" +
                "  </div>\n" +
                "  <div class=\"card-body\">\n" +
                "    <p class=\"card-text fs-7\">Trust and data privacy matter. Your individual data is always private.</p>\n" +
                "  </div>\n" +
                "</div>\n";
    }

    private static String getFlowModeGettingStartedComponent() {
        return "<div class=\"card mb-0 pb-2 getting-started-bg\">\n" +
                "  <div class=\"card-body mb-2\">\n" +
                "    <h6 class=\"card-title mb-4 text-nowrap\">Getting Started</h6>\n" +
                "    <div class=\"progress pg-track-bg\" >\n" +
                "      <div class=\"progress-bar pg-track\" role=\"progressbar\" style=\"width: 40%;\" aria-valuenow=\"40\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "  <div class=\"d-grid gap-2 col-6 mx-auto mb-2\">\n" +
                "    <button type=\"button\" class=\"btn btn-primary text-nowrap bb-text getting-started-button\" onclick=\"onCmdClick('register')\">\n" +
                "      Register your account\n" +
                "    </button>\n" +
                "  </div>\n" +
                "  <div class=\"card-body mb-0 fs-7\">\n" +
                "    <p>Already have an account?<span><a href=\"#\" class=\"white-link\" style=\"padding-left: 5px\" onclick=\"onCmdClick('login')\">Log in</a></span></p>\n" +
                "  </div>\n" +
                "</div>\n";
    }

    private static String getSlackGettingStartedComponent() {
        return "<div class=\"card mb-0 pb-2 getting-started-bg\">\n" +
                "  <div class=\"card-body mb-2\">\n" +
                "    <h6 class=\"card-title mb-4 text-nowrap\">Getting Started</h6>\n" +
                "    <div class=\"progress pg-track-bg\" style=\"height: 6px; background-color: #a1cbf5\">\n" +
                "      <div class=\"progress-bar pg-track\" role=\"progressbar\" style=\"width: 85%;\" aria-valuenow=\"85\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "  <div class=\"d-grid gap-2 mx-auto mb-2\">\n" +
                "    <button type=\"button\" class=\"btn btn-primary text-nowrap bb-text getting-started-button\" onclick=\"onCmdClick('add_workspace')\">\n" +
                "      Connect a Slack Workspace\n" +
                "    </button>\n" +
                "  </div>\n" +
                "  <div class=\"card-body mb-0 fs-7\">\n" +
                "    <p>Not using slack?<span><a href=\"#\" class=\"white-link\" style=\"padding-left: 5px\" onclick=\"onCmdClick('skip_slack_connect')\">Skip this step.</a></span></p>\n" +
                "  </div>\n" +
                "</div>\n";
    }

    private static String getFlowModeComponent() {
        String flowModeLabel = "Enter Flow Mode";
        String flowModeIcon = IconUtil.getFlowModeOffFaIcon();
        if (FlowModeClient.isFlowModeOn()) {
            FlowManager.initFlowStatus(true);
            flowModeLabel = "Exit Flow Mode";
            flowModeIcon = IconUtil.getFlowModeOnFaIcon();
        }
        return "<div class=\"card mb-0 pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Flow Mode</h6>\n" +
                "    <p class=\"card-text mb-1 text-muted text-nowrap\">Block out distractions</p>\n" +
                "    <div class=\"top-right\">\n" +
                "      <button type=\"button\" class=\"icon-button\" aria-label=\"Settings\" onclick=\"onCmdClick('configure')\" data-bs-toggle=\"tooltip\" data-bs-placement=\"left\" data-bs-html=\"true\" title=\"<div>Configure settings</div>\">\n" +
                "        <span aria-hidden=\"false\">\n" +
                IconUtil.getSettingsSvg() +
                "        </span>\n" +
                "      </button>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "  <div class=\"d-grid gap-2 col-8 mx-auto\">\n" +
                "    <button type=\"button\" class=\"btn btn-primary text-nowrap bb-text\" onclick=\"onCmdClick('toggle_flow')\">\n" +
                flowModeIcon + "\n" +
                flowModeLabel + "\n" +
                "    </button>\n" +
                "  </div>\n" +
                "</div>\n";
    }

    private static String getAccountComponent() {
        String email = FileUtilManager.getItem("name");
        String emailDiv = (StringUtils.isNotBlank(email)) ? "    <p class=\"card-text mb-1 text-muted text-nowrap\">" + email + "</p>\n" : "\n";
        return "<div class=\"card mb-0 pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Account</h6>\n" +
                emailDiv +
                "  </div>\n" +
                getAccountListItems() +
                "</div>\n";
    }

    private static String getTeamListComponent() {
        return "<div class=\"card mb-0 pb-2\">\n" +
                "  <div class=\"card-body mb-0 pb-1\">\n" +
                "    <h6 class=\"card-title mb-1 text-nowrap\">Teams</h6>\n" +
                "    <p class=\"card-text mb-1 text-muted text-nowrap\">View your team dashboard</p>\n" +
                "  </div>\n" +
                getTeamListItems() +
                "</div>\n";
    }

    private static String getTeamButtonListItem(String label, String org_name, long team_id) {
        return "     <button id=\"`team_${team_id}`\" type=\"button\" class=\"list-group-item list-group-item-action shadow-none text-nowrap p-2 cursor-pointer\" onclick=\"teamClickHandler('" + org_name + "', " + team_id + ")\">\n" +
                "        <span class=\"right-padding-4\">\n" +
                IconUtil.getTeamSvg() +
                "        </span>\n" +
                label +
                "    </button>\n";
    }

    private static String getWorkspaceButtonListItem(String team_name, String team_domain, long id) {
        return "<div class=\"row\">\n" +
                "  <div class=\"col\">\n" +
                "    <button id=\"`team_${team_id}`\" type=\"button\" class=\"list-group-item list-group-item-action shadow-none text-nowrap p-2 cursor-pointer\">\n" +
                "      <span class=\"right-padding-4\">\n" +
                IconUtil.getSlackFaIcon() +
                "       </span>\n" +
                team_domain + " - " + team_name +
                "    </button>\n" +
                "  </div>\n" +
                "  <div class=\"col\">\n" +
                "    <div class=\"float-end pt-2\">\n" +
                "        <button type=\"button\" class=\"icon-button\" data-dismiss=\"modal\" aria-label=\"Settings\" onclick=\"workspaceRemoveClickHandler(" + id + ")\">\n" +
                "          <span aria-hidden=\"true\">\n" +
                IconUtil.getMinusCircleFaIcon() +
                "          </span>\n" +
                "        </button>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>\n";

    }

    private static String getCommandButtonItem(String svg, String label, String cmd) {
        return "     <button type=\"button\" class=\"list-group-item list-group-item-action shadow-none text-nowrap p-2 cursor-pointer\" onclick=\"onCmdClick('" + cmd + "')\">\n" +
                "        <span class=\"right-padding-4\">\n" +
                svg +
                "        </span>\n" +
                label +
                "    </button>\n";
    }

    private static String getCollapseButtonItem(String svg, String label, String accordionChildrenItems) {
        return "<div class=\"accordion accordion-flush\" id=\"workspaceItems\">\n" +
                "  <div class=\"accordion-item\">\n" +
                "     <button type=\"button\" class=\"accordion-button collapsed shadow-none text-nowrap p-2\" data-bs-toggle=\"collapse\" data-bs-target=\"#workspacesBody\" aria-expanded=\"false\" aria-controls=\"workspacesBody\">\n" +
                "        <span class=\"right-padding-4\">\n" +
                svg +
                "        </span>\n" +
                label +
                "    </button>\n" +
                "    <div id=\"workspacesBody\" class=\"accordion-collapse collapse\" aria-labelledby=\"headingOne\" data-bs-parent=\"#workspaceItems\">\n" +
                "      <div class=\"accordion-body\">\n" +
                accordionChildrenItems +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>\n";
    }

    private static String getButtonListItemContainer(String listItems) {
        return "<div class=\"list-group mx-2 mt-0\">\n" +
                listItems +
                "</div>\n";
    }

    private static List<Team> getTeams() {
        String email = FileUtilManager.getItem("name");
        List<Team> teams = new ArrayList<>();
        List<Org> orgs = OpsHttpClient.getOrganizations(FileUtilManager.getItem("jwt"));
        if (orgs != null && orgs.size() > 0) {
            for (Org org : orgs) {
                if (org.teams != null && org.teams.size() > 0) {

                    for (Team team : org.teams) {
                        List<TeamMember> members = team.team_members.stream().filter((TeamMember n) -> n.email.equals(email)).collect(Collectors.toList());
                        if (members == null || members.size() == 0) {
                            continue;
                        }
                        if (StringUtils.isBlank(team.org_name)) {
                            team.org_name = org.name;
                        }
                        teams.add(team);
                    }
                }
            }
        }
        return teams;
    }

    private static String getTeamListItems() {
        List<Team> teams = getTeams();
        StringBuilder sb = new StringBuilder();
        for (Team team : teams) {
            sb.append(getTeamButtonListItem(team.name, team.org_name, team.id));
        }
        return getButtonListItemContainer(sb.toString());
    }

    private static String getWorkspaceListItems() {
        List<Integration> workspaces = SlackManager.getSlackWorkspaces();

        StringBuilder sb = new StringBuilder();
        for (Integration workspace : workspaces) {
            sb.append(getWorkspaceButtonListItem(workspace.team_name, workspace.team_domain, workspace.id));
        }
        sb.append(getCommandButtonItem(IconUtil.getPlusCircleFaIcon(), "Add workspace", "add_workspace"));
        sb.append(getCommandButtonItem(IconUtil.getRefreshFaIcon(), "Refresh", "refresh_workspaces"));
        return getButtonListItemContainer(sb.toString());
    }

    private static String getAccountListItems() {
        String email = FileUtilManager.getItem("name");
        boolean isRegistered = (StringUtils.isNotBlank(email)) ? true : false;
        String toggleStatusLabel = StatusBarManager.showingStatusText() ? "Hide Code Time status" : "Show Code Time status";

        StringBuilder sb = new StringBuilder();

        if (isRegistered) {
            sb.append(getCommandButtonItem(IconUtil.getPawSvg(), "Switch account", "switch_account"));
            sb.append(getCommandButtonItem(IconUtil.getSettingsSvg(), "Configure settings", "configure"));
        }
        sb.append(getCommandButtonItem(IconUtil.getReadmeSvg(), "Documentation", "readme"));
        sb.append(getCommandButtonItem(IconUtil.getMessageSvg(), "Submit an issue", "submit_issue"));
        sb.append(getCommandButtonItem(IconUtil.getVisibleSvg(), toggleStatusLabel, "toggle_status"));
        sb.append(getCollapseButtonItem(IconUtil.getSlackSvg(), "Workspaces", getWorkspaceListItems()));
        return getButtonListItemContainer(sb.toString());
    }

    private static String getStatsListItems() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCommandButtonItem(IconUtil.getDashboardSvg(), "Dashboard", "dashboard"));
        sb.append(getCommandButtonItem(IconUtil.getPawSvg(), "More data at Software.com", "web_dashboard"));
        return getButtonListItemContainer(sb.toString());
    }
}
