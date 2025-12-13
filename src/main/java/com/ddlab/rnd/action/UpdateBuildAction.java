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

import com.ddlab.rnd.action.addon.SnykDataActionAddon;
import com.ddlab.rnd.common.util.Constants;
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
import org.jetbrains.annotations.NotNull;

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
        Project project = e.getProject();
        if (project == null)
            return;
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        CommonUIUtil.validateAiInputsFromSetting();
        performOperationProgressively(psiFile, project);
    }

    /**
     * Update.
     *
     * @param e the e
     */
    @Override
    public void update(AnActionEvent e) {
        // Control visibility and enablement of the action
        // e.g., enable only if an editor is active
//            VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
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
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {
//        ProgressManager.getInstance().run(new Task.Modal(project, Constants.SNYKOMYCIN_PROGRESS_TITLE, true) {

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText(Constants.SNYK_ISSUES_PROGRESS_MSG);

                    String projectIssuesAsJsonText = SnykDataActionAddon.getFixableSnykIssuesAsJsonText(project);
                    indicator.setText("Analyzing Dependencies for issues reported by Snyk ...");
                    String aiResponse = SnykDataActionAddon.getBuildUpdateGenAIAnswer(psiFile, projectIssuesAsJsonText);

                    indicator.setText("Creating a backup and updating build file ...");

                    // Finally create a backup file
                    CommonUIUtil.createBackFile(project, psiFile.getName());
                    // Update the build.gradle file contents
                    indicator.setText("Finishing all ...");
                    CommonUIUtil.updateBuildFileContents(psiFile, project,aiResponse);
                    CommonUIUtil.showAppSuccessfulMessage(Constants.UPDATE_BUILD_SUCCESS_MSG);
                } catch (Exception ex) {
                    log.error("Error Messages to get Snyk Issues: {}", ex.getMessage());
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
            }

        });
    }

}