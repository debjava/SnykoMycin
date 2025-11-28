package com.ddlab.rnd.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class DisplaySnykDataAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent ae) {

        Project project = ae.getProject();
        if (project == null) return;

        String projectName = project.getName();
        Messages.showMessageDialog("Project Name: "+projectName,"Info",Messages.getInformationIcon());

//        Messages.showMessageDialog(editor.getProject(), "Hello from custom editor action!", "Custom Action", Messages.getInformationIcon());

        // TODO Tool Window Creation

//        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("MyTabularView");
////        if (toolWindow != null) {
////            toolWindow.activate(null);
////        }
//
//        if (toolWindow != null) {
//            toolWindow.show();
//        }
//
//        ViewDataTooWindowFactory factory = project.getUserData(MyKeys.MY_TOOLWINDOW_FACTORY);
//
//        // Send data to tool window
//        if (factory != null) {
//
//            // Column names
//            String[] columns = {"Column 1", "Column 2", "Column 3"};
//
//            // 4 rows × 3 columns (empty cells)
//            Object[][] rows = {
//                    {"Bagha", "Bilua", "Biradi"},
//                    {"Hati", "Ghoda", "Singha"},
//                    {"Musa", "Neula", "Kukuda"},
//                    {"Apple", "Orange", "PineApple"}
//            };
//
//            // Create table model
//            DefaultTableModel model = new DefaultTableModel(rows, columns);
//
//            // Create JTable
//            JTable table = new JTable(model);
//            factory.updateData("Updated from Action!", table);
//        }
    }
}
