package com.ddlab.rnd.action.service;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.swing.JTable;

import org.jetbrains.annotations.NotNull;

import com.ddlab.rnd.ai.AIAssistant;
import com.ddlab.rnd.common.util.CallApiType;
import com.ddlab.rnd.common.util.CommonUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.NoProjectConfiguredException;
import com.ddlab.rnd.exception.NoSnykIssueFoundException;
import com.ddlab.rnd.exception.NoSuchSnykProjectFoundException;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.ddlab.rnd.snyk.ai.out.model.SnykIssue;
import com.ddlab.rnd.snyk.ai.out.model.SnykProjectIssues;
import com.ddlab.rnd.snyk.api.SnykApi;
import com.ddlab.rnd.snyk.project.model.ProjectIdAttributes;
import com.ddlab.rnd.snyk.project.model.ProjectIdData;
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.ddlab.rnd.ui.util.SnykUiUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class SnykActionServiceImpl {

    private static SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
    private static AIAssistant aiAssistant;

    static {
        String clientId = setting.getClientIdStr();
        String clientSecret = setting.getClientSecretStr();
        String tokenUrl = setting.getOauthEndPointUri();
        aiAssistant = new AIAssistant(clientId, clientSecret, tokenUrl);
    }

    public static CompletableFuture<JTable> getProgressiveSnykIssuesInBackground11(Project project, String buildTypeName) {
        CompletableFuture<JTable> future = new CompletableFuture<>();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.PROD_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    String snykProjectIssuesJsonTxt = getRawSnykProjectIssuesAsText11(project, buildTypeName);
                    indicator.setText("Analyzing and Consolidating Issues ...");

                    SnykProjectIssues allProjectIssue = getAIManipulatedSnykIssues(snykProjectIssuesJsonTxt);
                    JTable table = SnykUiUtil.getUpdatedSnykIssueTable(allProjectIssue);
                    indicator.setText("Finishing all ...");

                    future.complete(table);
                } catch (NoSuchSnykProjectFoundException nsfe) {
                    log.error("No such project found in Snyk");
                    CommonUIUtil.showAppErrorMessage(Constants.NO_SNYK_PROJECT_FOUND_MSG);
                } catch (NoProjectConfiguredException npce) {
                    CommonUIUtil.showAppErrorMessage(Constants.NO_PROJECT_CONFIGURATION_BUILD_TYPE);
                } catch (NoSnykIssueFoundException ex) {
                    log.error("No issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_SNYK_ISSUES_FOUND);
                } catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
                log.debug("\n************** END - TRACKING DATA FOR ANALYSIS **************\n");
            }

            @Override
            public void onCancel() {
                future.completeExceptionally(new CancellationException("Task cancelled"));
            }
        });

        return future;
    }

    public static JTable getProgressiveSnykIssues11(Project project, String buildTypeName) {
        final JTable[] table = new JTable[1];
//        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.PROD_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    String snykProjectIssuesJsonTxt = getRawSnykProjectIssuesAsText11(project, buildTypeName);
                    indicator.setText("Analyzing and Consolidating Issues ...");

                    SnykProjectIssues allProjectIssue = getAIManipulatedSnykIssues(snykProjectIssuesJsonTxt);
                    table[0] = SnykUiUtil.getUpdatedSnykIssueTable(allProjectIssue);
                    indicator.setText("Finishing all ...");

                } catch (NoSuchSnykProjectFoundException nsfe) {
                    log.error("No such project found in Snyk");
                    CommonUIUtil.showAppErrorMessage(Constants.NO_SNYK_PROJECT_FOUND_MSG);
                } catch (NoProjectConfiguredException npce) {
                    CommonUIUtil.showAppErrorMessage(Constants.NO_PROJECT_CONFIGURATION_BUILD_TYPE);
                } catch (NoSnykIssueFoundException ex) {
                    log.error("No issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_SNYK_ISSUES_FOUND);
                } catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
            }

            @Override
            public void onSuccess() {
//                Messages.showInfoMessage("Task finished successfully!", "Done");
            }

        });
        return table[0];
    }

    public static String getRawSnykProjectIssuesAsText11(Project project, String buildFileTypeName) {
        String snykProjectIssuesJsonTxt = null;
        String displayProjectName = project.getName();
        String projectRootDirName = new File(project.getBasePath()).getName();
        snykProjectIssuesJsonTxt = getRawProjectIssues11(displayProjectName, projectRootDirName, buildFileTypeName);
        return snykProjectIssuesJsonTxt;
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

    private static String getRawProjectIssues11(String projectName, String projectRootDirName, String buildFileTypeName) {
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
//        try {
        String snykProjectIssueUri = CommonUtil.getProperty("snyk.project.issue.uri");
        String projectIssueFilledUri = MessageFormat.format(snykProjectIssueUri, orgId, projectId);
        String inputJsonTxt = CommonUtil.getResourceContentAsText("json/project_issues_input.json");
        snykRawResponse = CallApiType.POST.perform(projectIssueFilledUri, snykToken, inputJsonTxt);
        if (snykRawResponse.equalsIgnoreCase("{\"issues\":[]}")) {
            throw new NoSnykIssueFoundException("No Snyk Issue Found.");
        }
        return snykRawResponse;
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
            projectId = getProjectId11(projectIdAsTxt, buildType);
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

    private static String getProjectId11(String targeIdDataText, String projectType) {
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

    private static SnykProjectIssues getAIManipulatedSnykIssues(String snykProjectIssuesJsonTxt) throws RuntimeException {
        SnykProjectIssues allProjectIssue = null;

//        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String selectedLlmModelName = setting.getLlmModelComboSelection();
        String llmModel = selectedLlmModelName.split("~")[0];
        String aiInputModelMsg = SnykApi.getSnykProjectIssueInputAIPrompt(snykProjectIssuesJsonTxt, llmModel);
        allProjectIssue = getModifiedAiSnykIssueObject(aiInputModelMsg);

        return allProjectIssue;
    }

    private static SnykProjectIssues getModifiedAiSnykIssueObject(String aiInputModelMsg) throws RuntimeException {
        String bearerToken = null;
        SnykProjectIssues allProjectIssue;
        try {
            bearerToken = aiAssistant.getBearerToken();
        } catch (Exception e) {
//            log.error("Error while retrieving token for AI for Synk Issues...", e);
            throw new RuntimeException("Error while retrieving token from AI, recheck AI input. \nIf the issue persists, please contact developer...");
        }

        String aiResponse = null;
        try {
            String aiApiUrl = aiAssistant.getFormedAIApiUrl(setting.getLlmApiEndPointUri());
            aiResponse = aiAssistant.getOnlyAnswerFromAI(aiApiUrl, bearerToken, aiInputModelMsg);

        } catch (Exception e) {
//            log.error("Unexpected exception while getting response from AI:\n", e);
            throw new RuntimeException("UnExpected exception while getting response from AI");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            allProjectIssue = mapper.readValue(aiResponse, SnykProjectIssues.class);
        } catch (Exception ex) {
            log.error("Unable to process the response using Mapper from AI: \n", ex);
            throw new RuntimeException("Unable to process the response from AI,\n Please contact the developer.");
        }

        return allProjectIssue;
    }

    public static Map<String, String> getHighestFixableSnykIssuesDetails(Project project, String fileName) {
        SnykProjectIssues allProjectIssue = null;
        String snykProjectIssuesJsonTxt = getRawSnykProjectIssuesAsText11(project, fileName);

        String highestFixedVersionPrompt = CommonUtil.getProperty("highest.fixed.version.prompt");
        highestFixedVersionPrompt = highestFixedVersionPrompt.replaceAll("\\{innerJson\\}", snykProjectIssuesJsonTxt);

        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String selectedLlmModelName = setting.getLlmModelComboSelection();
        String llmModel = selectedLlmModelName.split("~")[0];
        log.debug("Selected Actual LLM Model Name ?: " + llmModel);
//
        String aiInputModelMsg = SnykApi.getSnykProjectIssueInputAIPrompt(highestFixedVersionPrompt, llmModel);
        allProjectIssue = getModifiedAiSnykIssueObject(aiInputModelMsg);

        Map<String, String> fixedDependencyMap = allProjectIssue.getIssues().stream()
                .filter(issue -> issue.getFixInfo() != null && !issue.getFixInfo().getFixedIn().isEmpty())
                .collect(Collectors.toMap(
                        SnykIssue::getPkgName,
                        issue -> issue.getFixInfo().getFixedIn().get(0),
                        (existing, replacement) -> replacement
                ));

        return fixedDependencyMap;
    }

}




