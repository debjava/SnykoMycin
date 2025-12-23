package com.ddlab.rnd.action;

import org.jetbrains.annotations.NotNull;

import com.ddlab.rnd.common.util.Constants;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;

public class GroupMenuControlAction extends DefaultActionGroup  {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        String fileName = editor.getVirtualFile().getName();
        boolean isApplicableFileType = Constants.APPLICABLE_FILE_TYPES.contains(fileName);
        e.getPresentation().setVisible(isApplicableFileType);

    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT; // UI-safe
    }
}
