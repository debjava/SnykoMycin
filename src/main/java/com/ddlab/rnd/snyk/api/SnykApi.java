package com.ddlab.rnd.snyk.api;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ddlab.rnd.ai.AIAssistant;
import com.ddlab.rnd.common.util.CallApiType;
import com.ddlab.rnd.common.util.CommonUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.NoProjectConfiguredException;
import com.ddlab.rnd.exception.NoSuchSnykProjectFoundException;
import com.ddlab.rnd.snyk.project.model.ProjectIdAttributes;
import com.ddlab.rnd.snyk.project.model.ProjectIdData;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class SnykApi {

    public static String getTargetIdAsJsonText(String projectName, String snykTargetUri, String snykApiToken) throws RuntimeException {
        String queryParams = Constants.SNYK_TGT_ID_PARAM + projectName;
        String responseBody = CallApiType.GET.perform(snykTargetUri + queryParams, snykApiToken);
        return responseBody;
    }

    public static String getProjectIdAsJsonText(String targetId, String snykProjectIdtUri, String snykApiToken) {
        String queryParams = Constants.SNYK_PGT_ID_PARAM + targetId;
        String responseBody = CallApiType.GET.perform(snykProjectIdtUri + queryParams, snykApiToken);
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
            return targetId;
        }).collect(Collectors.toList());
        if(tgtIds.isEmpty()) {
            throw new NoProjectConfiguredException("No project configured for build type in Snyk." +
                    "\nPlease check in Snyk System.");
        }
        return tgtIds.get(0);
    }

    public static String getProjectId11(String targeIdDataText, String projectType) {
        ObjectMapper mapper = new ObjectMapper();
        ProjectIdData targetIdData = mapper.readValue(targeIdDataText, ProjectIdData.class);

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

    public static String getSnykProjectIssuesAsJsonText(String projectIssueFilledUri, String snykToken, String inputContent) {
        String responseBody = null;
        responseBody = CallApiType.POST.perform(projectIssueFilledUri, snykToken, inputContent);
        return responseBody;
    }

    private static String getSnykProjectTargetId(String projectName, String orgId, String snykToken) throws RuntimeException {
        String snykTargetUri = CommonUtil.getProperty("snyk.target.id.uri");
        String fmtdSnykTargetUri = MessageFormat.format(snykTargetUri, orgId);

        String targetIdJsonTxt = getTargetIdAsJsonText(projectName, fmtdSnykTargetUri, snykToken);
        log.debug("Target Id Json Txt: " + targetIdJsonTxt);

        if (targetIdJsonTxt == null) {
            throw new NoSuchSnykProjectFoundException("No response received or project not found in the Snyk. ");
        }
        if (targetIdJsonTxt.equalsIgnoreCase("{\"jsonapi\":{\"version\":\"1.0\"},\"data\":[],\"links\":{},\"meta\":{}}")) {
            throw new NoSuchSnykProjectFoundException("No Project found in Snyk.");
        }
        String targetId = getTargetId(targetIdJsonTxt, projectName);

        return targetId;
    }

    private static String getSnykProjectId(String targetId, String buildFileType, String orgId, String snykToken) throws RuntimeException {
        String snykProjectIdUri = CommonUtil.getProperty("snyk.project.id.uri");
        String fmtdSnykProjectIdUri = MessageFormat.format(snykProjectIdUri, orgId);
        String buildType = CommonUtil.BUILDTYPEMAP.get(buildFileType);
        log.debug("Snyk Project Build Type: " + buildType);

        String projectIdAsTxt = getProjectIdAsJsonText(targetId, fmtdSnykProjectIdUri, snykToken);
        String projectId = getProjectId11(projectIdAsTxt, buildType);

        return projectId;
    }

    @Deprecated
    public static String getSnykIssuesAsJsonText11(String projectName, String buildFileType, String orgId, String snykToken) throws RuntimeException {
        String targetId = getSnykProjectTargetId(projectName, orgId, snykToken);
        log.debug("Snyk Final Target Id: " + targetId);

        String projectId = getSnykProjectId(targetId, buildFileType, orgId, snykToken);
        log.debug("Snyk Final Project Id: " + projectId);

        String snykProjectIssueUri = CommonUtil.getProperty("snyk.project.issue.uri");
        String projectIssueFilledUri = MessageFormat.format(snykProjectIssueUri, orgId, projectId);
        String inputJsonTxt = CommonUtil.getResourceContentAsText("json/project_issues_input.json");
        String responseBody = getSnykProjectIssuesAsJsonText(projectIssueFilledUri, snykToken, inputJsonTxt);

        return responseBody;
    }

    public static String getSnykProjectIssueInputAIPrompt(String snykProjectIssuesJsonTxt, String aiModelName) {
        String initialPrompt = CommonUtil.getProperty("make.snyk.json.required.prompt");
        String smallJsonPromtText = initialPrompt.replaceAll("\\{innerJson\\}", snykProjectIssuesJsonTxt);
        AIAssistant aiAssistant = new AIAssistant();
        String aiInputModelMsg = aiAssistant.getFormedPrompt(smallJsonPromtText, aiModelName);

        return aiInputModelMsg;
    }
}
