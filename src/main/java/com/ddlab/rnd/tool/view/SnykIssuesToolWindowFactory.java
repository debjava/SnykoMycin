package com.ddlab.rnd.tool.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SnykIssuesToolWindowFactory implements ToolWindowFactory  {

//    private static MyToolWindowPanel myPanel;
//
//    @Override
//    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
//        myPanel = new MyToolWindowPanel();
//
////        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        ContentFactory contentFactory = ContentFactory.getInstance();
//        Content content = contentFactory.createContent(myPanel.getContent(), "", false);
//        toolWindow.getContentManager().addContent(content);
//    }
//
//    public static MyToolWindowPanel getMyPanel() {
//        return myPanel;
//    }





    private JScrollPane scrollPane;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        scrollPane = new JScrollPane();

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(scrollPane, "", false);
        toolWindow.getContentManager().addContent(content);

        // Store reference to this factory for external updates
//        project.putUserData(SnykoKeys.SNYKO_TOOLWINDOW_FACTORY, this);
    }

//    public void updateData(String text, JTable table) {
////        JScrollPane scrollPane = new JScrollPane();
//        scrollPane.setViewportView(table);
//    }


}
