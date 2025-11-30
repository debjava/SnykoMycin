package com.ddlab.rnd.snyk.api;

import com.ddlab.rnd.ai.AgentUtil;
import com.ddlab.rnd.common.util.CommonUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.snyk.project.model.ProjectIdData;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SnykApi {

    public static List<String> getProjectList(String snykFetchProjectIdUri, String snykToken) {
        String jsonTextResponse = getProjectIdAsJsonText(snykFetchProjectIdUri, snykToken);
        return getProjectIdAsList(jsonTextResponse);
    }

    public static String getProjectIdAsJsonText(String snykFetchProjectIdUri, String snykToken) {
        String responseBody = null;
        HttpResponse<String> response = null;
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(snykFetchProjectIdUri))
                .header("Content-Type", "application/json").header("Authorization", snykToken).GET().build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        responseBody = response.body();
        return responseBody;
    }

    public static List<String> getProjectIdAsList(String jsonTextResponse) {

        List<String> projectIdList = new LinkedList<String>();

        ObjectMapper mapper = new ObjectMapper();
        ProjectIdData projectIdData = mapper.readValue(jsonTextResponse, ProjectIdData.class);
        log.debug("Project Id Data: " + projectIdData);

        projectIdData.getProjectIdData().forEach(value -> {

            String type = value.getAttributes().getType();
            String name = value.getAttributes().getName();
            if ( !type.equalsIgnoreCase("sast")) {
                projectIdList.add(value.getId());
            }

        });
        return projectIdList;
    }

    public static String getSnykProjectIssuesAsJsonText(String projectIssueFilledUri, String snykToken, String inputContent) {
        String responseBody = null;
        HttpResponse<String> response = null;
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(projectIssueFilledUri))
//                .header("Content-Type", "application/json").header("Authorization", snykToken)
                .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, snykToken)
                .POST(HttpRequest.BodyPublishers.ofString(inputContent)).build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        responseBody = response.body();
        return responseBody;
    }

    public static String getSnykIssuesAsJsonText(String orgId, String projectName, String snykToken) {
        String snykFetchProjectIdUri = CommonUtil.getProperty("snyk.get.project.uri");
        snykFetchProjectIdUri = MessageFormat.format(snykFetchProjectIdUri, orgId, projectName);
        List<String> projectList = SnykApi.getProjectList(snykFetchProjectIdUri, snykToken);
        // Pick first project id
        String projectId = projectList.get(0);
        log.debug("Snyk projectId: " + projectId);

        String snykProjectIssueUri = CommonUtil.getProperty("snyk.project.issue.uri");
        String projectIssueFilledUri = MessageFormat.format(snykProjectIssueUri, orgId, projectId);
        String inputJsonTxt = CommonUtil.getResourceContentAsText("json/project_issues_input.json");
        String responseBody = getSnykProjectIssuesAsJsonText(projectIssueFilledUri, snykToken, inputJsonTxt);

        return responseBody;
    }

    public static String getSnykProjectIssueInputAIPromt(String snykProjectIssuesJsonTxt, String aiModelName) {
        String initialPrompt = CommonUtil.getProperty("make.snyk.json.required.prompt");
        String smallJsonPromtText = initialPrompt.replaceAll("\\{innerJson\\}", snykProjectIssuesJsonTxt);
        log.debug("smallJsonPromtText: " + smallJsonPromtText);
        String aiInputModelMsg = AgentUtil.getFormedPrompt(smallJsonPromtText, aiModelName);

        return aiInputModelMsg;
    }
}
