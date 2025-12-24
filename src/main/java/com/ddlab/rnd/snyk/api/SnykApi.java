package com.ddlab.rnd.snyk.api;

import com.ddlab.rnd.ai.AIAssistant;
import com.ddlab.rnd.common.util.CallApiType;
import com.ddlab.rnd.common.util.CommonUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.NoSnykIssueFoundException;
import com.ddlab.rnd.exception.NoSuchSnykProjectFoundException;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.ddlab.rnd.snyk.project.model.ProjectIdAttributes;
import com.ddlab.rnd.snyk.project.model.ProjectIdData;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SnykApi {

    private static SynkoMycinSettings setting = SynkoMycinSettings.getInstance();

    public static String getTargetIdAsJsonText(String projectName, String snykTargetUri, String snykApiToken) throws RuntimeException {
        String queryParams = Constants.SNYK_TGT_ID_PARAM + projectName;
        String responseBody = CallApiType.GET.perform(snykTargetUri + queryParams, snykApiToken);
        return responseBody;
    }

    public static String fetchSnykProjectIssuesAsJsonText(Project project, String buildFileTypeName) {
        String snykProjectIssuesJsonTxt = null;
        String displayProjectName = project.getName();
        String projectRootDirName = new File(project.getBasePath()).getName();
        snykProjectIssuesJsonTxt = fetchSnykProjectIssuesAsJson(displayProjectName, projectRootDirName, buildFileTypeName);
        return snykProjectIssuesJsonTxt;
    }

    public static String getSnykProjectIssueInputAIPrompt(String snykProjectIssuesJsonTxt, String aiModelName) {
        String initialPrompt = CommonUtil.getProperty("make.snyk.json.required.prompt");
        String smallJsonPromtText = initialPrompt.replaceAll("\\{innerJson\\}", snykProjectIssuesJsonTxt);
        AIAssistant aiAssistant = new AIAssistant();
        String aiInputModelMsg = aiAssistant.getFormedPrompt(smallJsonPromtText, aiModelName);

        return aiInputModelMsg;
    }

    private static String fetchSnykProjectIssuesAsJson(String projectName, String projectRootDirName, String buildFileTypeName) {
        String actualSnykProjectName = projectName;
        String snykOrgComboSelection = setting.getSnykOrgComboSelection();
        String snykToken = setting.getSnykTokenTxt();
        snykToken = !snykToken.startsWith(Constants.TOKEN_SPACE) ? Constants.TOKEN_SPACE + snykToken : snykToken;
        Map<String, String> snykOrgNameIdMap = setting.getSnykOrgNameIdMap();
        String orgId = snykOrgNameIdMap.get(snykOrgComboSelection);
        log.debug("Snyk Project Org Id: " + orgId);
        // Get the target id, See and validate the response
        String snykTargetUri = CommonUtil.getProperty("snyk.target.id.uri");
        String fmtdSnykTargetUri = MessageFormat.format(snykTargetUri, orgId);
        String projectId = getProjectIdByProjectName(actualSnykProjectName, buildFileTypeName, fmtdSnykTargetUri, orgId, snykToken);
        if (projectId == null) {
            // Make second trial
            actualSnykProjectName = projectRootDirName;
            projectId = getProjectIdByProjectName(actualSnykProjectName, buildFileTypeName, fmtdSnykTargetUri, orgId, snykToken);
        }
        // Finally throw the exception
        if (projectId == null) {
            throw new NoSuchSnykProjectFoundException("Unable to find the project in Snyk System.");
        }
        return getSnykProjectDependencyIssues(orgId, projectId, snykToken);
    }

    private static String getSnykProjectDependencyIssues(String orgId, String projectId, String snykToken) {
        String snykRawResponse = null;
        String snykProjectIssueUri = CommonUtil.getProperty("snyk.project.issue.uri");
        String projectIssueFilledUri = MessageFormat.format(snykProjectIssueUri, orgId, projectId);
        String inputJsonTxt = CommonUtil.getResourceContentAsText("json/project_issues_input.json");
        snykRawResponse = CallApiType.POST.perform(projectIssueFilledUri, snykToken, inputJsonTxt);
        if (snykRawResponse.equalsIgnoreCase("{\"issues\":[]}")) {
            throw new NoSnykIssueFoundException("No Snyk Issue Found.");
        }
        return snykRawResponse;
    }

    private static String getProjectIdByProjectName(String projectName, String buildTypeName, String snykTargetUri, String orgId, String snykToken) {
        // Fetch Target Id
        String targetIdJsonTxt = SnykApi.getTargetIdAsJsonText(projectName, snykTargetUri, snykToken);
//        log.debug("Initial Target Id Json Txt: " + targetIdJsonTxt);
//        log.debug("Target Id (Objects.isNull(targetIdJsonTxt)): " + Objects.isNull(targetIdJsonTxt));
        String targetId = getTargetId(targetIdJsonTxt, projectName);
        log.debug("Initial Target Id: " + targetId);
        // Fetch Project Id
        String projectId = getSnykProjectId(targetId, buildTypeName, orgId, snykToken);
        log.debug("Initial Snyk Project Id: {}", projectId);

        return projectId;
    }

    private static String getTargetId(String targeIdDataText, String projectName) throws RuntimeException {
        String snykTargetId = null;
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

        snykTargetId = tgtIds.isEmpty() ? null : tgtIds.get(0);
        return snykTargetId;
    }

    private static String getSnykProjectId(String targetId, String buildFileType, String orgId, String snykToken) {
        String snykProjectIdUri = CommonUtil.getProperty("snyk.project.id.uri");
        String fmtdSnykProjectIdUri = MessageFormat.format(snykProjectIdUri, orgId);
        String buildType = CommonUtil.BUILDTYPEMAP.get(buildFileType);
        log.debug("Snyk Build Type: " + buildType);

        String projectIdAsTxt = getProjectIdAsJsonText(targetId, fmtdSnykProjectIdUri, snykToken);
//        log.debug("Project Id Json Txt: " + projectIdAsTxt);
        String projectId = null;
        if (projectIdAsTxt != null) {
            projectId = fetchSnykProjectId(projectIdAsTxt, buildType);
        }

        return projectId;
    }

    private static String getProjectIdAsJsonText(String targetId, String snykProjectIdtUri, String snykApiToken) {
        String queryParams = Constants.SNYK_PGT_ID_PARAM + targetId;
        String responseBody = null;
        try {
            responseBody = CallApiType.GET.perform(snykProjectIdtUri + queryParams, snykApiToken);
        } catch (RuntimeException e) {
//            log.error("Exception while getting Project Id from Snyk:\n {}", e);
            // Just log the error
        }
        return responseBody;
    }

    private static String fetchSnykProjectId(String targeIdDataText, String projectType) {
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


}
