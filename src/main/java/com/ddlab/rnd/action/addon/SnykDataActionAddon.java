package com.ddlab.rnd.action.addon;

import com.ddlab.rnd.ai.AgentUtil;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.ddlab.rnd.snyk.ai.out.model.SnykProjectIssues;
import com.ddlab.rnd.snyk.api.SnykApi;
import com.ddlab.rnd.tool.view.SnykIssuesToolWindowFactory;
import com.ddlab.rnd.tool.view.SnykoKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.table.JBTable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class SnykDataActionAddon {

    public static JTable getProgressiveSnykProjectIssuesTable(Project project) {
        final JTable[] table = new JTable[1];
        // Run in Progress
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.SNYK_ISSUES, true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);
                // Fetching Snyk Issues a long-running task
                table[0] = SnykDataActionAddon.getAllSnykIssuesAsTable(project);
            }
//            public void cancel() {
//                table[0] = null;
//            }
        });
        return table[0];
    }

    public static void updateSnykIssueToolWIndow(Project project, JTable table) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Constants.SNYK_ISSUES);
        log.debug("Showing Tool Window: " + toolWindow);

        if (toolWindow != null) {
            toolWindow.show();
        }
        SnykIssuesToolWindowFactory factory = project.getUserData(SnykoKeys.SNYKO_TOOLWINDOW_FACTORY);
        if (factory != null) {
            // Get Table along with Data
            factory.updateData("Updated from Snyk Data Action!", table);
        }

    }

    public static JTable getAllSnykIssuesAsTable(Project project) {
        String projectName = project.getName();
        log.debug("Display Action Project Name: " + projectName);
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String selectedLlmModelName = setting.getLlmModelComboSelection();
        log.debug("What is the selected LLM Model Name: " + selectedLlmModelName);
        String llmModel = selectedLlmModelName.split("~")[0];
        log.debug("What is the Actual LLM Model Name: " + llmModel);
        String snykProjectIssuesJsonTxt = getRawProjectIssues(projectName);
        String aiInputModelMsg = SnykApi.getSnykProjectIssueInputAIPromt(snykProjectIssuesJsonTxt, llmModel);
//        log.debug("Input Model: \n" + aiInputModelMsg);
        SnykProjectIssues allProjectIssue = getModifiedAiSnykIssueObject(aiInputModelMsg);
//        log.debug("All Project Issues: " + allProjectIssue);

        return getUpdatedSnykIssueTable(allProjectIssue);
    }

    private static String getRawProjectIssues(String projectName) {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String snykOrgComboSelection = setting.getSnykOrgComboSelection();
        String snykToken = setting.getSnykTokenTxt();
        snykToken = !snykToken.startsWith(Constants.TOKEN_SPACE) ? Constants.TOKEN_SPACE + snykToken : snykToken;
        Map<String, String> snykOrgNameIdMap = setting.getSnykOrgNameIdMap();
        String orgId = snykOrgNameIdMap.get(snykOrgComboSelection);
        log.debug("orgId: " + orgId);
        String snykProjectIssuesJsonTxt = SnykApi.getSnykIssuesAsJsonText(orgId, projectName, snykToken);
        return snykProjectIssuesJsonTxt;
    }

    private static SnykProjectIssues getModifiedAiSnykIssueObject(String aiInputModelMsg) {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String clientId = setting.getClientIdStr();
        String clientSecret = setting.getClientSecretStr();
        String tokenUrl = setting.getOauthEndPointUri();
        String bearerToken = AgentUtil.getAIBearerToken(clientId, clientSecret, tokenUrl);
//        log.debug("AI Bearer Token: " + bearerToken);
        String aiApiUrl = setting.getLlmApiEndPointUri();
//        log.debug("Initial AI API URL: " + aiApiUrl);
//        aiApiUrl = aiApiUrl.endsWith("/") ? aiApiUrl + "chat/completions" : aiApiUrl + "/chat/completions";
        aiApiUrl = aiApiUrl.endsWith("/") ? aiApiUrl + Constants.AI_CHAT_COMPLETIONS : aiApiUrl + "/" +Constants.AI_CHAT_COMPLETIONS;
//        log.debug("Final AI API URL: " + aiApiUrl);

        String aiResponse = null;
        try {
            aiResponse = AgentUtil.getOnlyAnswerFromAI(aiApiUrl, bearerToken, aiInputModelMsg);
//            log.debug("Final AI Response: \n" + aiResponse);
        } catch (Exception e) {
            log.error("Error while asking AI", e);
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        SnykProjectIssues allProjectIssue = mapper.readValue(aiResponse, SnykProjectIssues.class);
//        log.debug("All Project Issues: " + allProjectIssue);

        return allProjectIssue;
    }

    private static JTable getUpdatedSnykIssueTable(SnykProjectIssues allProjectIssue) {
        AtomicInteger counter = new AtomicInteger(1);
        List<Object[]> tableData = allProjectIssue.getIssues().stream().map(value -> {
            Integer index = counter.getAndIncrement();
            String artifactName = value.getPkgName();
            String currentVersions = String.join(", ", value.getPkgVersions());
            String severity = value.getSeverity();
            boolean isFixable = value.getFixInfo().getIsFixable();
            String fixedVersions = String.join(", ", value.getFixInfo().getFixedIn());
            return new Object[]{index, artifactName, severity, isFixable, currentVersions, fixedVersions};
        }).collect(Collectors.toList());

//        String[] columnHeaders = {"#", "Artifact Name", "Current Versions", "Severity", "Fixable", "Fixed Versions"};
        String[] columnHeaders ={Constants.HASH, Constants.ARTIFACT_NAME, Constants.SEVERITY, Constants.FIXABLE, Constants.CURRENT_VERSIONS, Constants.FIXED_VERSIONS};
        Object[][] rows = tableData.toArray(new Object[0][]);

        DefaultTableModel model = new DefaultTableModel(rows, columnHeaders);
        JTable table = new JBTable(model);

        reSizeTable(table);

        return table;
    }

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

}
