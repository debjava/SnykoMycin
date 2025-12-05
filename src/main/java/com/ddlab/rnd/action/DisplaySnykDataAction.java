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
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * The Class DisplaySnykDataAction.
 * @author Debadatta Mishra
 */
@Slf4j
public class DisplaySnykDataAction extends AnAction {

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

		// Handle Exception
//		JTable table = SnykDataActionAddon.getProgressiveSnykProjectIssuesTable(project);


//        CommonUIUtil.validateAiInputsFromSetting();
//        JTable table = SnykDataActionAddon.getSnykIssuesProgressively(project);
//		SnykDataActionAddon.updateSnykIssueToolWindow(project, table);

        try {
            CommonUIUtil.validateAiInputsFromSetting();
            JTable table = SnykDataActionAddon.getSnykIssuesProgressively(project);
            SnykDataActionAddon.updateSnykIssueToolWindow(project, table);
        } catch (Exception e) {
            log.error("Exception for DisplaySnykDataAction: {}", e.getMessage());
        }


	}

}
