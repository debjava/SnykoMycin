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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * The Class DisplaySnykDataAction.
 * @author Debadatta Mishra
 */
@Slf4j
public class DisplaySnykDataAction extends AnAction {

	private final List<String> applicableFileTypes = List.of("pom.xml", "build.gradle", "package.json");

	/**
	 * Action performed.
	 *
	 * @param ae the ae
	 */
	@Override
	public void actionPerformed(@NotNull AnActionEvent ae) {
		Project project = ae.getProject();
		if (project == null)
			return;
        try {
			log.debug("Display Action Project Name: " + project.getName());
            CommonUIUtil.validateAiInputsFromSetting();
//            JTable table = SnykDataActionAddon.getSnykIssuesProgressively(project);

			JTable table = SnykDataActionAddon.getProgressiveSnykIssues(project);


			if(table != null && table.getRowCount() >= 0) {
				ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(Constants.SNYK_ISSUES);
				Content content = toolWindow.getContentManager().getContent(0);
				JComponent component = content.getComponent();
				if(component instanceof JScrollPane scrollPane) {
					scrollPane.setViewportView(table);
				}
				toolWindow.show();
			}

        }
		catch (Exception e) {
            log.error("Exception for DisplaySnykDataAction: {}", e.getMessage());
        }
	}

	@Override
	public void update(AnActionEvent e) {
		Editor editor = e.getData(CommonDataKeys.EDITOR);
		String fileName = editor.getVirtualFile().getName();
		boolean isApplicableFileType = applicableFileTypes.contains(fileName);
		e.getPresentation().setEnabled(isApplicableFileType);
	}

	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread() {
		return ActionUpdateThread.EDT; // UI-safe
	}
}
