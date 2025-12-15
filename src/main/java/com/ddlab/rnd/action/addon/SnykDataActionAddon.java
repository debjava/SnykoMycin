/*
 * ============================================================================
 *  Copyright (c) 2025 DDLABS INC. All Rights Reserved.
 *  Snykomycin is a plugin from Tornado Application maintained
 *  by DDLABS INC (Debadatta Mishra).
 *  Contact me in deba.java@gmail.com.
 *
 *  Description: Code for Snykomycin product from Tornado
 *  Author: Debadatta Mishra
 *  Version: 1.0
 * ============================================================================
 */
package com.ddlab.rnd.action.addon;

import com.ddlab.rnd.ai.AgentUtil;
import com.ddlab.rnd.ai.input.model.SnykFixInputModel;
import com.ddlab.rnd.common.util.CommonUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.NoFixableSnykIssueFoundException;
import com.ddlab.rnd.exception.NoProjectConfiguredException;
import com.ddlab.rnd.exception.NoSnykIssueFoundException;
import com.ddlab.rnd.exception.NoSuchSnykProjectFoundException;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.ddlab.rnd.snyk.ai.out.model.SnykProjectIssues;
import com.ddlab.rnd.snyk.api.SnykApi;
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.ui.table.JBTable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * The Class SnykDataActionAddon.
 *
 * @author Debadatta Mishra
 */
@Slf4j
public class SnykDataActionAddon {


    public static CompletableFuture<JTable> getProgressiveSnykIssuesInBackground11(Project project, String buildTypeName) {
        CompletableFuture<JTable> future = new CompletableFuture<>();

        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    String snykProjectIssuesJsonTxt =  getRawSnykProjectIssuesAsText11(project, buildTypeName);
                    indicator.setText("Analyzing and Consolidating Issues ...");
                    SnykProjectIssues allProjectIssue = getAIManipulatedSnykIssues(snykProjectIssuesJsonTxt);
                    JTable table = getUpdatedSnykIssueTable(allProjectIssue);
                    indicator.setText("Finishing all ...");

                    future.complete(table);
                }
                catch(NoSuchSnykProjectFoundException nsfe) {
                    log.error("No such project found in Snyk");
                    CommonUIUtil.showAppErrorMessage(Constants.NO_SNYK_PROJECT_FOUND_MSG);
                }
                catch(NoProjectConfiguredException npce) {
                    CommonUIUtil.showAppErrorMessage(Constants.NO_PROJECT_CONFIGURATION_BUILD_TYPE);
                }
                catch (NoSnykIssueFoundException ex) {
                    log.debug("No issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_SNYK_ISSUES_FOUND);
                } catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
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
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    String snykProjectIssuesJsonTxt =  getRawSnykProjectIssuesAsText11(project, buildTypeName);
                    indicator.setText("Analyzing and Consolidating Issues ...");
                    SnykProjectIssues allProjectIssue = getAIManipulatedSnykIssues(snykProjectIssuesJsonTxt);
                    table[0] = getUpdatedSnykIssueTable(allProjectIssue);
                    indicator.setText("Finishing all ...");
                } catch(NoSuchSnykProjectFoundException nsfe) {
                    log.error("No such project found in Snyk");
                    CommonUIUtil.showAppErrorMessage(Constants.NO_SNYK_PROJECT_FOUND_MSG);
                }
                catch(NoProjectConfiguredException npce) {
                    CommonUIUtil.showAppErrorMessage(Constants.NO_PROJECT_CONFIGURATION_BUILD_TYPE);
                }
                catch (NoSnykIssueFoundException ex) {
                    log.debug("No issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_SNYK_ISSUES_FOUND);
                } catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
            }

            @Override
            public void onSuccess() {
                // Runs on UI thread after completion
//                Messages.showInfoMessage("Task finished successfully!", "Done");
            }

        });
        return table[0];
    }

    @Deprecated
    public static JTable getProgressiveSnykIssues(Project project) {
        final JTable[] table = new JTable[1];
//        ProgressManager.getInstance().run(new Task.Backgroundable(null, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    String snykProjectIssuesJsonTxt =  getRawSnykProjectIssuesAsText(project);
                    indicator.setText("Analyzing and Consolidating Issues ...");
                    SnykProjectIssues allProjectIssue = getAIManipulatedSnykIssues(snykProjectIssuesJsonTxt);
                    table[0] = getUpdatedSnykIssueTable(allProjectIssue);
                    indicator.setText("Finishing all ...");
                } catch (NoSnykIssueFoundException ex) {
                    log.debug("No issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_SNYK_ISSUES_FOUND);
                } catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
            }

        });
        return table[0];
    }

    @Deprecated
    public static JTable getSnykIssuesProgressively(Project project) {
        final JTable[] table = new JTable[1];
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                    table[0] = getAllSnykIssuesAsTable(project);
                    indicator.setText("Finishing all ...");
                } catch (NoSnykIssueFoundException ex) {
                    log.debug("No issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage("No Snyk Issues Found");
                } catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
            }

//            @Override
//            public void onFinished() {
//                table[0] = SnykDataActionAddon.getAllSnykIssuesAsTable(project);
//            }

//            public void cancel() {
//                table[0] = null;
//            }

        });
        return table[0];
    }

    public static SnykProjectIssues getAllSnykIssues(Project project) throws RuntimeException {
        SnykProjectIssues allProjectIssue = null;
        String snykProjectIssuesJsonTxt = getRawSnykProjectIssuesAsText(project);

        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String selectedLlmModelName = setting.getLlmModelComboSelection();
        String llmModel = selectedLlmModelName.split("~")[0];
        log.debug("Selected Actual LLM Model Name ?: " + llmModel);

        String aiInputModelMsg = SnykApi.getSnykProjectIssueInputAIPromt(snykProjectIssuesJsonTxt, llmModel);
        allProjectIssue = getModifiedAiSnykIssueObject(aiInputModelMsg);
        return allProjectIssue;
    }

    public static String getFixableSnykIssuesAsJsonText(Project project) throws RuntimeException {
        SnykProjectIssues allProjectIssue = getAllSnykIssues(project);

//        List<SnykFixInputModel> fixModelList = allProjectIssue.getIssues().stream()
//                .filter(value -> !value.getFixInfo().getFixedIn().isEmpty())
//                .map(value -> {
//            SnykFixInputModel fixModel = new SnykFixInputModel();
//            fixModel.setArtifactName(value.getPkgName());
//            fixModel.setFixedVersions(value.getFixInfo().getFixedIn());
//
//            return fixModel;
//        }).collect(Collectors.toList());

        List<SnykFixInputModel> fixModelList = allProjectIssue.getIssues().stream().filter(value -> value.getFixInfo().getIsFixable()).map(value -> {
            SnykFixInputModel fixModel = new SnykFixInputModel();
            fixModel.setArtifactName(value.getPkgName());
            fixModel.setFixedVersions(value.getFixInfo().getFixedIn());

            return fixModel;
        }).collect(Collectors.toList());

        if(fixModelList.isEmpty()) {
            log.debug("No Fixable Issues Found.");
            throw new NoFixableSnykIssueFoundException("No Fixable Issues Found.");

        }

        String jsonText = getJsonContentAsText(fixModelList);
//        log.debug("Final Input JSON: " + jsonText);
        return jsonText;
    }

    public static String getJsonContentAsText(List<SnykFixInputModel> fixModelList) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        String jsonText = writer.writeValueAsString(fixModelList);
//        log.debug("Final Input JSON: " + jsonText);
        return jsonText;
    }


    public static String getRawSnykProjectIssuesAsText11(Project project, String buildFileTypeName) throws RuntimeException {
        String snykProjectIssuesJsonTxt = getRawProjectIssues11(project.getName(), buildFileTypeName);
        log.debug("Temporary To be removed: snykProjectIssuesJsonTxt----->{}", snykProjectIssuesJsonTxt);
        if (snykProjectIssuesJsonTxt.equalsIgnoreCase("{\"issues\":[]}")) {
            throw new NoSnykIssueFoundException("No Snyk Issue Found.");
        }
        return snykProjectIssuesJsonTxt;
    }

    // 1............
    @Deprecated
    public static String getRawSnykProjectIssuesAsText(Project project) throws RuntimeException {
        String snykProjectIssuesJsonTxt = getRawProjectIssues(project.getName());
        log.debug("Temporary To be removed: snykProjectIssuesJsonTxt----->{}", snykProjectIssuesJsonTxt);
        if (snykProjectIssuesJsonTxt.equalsIgnoreCase("{\"issues\":[]}")) {
            throw new NoSnykIssueFoundException("No Snyk Issue Found.");
        }
        return snykProjectIssuesJsonTxt;
    }

    // 2 .................
    public static SnykProjectIssues getAIManipulatedSnykIssues(String snykProjectIssuesJsonTxt) throws RuntimeException {
        SnykProjectIssues allProjectIssue = null;

        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String selectedLlmModelName = setting.getLlmModelComboSelection();
        String llmModel = selectedLlmModelName.split("~")[0];
        String aiInputModelMsg = SnykApi.getSnykProjectIssueInputAIPromt(snykProjectIssuesJsonTxt, llmModel);
//        log.debug("Input Model: \n" + aiInputModelMsg);
        allProjectIssue = getModifiedAiSnykIssueObject(aiInputModelMsg);

        return allProjectIssue;
    }


    /**
     * Gets the all snyk issues as table.
     *
     * @param project the project
     * @return the all snyk issues as table
     */
    public static JTable getAllSnykIssuesAsTable(Project project) throws RuntimeException {
        SnykProjectIssues allProjectIssue = null;
//        String projectName = project.getName();

        String snykProjectIssuesJsonTxt = getRawProjectIssues(project.getName());
        if (snykProjectIssuesJsonTxt.equalsIgnoreCase("{\"issues\":[]}")) {
            throw new NoSnykIssueFoundException("No Snyk Issue Found.");
        }

        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String selectedLlmModelName = setting.getLlmModelComboSelection();
        String llmModel = selectedLlmModelName.split("~")[0];

        String aiInputModelMsg = SnykApi.getSnykProjectIssueInputAIPromt(snykProjectIssuesJsonTxt, llmModel);
//        log.debug("Input Model: \n" + aiInputModelMsg);
        allProjectIssue = getModifiedAiSnykIssueObject(aiInputModelMsg);

        return getUpdatedSnykIssueTable(allProjectIssue);
    }

    private static String getRawProjectIssues11(String projectName, String buildFileTypeName) throws RuntimeException {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String snykOrgComboSelection = setting.getSnykOrgComboSelection();
        String snykToken = setting.getSnykTokenTxt();
        String snykRawResponse = null;

        try {
            snykToken = !snykToken.startsWith(Constants.TOKEN_SPACE) ? Constants.TOKEN_SPACE + snykToken : snykToken;
            Map<String, String> snykOrgNameIdMap = setting.getSnykOrgNameIdMap();
            String orgId = snykOrgNameIdMap.get(snykOrgComboSelection);
            log.debug("Snyk Project Org Id: " + orgId);
//            snykRawResponse = SnykApi.getSnykIssuesAsJsonText(orgId, projectName, snykToken);
            snykRawResponse = SnykApi.getSnykIssuesAsJsonText11(projectName, buildFileTypeName, orgId, snykToken);
            log.debug("getRawProjectIssues Snyk Project Raw Response: " + snykRawResponse);
        } catch(NoSuchSnykProjectFoundException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Exception while getting Raw Snyk Issues : {}\n", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return snykRawResponse;

    }

    /**
     * Gets the raw project issues.
     *
     * @param projectName the project name
     * @return the raw project issues
     */
    @Deprecated
    private static String getRawProjectIssues(String projectName) throws RuntimeException {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String snykOrgComboSelection = setting.getSnykOrgComboSelection();
        String snykToken = setting.getSnykTokenTxt();
        String snykRawResponse = null;

        try {
            snykToken = !snykToken.startsWith(Constants.TOKEN_SPACE) ? Constants.TOKEN_SPACE + snykToken : snykToken;
            Map<String, String> snykOrgNameIdMap = setting.getSnykOrgNameIdMap();
            String orgId = snykOrgNameIdMap.get(snykOrgComboSelection);
            log.debug("Snyk Project Org Id: " + orgId);
            snykRawResponse = SnykApi.getSnykIssuesAsJsonText(orgId, projectName, snykToken);
            log.debug("getRawProjectIssues Snyk Project Raw Response: " + snykRawResponse);
        } catch (Exception e) {
            log.error("Exception while getting Raw Snyk Issues : {}\n", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return snykRawResponse;

    }

    /**
     * Gets the modified ai snyk issue object.
     *
     * @param aiInputModelMsg the ai input model msg
     * @return the modified ai snyk issue object
     */
    private static SnykProjectIssues getModifiedAiSnykIssueObject(String aiInputModelMsg) throws RuntimeException {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String clientId = setting.getClientIdStr();
        String clientSecret = setting.getClientSecretStr();
        String tokenUrl = setting.getOauthEndPointUri();
        String bearerToken = null;
        SnykProjectIssues allProjectIssue;

        try {
            bearerToken = AgentUtil.getAIBearerToken(clientId, clientSecret, tokenUrl);
        } catch (Exception e) {
            log.error("Error while retrieving token for AI for Synk Issues...", e);
            throw new RuntimeException("Error while retrieving token from AI, recheck AI input. \nIf the issue persists, please contact developer...");
        }

        String aiResponse = null;
        try {
            String aiApiUrl = AgentUtil.getFormedAIApiUrl(setting.getLlmApiEndPointUri());
            aiResponse = AgentUtil.getOnlyAnswerFromAI(aiApiUrl, bearerToken, aiInputModelMsg);
//            log.debug("Final Final Modified AI Response: " + aiResponse);

        } catch (Exception e) {
            log.error("UnExpected exception while getting response from AI:\n", e);
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

    /**
     * Gets the updated snyk issue table.
     *
     * @param allProjectIssue the all project issue
     * @return the updated snyk issue table
     */
    private static JTable getUpdatedSnykIssueTable(SnykProjectIssues allProjectIssue) {
        JTable table = null;
        AtomicInteger counter = new AtomicInteger(1);
        if (allProjectIssue == null) {
            return null;
        }

        if (allProjectIssue.getIssues() != null || allProjectIssue.getIssues().isEmpty()) {
            List<Object[]> tableData = allProjectIssue.getIssues().stream().map(value -> {
                Integer index = counter.getAndIncrement();
                String artifactName = value.getPkgName();
                String currentVersions = String.join(", ", value.getPkgVersions());
                String severity = value.getSeverity();
                boolean isFixable = value.getFixInfo().getIsFixable();
                String fixedVersions = String.join(", ", value.getFixInfo().getFixedIn());
                return new Object[]{index, artifactName, severity, isFixable, currentVersions, fixedVersions};
            }).collect(Collectors.toList());

            String[] columnHeaders = {Constants.HASH, Constants.ARTIFACT_NAME, Constants.SEVERITY, Constants.FIXABLE, Constants.CURRENT_VERSIONS, Constants.FIXED_VERSIONS};
            Object[][] rows = tableData.toArray(new Object[0][]);

            DefaultTableModel model = new DefaultTableModel(rows, columnHeaders);
            table = new JBTable(model);

            // Enable Sorting also
            table.setAutoCreateRowSorter(true);

            reSizeTable(table);
        }

        return table;
    }

    /**
     * Re size table.
     *
     * @param table the table
     */
    private static void reSizeTable(JTable table) {
        // Make header bold
        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        // Adjust column widths to fit content
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);
            int width = 50;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(renderer, row, col);
                width = Math.max(width, comp.getPreferredSize().width);
            }
            column.setPreferredWidth(width);
        }
    }

    public static String getAIInputPrompt(PsiFile file, String projectIssuesAsJsonText) {
        String buildFileName = file.getName();
        String buildFileText = file.getFileDocument().getText();
        String initialPrompt = CommonUtil.getProperty(Constants.SNYK_FIX_INPUT_PROMPT);
        String smallJsonPromtText = initialPrompt.replaceAll("\\{buildFileName\\}", buildFileName);
        smallJsonPromtText = smallJsonPromtText.replaceAll("\\{innerJson\\}", projectIssuesAsJsonText);
        smallJsonPromtText = smallJsonPromtText.replaceAll("\\{buildContents\\}", Matcher.quoteReplacement(buildFileText));

        return smallJsonPromtText;
    }

    public static String getBuildUpdateGenAIAnswer(PsiFile psiFile, String projectIssuesAsJsonText) throws Exception {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String clientId = setting.getClientIdStr();
        String clientSecret = setting.getClientSecretStr();
        String tokenUrl = setting.getOauthEndPointUri();

        String selectedLlmModelName = setting.getLlmModelComboSelection();
        String llmModel = selectedLlmModelName.split("~")[0];

        String bearerToken = AgentUtil.getAIBearerToken(clientId, clientSecret, tokenUrl);

        String aiInputPromptMsg = SnykDataActionAddon.getAIInputPrompt(psiFile, projectIssuesAsJsonText);
//        log.debug("Final Generated AI Input Prompt Text: " + aiInputPromptMsg);

        String aiInputModelMsg = AgentUtil.getFormedPrompt(aiInputPromptMsg, llmModel);
//        log.debug("Final AI Input Final Prompt: " + aiInputModelMsg);

        String aiApiUrl = AgentUtil.getFormedAIApiUrl(setting.getLlmApiEndPointUri());
        String aiResponse = AgentUtil.getOnlyAnswerFromAI(aiApiUrl, bearerToken, aiInputModelMsg);

        return aiResponse;
    }


}
