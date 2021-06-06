package com.software.codetime.toolwindows.codetime.components;

import com.google.gson.JsonArray;
import swdc.java.ops.http.ClientResponse;
import swdc.java.ops.http.OpsHttpClient;
import swdc.java.ops.manager.FileUtilManager;

public class Teams {


    public static String getHtml() {
        JsonArray arr = getTeams();
        return "<div class=\"col-md-6 mb-4 borderless\">\n" +
                "    \n" +
                "        <ul class=\"list-group\">\n" +
                "            <li class=\"list-group-item active\">\n" +
                "              <div class=\"md-v-line\"></div><i class=\"fas fa-user-friends mr-4 pr-3\"></i> Cras justo odio\n" +
                "            </li>\n" +
                "            <li class=\"list-group-item\">\n" +
                "              <div class=\"md-v-line\"></div><i class=\"fas fa-user-friends mr-4 pr-3\"></i>Dapibus ac facilisis in\n" +
                "            </li>\n" +
                "            <li class=\"list-group-item\">\n" +
                "              <div class=\"md-v-line\"></div><i class=\"fas fa-user-friends mr-4 pr-3\"></i>Morbi leo risus\n" +
                "            </li>\n" +
                "            <li class=\"list-group-item\">\n" +
                "              <div class=\"md-v-line\"></div><i class=\"fas fa-user-friends mr-4 pr-3\"></i>Porta ac consectetur ac\n" +
                "            </li>\n" +
                "            <li class=\"list-group-item\">\n" +
                "              <div class=\"md-v-line\"></div><i class=\"fas fa-user-friends mr-4 pr-3\"></i>Vestibulum at eros\n" +
                "            </li>\n" +
                "          </ul>\n" +
                "    \n" +
                "    </div>";
    }

    private static JsonArray getTeams() {
        JsonArray arr = new JsonArray();
        ClientResponse resp = OpsHttpClient.softwareGet("/v1/organizations", FileUtilManager.getItem("jwt"));

        return arr;
    }

//    let teams = [];
//
//    export async function buildTeams() {
//        initializedCache = true;
//  const resp = await softwareGet("/v1/organizations", getItem("jwt"));
//        // synchronized team gathering
//        teams = isResponseOk(resp) ? await gatherTeamsFromOrgs(resp.data) : [];
//    }
//
//    export async function getCachedTeams() {
//        if (!initializedCache) {
//            await buildTeams();
//        }
//        return teams;
//    }
//
//    async function gatherTeamsFromOrgs(orgs) {
//        let org_teams = [];
//
//        if (orgs?.length) {
//            orgs.forEach((org) => {
//                    // add every team from each org
//                    org.teams?.forEach((team) => {
//                    org_teams.push({
//          ...team,
//                    org_name: org.name,
//                    org_id: org.id,
//        });
//      });
//    });
//        }
//        return org_teams;
//    }


}
