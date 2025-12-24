package com.ddlab.rnd.action.service;

import com.ddlab.rnd.ai.AIAssistant;
import com.ddlab.rnd.common.util.CommonUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.NoProjectConfiguredException;
import com.ddlab.rnd.exception.NoSnykIssueFoundException;
import com.ddlab.rnd.exception.NoSuchSnykProjectFoundException;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.ddlab.rnd.snyk.ai.out.model.SnykIssue;
import com.ddlab.rnd.snyk.ai.out.model.SnykProjectIssues;
import com.ddlab.rnd.snyk.api.SnykApi;
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.ddlab.rnd.ui.util.SnykUiUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    public static CompletableFuture<JTable> fetchSnykIssuesInBackground(Project project, String buildTypeName) {
        CompletableFuture<JTable> future = new CompletableFuture<>();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.PROD_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    String snykProjectIssuesJsonTxt = SnykApi.fetchSnykProjectIssuesAsJsonText(project, buildTypeName);
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

    public static JTable fetchProgressiveSnykIssuesInForeground(Project project, String buildTypeName) {
        final JTable[] table = new JTable[1];
//        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.PROD_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    String snykProjectIssuesJsonTxt = SnykApi.fetchSnykProjectIssuesAsJsonText(project, buildTypeName);
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

    public static Map<String, String> getHighestFixableSnykIssuesDetails(Project project, String fileName) {
        SnykProjectIssues allProjectIssue = null;
        String snykProjectIssuesJsonTxt = SnykApi.fetchSnykProjectIssuesAsJsonText(project, fileName);

        String highestFixedVersionPrompt = CommonUtil.getProperty("highest.fixed.version.prompt");
        highestFixedVersionPrompt = highestFixedVersionPrompt.replaceAll("\\{innerJson\\}", snykProjectIssuesJsonTxt);

        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String llmModel = setting.getLlmModelComboSelection();
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

    private static SnykProjectIssues getAIManipulatedSnykIssues(String snykProjectIssuesJsonTxt) throws RuntimeException {
        SnykProjectIssues allProjectIssue = null;

        String llmModel = setting.getLlmModelComboSelection();
        log.debug("Selected Actual LLM Model Name ?: " + llmModel);
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

}




