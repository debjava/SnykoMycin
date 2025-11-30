package com.ddlab.rnd.action;

import com.ddlab.rnd.action.addon.SnykDataActionAddon;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@Slf4j
public class DisplaySnykDataAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent ae) {
        Project project = ae.getProject();
        if (project == null) return;

        // Handle Exception
        JTable table = SnykDataActionAddon.getProgressiveSnykProjectIssuesTable(project);
        SnykDataActionAddon.updateSnykIssueToolWIndow(project, table);

    }

}
