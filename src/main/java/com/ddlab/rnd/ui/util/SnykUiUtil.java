package com.ddlab.rnd.ui.util;

import com.ddlab.rnd.snyk.model.OrgDetails;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SnykUiUtil {

    public static void check() {
//        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
//        for (Project project : openProjects) {
//            System.out.println("Open project: " + project.getName());
//        }

        Project project = ProjectManager.getInstance().getDefaultProject();
        System.out.println("Default project: " + project.getName());

        ProgressManager.getInstance().run(new Task.Modal(null, "Running Test...", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Please wait, running test...");

                // Simulate long-running work
                try {
                    Thread.sleep(5000); // replace with your actual logic
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });

    }
    public static List<String> getSnykOrgGroupNames(String snykCoreUri, String snykAuthToken) {
//        check();
        List<String> orgGroupNames = null;
        try {
            snykCoreUri = snykCoreUri.endsWith("/") ? snykCoreUri +"orgs" : snykCoreUri+"/orgs";
            snykAuthToken = !snykAuthToken.startsWith("token ") ? "token " + snykAuthToken : snykAuthToken;
            log.debug("Snyk Core URI: " + snykCoreUri);
            log.debug("Snyk Auth Token: " + snykAuthToken);
            String responseBody = getSnykOrgsResponseAsText(snykCoreUri, snykAuthToken);
            log.debug("Snyk OrgResponse Body: " + responseBody);
            ObjectMapper mapper = new ObjectMapper();
            OrgDetails orgDetails = mapper.readValue(responseBody, OrgDetails.class);
            orgGroupNames = orgDetails.getOrgs().stream().map(value -> {
                String orgId = value.getId();
                String orgName = value.getName();
                String groupName = value.getGroup().getName();
                return orgId + "~" + orgName + "~" + groupName;
            }).collect(Collectors.toList());
            log.debug("Org and Group Names: " + orgGroupNames);
        } catch (Exception e) {
            log.error("Unable to fetch Snyk org and group names: ", e);
            e.printStackTrace();
            Messages.showErrorDialog("Unable to fetch Snyk Org and Group name/s, please check the Snyk details ", "SnykoMycin Error");
        }
        return orgGroupNames;
    }

    public static String getSnykOrgsResponseAsText(String snykCoreUri, String snykAuthToken) throws Exception {
        log.debug("Actual Call Snyk Core URI: " + snykCoreUri);
        log.debug("Actual Call Snyk Auth Token: " + snykAuthToken);
        String responseBody = null;
        HttpResponse<String> response = null;
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(snykCoreUri))
                    .header("Content-Type", "application/json").header("Authorization", snykAuthToken)
                    .GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Unable to fetch Snyk org and group names: ", e);
            e.printStackTrace();
        }
        responseBody = response.body();
        return responseBody;
    }
}
