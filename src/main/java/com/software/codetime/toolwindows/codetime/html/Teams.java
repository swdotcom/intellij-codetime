package com.software.codetime.toolwindows.codetime.html;

import org.apache.commons.lang3.StringUtils;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.FileUtilManager;
import swdc.java.ops.model.Org;
import swdc.java.ops.model.Team;

import java.util.ArrayList;
import java.util.List;

public class Teams {

    private static List<Team> teams = new ArrayList<>();

    public static void clearTeams() {
        teams.clear();
    }

    public static List<Team> getTeams() {
        buildTeams();
        return teams;
    }

    public static void buildTeams() {
        if (teams.isEmpty()) {
            List<Org> orgs = OpsHttpClient.getOrganizations(FileUtilManager.getItem("jwt"));
            if (orgs != null && orgs.size() > 0) {
                for (Org org : orgs) {
                    if (org.teams != null && org.teams.size() > 0) {
                        for (Team team : org.teams) {
                            if (StringUtils.isBlank(team.org_name)) {
                                team.org_name = org.name;
                            }
                            teams.add(team);
                        }
                    }
                }
            }
        }
    }

}
