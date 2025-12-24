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
package com.ddlab.rnd.action;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.ddlab.rnd.action.service.SnykActionServiceImpl;
import com.ddlab.rnd.build.modifier.BuildModifiable;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.NoFixableSnykIssueFoundException;
import com.ddlab.rnd.exception.NoModificationRequiredException;
import com.ddlab.rnd.exception.NoSnykIssueFoundException;
import com.ddlab.rnd.exception.NoSuchSnykProjectFoundException;
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class UpdateBuildAction.
 *
 * @author Debadatta Mishra
 */
@Slf4j
public class UpdateBuildAction extends AnAction {

    /**
     * Action performed.
     *
     * @param e the e
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        log.debug("\n************** START - TRACKING DATA FOR ANALYSIS **************\n");
        Project project = e.getProject();
        if (project == null)
            return;

        log.debug("Action Type: {},  Project Name: {}", "Update", project.getName());
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        CommonUIUtil.validateAiInputsFromSetting();
        performOperationProgressively(psiFile, project);
        log.debug("\n************** END - TRACKING DATA FOR ANALYSIS **************\n");
    }

    /**
     * Update.
     *
     * @param e the e
     */
    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        String fileName = editor.getVirtualFile().getName();
        System.out.println("Update File Name: " + fileName);
        String fileType = editor.getVirtualFile().getFileType().getName();
        System.out.println("Update File Type: " + fileType);

        boolean isApplicableFileType = Constants.APPLICABLE_FILE_TYPES.contains(fileName);

        e.getPresentation().setEnabled(isApplicableFileType);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT; // UI-safe
    }

    // ~~~~~~~~~~~~~~ all private methods below ~~~~~~~~~~~

    private void performOperationProgressively(PsiFile psiFile, Project project) {
        final String fileName = psiFile.getVirtualFile().getName();
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.PROD_TITLE, true) {

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);

                    indicator.setText("Analyzing the dependencies");
                    Map<String, String> fixedDependencyMap = SnykActionServiceImpl.getHighestFixableSnykIssuesDetails(project, fileName);
                    if(fixedDependencyMap.isEmpty()) {
                        CommonUIUtil.showAppSuccessfulMessage(Constants.NO_FIXABLE_ISSUES_MSG);
                        return;
                    }

                    String srcBuildFilePath = psiFile.getVirtualFile().getPath();
                    String destnBuildFilePath = srcBuildFilePath;
                    log.debug("Build fileName: {}", fileName);
                    String content = Files.readString(Paths.get(srcBuildFilePath));
                    indicator.setText("Creating a backup and updating build file ...");

                    // Finally create a backup file
                    CommonUIUtil.createBackFile(project, psiFile.getName());
                    // Update the build.gradle file contents
                    indicator.setText("Finishing all ...");

                    BuildModifiable buildModifiable = Constants.BUILD_MODIFIER_MAP.get(fileName);
                    buildModifiable.modifyBuild(content, fixedDependencyMap, Paths.get(destnBuildFilePath));

                    CommonUIUtil.showAppSuccessfulMessage(Constants.UPDATE_BUILD_SUCCESS_MSG);

                } catch(NoSuchSnykProjectFoundException nspe) {
                    log.error("No project found in Snyk");
                    CommonUIUtil.showAppErrorMessage(Constants.NO_SNYK_PROJECT_FOUND_MSG);
                }
                catch (NoSnykIssueFoundException ex) {
                    log.error("No issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_SNYK_ISSUES_FOUND);
                }
                catch(NoFixableSnykIssueFoundException nfse) {
                    log.error("No fixable issues found in Snyk");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_FIXABLE_SNYK_ISSUE_MSG);
                }
                catch (NoModificationRequiredException nre) {
                    log.error("No modification required in the build file");
                    CommonUIUtil.showAppSuccessfulMessage(Constants.NO_MODIFICATION_MSG);
                }
                catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex);
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                } finally {
                    CommonUIUtil.showWarningNotifiation(Constants.DISCLAIMER_MSG);
                }
            }

        });
    }

}