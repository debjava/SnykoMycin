package com.ddlab.rnd.snyk.api;

import com.ddlab.rnd.ai.AgentUtil;
import com.ddlab.rnd.common.util.CommonUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.NoProjectConfiguredException;
import com.ddlab.rnd.exception.NoSuchSnykProjectFoundException;
import com.ddlab.rnd.snyk.project.model.ProjectIdAttributes;
import com.ddlab.rnd.snyk.project.model.ProjectIdData;
import com.ddlab.rnd.snyk.project.model.ProjectIdDatum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SnykApi {

    public static String getTargetIdAsJsonText(String projectName, String snykTargetUri, String snykApiToken) throws RuntimeException {
        String responseBody = null;
        HttpResponse<String> response = null;
        String queryParams = "?version=2025-11-05&display_name=" + projectName;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(snykTargetUri + queryParams))
                .header("Authorization", snykApiToken).GET().build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                responseBody = response.body();
            }
        } catch (IOException | InterruptedException e) {
            log.error("Exception in getTargetIdAsJsonText: ", e);
        }
        return responseBody;
    }

    public static String getProjectIdAsJsonText(String targetId, String snykProjectIdtUri, String snykApiToken) {
        String responseBody = null;
        HttpResponse<String> response = null;
        String queryParams = "?version=2025-11-05&target_id=" + targetId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(snykProjectIdtUri + queryParams))
                .header("Authorization", snykApiToken).GET().build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                responseBody = response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    public static String getTargetId(String targeIdDataText, String projectName) throws RuntimeException {
        List<String> tgtIds = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        ProjectIdData prodIdData = mapper.readValue(targeIdDataText, ProjectIdData.class);
        tgtIds = prodIdData.getProjectIdData().stream().filter(projIdDataum -> {
            ProjectIdAttributes allAttributes = projIdDataum.getAttributes();
            String displayName = allAttributes.getDisplayName();
            String url = allAttributes.getUrl();

            return displayName.contains(projectName) && url != null;
        }).map(projIdDataum -> {
            String targetId = projIdDataum.getId();
//            log.debug("What is the Snyk Target Id: {}", targetId);
            return targetId;
        }).collect(Collectors.toList());
        if(tgtIds.isEmpty()) {
            throw new NoProjectConfiguredException("No project configured for build type in Snyk." +
                    "\nPlease check in Snyk System.");
        }
        return tgtIds.get(0);
    }


    public static String getProjectId11(String targeIdDataText, String projectType) {
        System.out.println("Target Id Text: " + targeIdDataText);
        ObjectMapper mapper = new ObjectMapper();
        ProjectIdData targetIdData = mapper.readValue(targeIdDataText, ProjectIdData.class);
        System.out.println("targetIdData : " + targetIdData);


        List<String> projectIds = targetIdData.getProjectIdData().stream().filter(projectIdDatum -> {
            ProjectIdAttributes attr = projectIdDatum.getAttributes();
            String type = attr.getType();

            return type.equalsIgnoreCase(projectType);
        }).map(projIdDataum -> {
            String targetId = projIdDataum.getId();
            return targetId;
        }).collect(Collectors.toList());

        return projectIds.get(0);
    }


    public static List<String> getProjectList(String snykFetchProjectIdUri, String snykToken) throws RuntimeException {
        String jsonTextResponse = getProjectIdAsJsonText(snykFetchProjectIdUri, snykToken);
        log.debug("Project List Json Response: {}", jsonTextResponse);
        if (jsonTextResponse == null) {
            throw new NoSuchSnykProjectFoundException("No response received or project not found in the Snyk. ");
        }
        if (jsonTextResponse.equalsIgnoreCase("{\"jsonapi\":{\"version\":\"1.0\"},\"data\":[],\"links\":{}}")) {
            throw new NoSuchSnykProjectFoundException("No Project found in Snyk.");
        }
        return getProjectIdAsList(jsonTextResponse);
    }

    public static String getProjectIdAsJsonText(String snykFetchProjectIdUri, String snykToken) {
        String responseBody = null;
        HttpResponse<String> response = null;
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(snykFetchProjectIdUri))
                .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE)
                .header(Constants.AUTHORIZATION, snykToken)
                .GET().build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            log.debug("getProjectIdAsJsonText Response Status Code: {}", response.statusCode());
            if (response.statusCode() == 200) {
                responseBody = response.body();
            }
            if (responseBody == null) {
                throw new NoSuchSnykProjectFoundException("No response received or project not found in the Snyk. ");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Exception in getProjectIdAsJsonText: ", e);
        }
        log.debug("In getProjectIdAsJsonText What is the Snyk Response Body: {}", responseBody);
        return responseBody;
    }

    public static List<String> getProjectIdAsList(String jsonTextResponse) {
        log.debug("getProjectIdAsList jsonTextResponse: {}", jsonTextResponse);
        List<String> projectIdList = new LinkedList<String>();

        try {

            ObjectMapper mapper = new ObjectMapper();
            ProjectIdData projectIdData = mapper.readValue(jsonTextResponse, ProjectIdData.class);
            log.debug("Project Id Data: " + projectIdData);

            // For Testing
            List<ProjectIdDatum> datumList = projectIdData.getProjectIdData();
            log.debug("datumList------>: " + datumList);
            log.debug("Datum List Size: {}", datumList.size());

            // For Testing


            projectIdData.getProjectIdData().forEach(value -> {
                String type = value.getAttributes().getType();
                log.debug("Project Type: {}", type);
//            String name = value.getAttributes().getName();
                if (!type.equalsIgnoreCase(Constants.SAST)) {
                    projectIdList.add(value.getId());
                }

            });

        } catch (Exception ex) {
            log.error("Exception in getProjectIdAsList: ", ex);
            ex.printStackTrace();
        }


//        ObjectMapper mapper = new ObjectMapper();
//        ProjectIdData projectIdData = mapper.readValue(jsonTextResponse, ProjectIdData.class);
////        log.debug("Project Id Data: " + projectIdData);
//
//        projectIdData.getProjectIdData().forEach(value -> {
//            String type = value.getAttributes().getType();
////            String name = value.getAttributes().getName();
//            if (!type.equalsIgnoreCase(Constants.SAST)) {
//                projectIdList.add(value.getId());
//            }
//
//        });
        log.debug("Project Id List: " + projectIdList);
        return projectIdList;
    }

    public static String getSnykProjectIssuesAsJsonText(String projectIssueFilledUri, String snykToken, String inputContent) {
        String responseBody = null;
        HttpResponse<String> response = null;
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(projectIssueFilledUri))
                .header(Constants.CONTENT_TYPE, Constants.JSON_TYPE).header(Constants.AUTHORIZATION, snykToken)
                .POST(HttpRequest.BodyPublishers.ofString(inputContent)).build();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.debug("Exception in getSnykProjectIssuesAsJsonText: ", e);
        }
        responseBody = response.body();
        return responseBody;
    }

    private static String getSnykProjectTargetId(String projectName, String orgId, String snykToken) throws RuntimeException {
        String snykTargetUri = CommonUtil.getProperty("snyk.target.id.uri");
        String fmtdSnykTargetUri = MessageFormat.format(snykTargetUri, orgId);
//        log.debug("Snyk Target Uri: " + fmtdSnykTargetUri);

        String targetIdJsonTxt = getTargetIdAsJsonText(projectName, fmtdSnykTargetUri, snykToken);
        log.debug("Snyk Target Id Json text: {}", targetIdJsonTxt);

        if (targetIdJsonTxt == null) {
            throw new NoSuchSnykProjectFoundException("No response received or project not found in the Snyk. ");
        }
        if (targetIdJsonTxt.equalsIgnoreCase("{\"jsonapi\":{\"version\":\"1.0\"},\"data\":[],\"links\":{},\"meta\":{}}")) {
            throw new NoSuchSnykProjectFoundException("No Project found in Snyk.");
        }
        String targetId = getTargetId(targetIdJsonTxt, projectName);
//        log.debug("Final Target Id: " + targetId);

        return targetId;
    }

    private static String getSnykProjectId(String targetId, String buildFileType, String orgId, String snykToken) throws RuntimeException {
        String snykProjectIdUri = CommonUtil.getProperty("snyk.project.id.uri");
        String fmtdSnykProjectIdUri = MessageFormat.format(snykProjectIdUri, orgId);
//        log.debug("Snyk Project Id Uri: " + fmtdSnykProjectIdUri);

        String buildType = CommonUtil.BUILDTYPEMAP.get(buildFileType);
        log.debug("Snyk Build Type: " + buildType);

        String projectIdAsTxt = getProjectIdAsJsonText(targetId, fmtdSnykProjectIdUri, snykToken);
//        log.debug("Snyk Project Id Json text: {}", projectIdAsTxt);
        String projectId = getProjectId11(projectIdAsTxt, buildType);
//        log.debug("Final Snyk Project Id: "+projectId);

        return projectId;
    }

    public static String getSnykIssuesAsJsonText11(String projectName, String buildFileType, String orgId, String snykToken) throws RuntimeException {
        String targetId = getSnykProjectTargetId(projectName, orgId, snykToken);
        log.debug("Final Target Id: " + targetId);

        String projectId = getSnykProjectId(targetId, buildFileType, orgId, snykToken);
        log.debug("Final Snyk Project Id: " + projectId);

        String snykProjectIssueUri = CommonUtil.getProperty("snyk.project.issue.uri");
        String projectIssueFilledUri = MessageFormat.format(snykProjectIssueUri, orgId, projectId);
        String inputJsonTxt = CommonUtil.getResourceContentAsText("json/project_issues_input.json");
        String responseBody = getSnykProjectIssuesAsJsonText(projectIssueFilledUri, snykToken, inputJsonTxt);

        return responseBody;
    }

    @Deprecated
    public static String getSnykIssuesAsJsonText(String orgId, String projectName, String snykToken) throws RuntimeException {
        String snykTargetUri = CommonUtil.getProperty("snyk.target.id.uri");
        String fmtdSnykTargetUri = MessageFormat.format(snykTargetUri, orgId);
//        log.debug("Snyk Target Uri: " + fmtdSnykTargetUri);

        String targetIdTxt = getTargetIdAsJsonText(projectName, fmtdSnykTargetUri, snykToken);
        String targetId = getTargetId(targetIdTxt, projectName);
        log.debug("Final Snyk Target Id: " + targetId);

        String snykProjectIdUri = CommonUtil.getProperty("snyk.project.id.uri");
        String fmtdSnykProjectIdUri = MessageFormat.format(snykProjectIdUri, orgId);
//        log.debug("Snyk Project Id Uri: " + fmtdSnykProjectIdUri);


//        String projectIdAsTxt = getProjectIdAsJsonText(orgId, targetId, fmtdSnykProjectIdUri, snykToken);
//        String projectId = getProjecId(projectIdAsTxt, buildType);
//        log.debug("FinalProject Id: "+projectId);


        String snykFetchProjectIdUri = CommonUtil.getProperty("snyk.get.project.uri");
        snykFetchProjectIdUri = MessageFormat.format(snykFetchProjectIdUri, orgId, projectName);
        List<String> projectList = getProjectList(snykFetchProjectIdUri, snykToken);

        // Pick first project id
        String projectId = projectList.get(0);
        log.debug("Final Snyk Project Id: " + projectId);

        String snykProjectIssueUri = CommonUtil.getProperty("snyk.project.issue.uri");
        String projectIssueFilledUri = MessageFormat.format(snykProjectIssueUri, orgId, projectId);
        String inputJsonTxt = CommonUtil.getResourceContentAsText("json/project_issues_input.json");
        String responseBody = getSnykProjectIssuesAsJsonText(projectIssueFilledUri, snykToken, inputJsonTxt);

        return responseBody;
    }

    public static String getSnykProjectIssueInputAIPromt(String snykProjectIssuesJsonTxt, String aiModelName) {
        String initialPrompt = CommonUtil.getProperty("make.snyk.json.required.prompt");
        String smallJsonPromtText = initialPrompt.replaceAll("\\{innerJson\\}", snykProjectIssuesJsonTxt);
//        log.debug("smallJsonPromtText: " + smallJsonPromtText);
        String aiInputModelMsg = AgentUtil.getFormedPrompt(smallJsonPromtText, aiModelName);

        return aiInputModelMsg;
    }
}
