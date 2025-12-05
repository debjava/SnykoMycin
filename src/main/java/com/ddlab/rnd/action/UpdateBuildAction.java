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

import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;

import java.util.List;

/**
 * The Class UpdateBuildAction.
 * @author Debadatta Mishra
 */
public class UpdateBuildAction extends AnAction {

	/** The applicable file types. */
	private List<String> applicableFileTypes = List.of("pom.xml", "build.gradle", "package.json");

	/**
	 * Action performed.
	 *
	 * @param e the e
	 */
	@Override
	public void actionPerformed(AnActionEvent e) {

		// Print to Console
		Project project = e.getProject();
		if (project == null)
			return;

		SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
		String selectedComboItem = setting.getLlmModelComboSelection();
		System.out.println("Selected Combo Item: " + selectedComboItem);

		String clientSecret = setting.getClientSecretStr();
		System.out.println("Client Secret: " + clientSecret);

//            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("com.ddlab.rnd.temp-plug-setting1"); // Use the ID from plugin.xml
//            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Temp-plug-setting1");
		ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("MyPluginConsole");
		System.out.println("ToolWindow: " + toolWindow);
		if (toolWindow != null) {
			// Ensure the tool window is visible if it's not already
//                toolWindow.activate(null, true);

			// TODO for Console

//                ConsoleView consoleView = MyPluginConsoleManager.getConsoleView(project);
//                System.out.println("Consolview: "+consoleView);
//                if (consoleView != null) {
//                    consoleView.print("Appending new message to console!\n", ConsoleViewContentType.NORMAL_OUTPUT);
//                    consoleView.print("Selected Combo Item: "+selectedComboItem+"\n", ConsoleViewContentType.NORMAL_OUTPUT);
//                    consoleView.print("Client Secret: "+clientSecret+"\n", ConsoleViewContentType.NORMAL_OUTPUT);
//                    toolWindow.activate(null); // Activate the tool window to show the output
//                }

//                ContentManager contentManager = toolWindow.getContentManager();
//                // Find the content containing your ConsoleView (assuming it's the first one)
//                Content content = contentManager.getContent(0); // Or iterate to find by display name
//
//                if (content != null && content.getComponent() instanceof ConsoleView) {
//                    ConsoleView consoleView = (ConsoleView) content.getComponent();
//                    consoleView.print("Message from MyPluginAction!\n", ConsoleViewContentType.NORMAL_OUTPUT);
//                }
		}

		// End pf printing to console

		PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

		String fileType = file.getFileType().getName();
		System.out.println("File Type: " + fileType);

		String text = file.getFileDocument().getText();
//            System.out.println("Text : "+text);

//            MyPluginSettings setting = MyPluginSettings.getInstance();
//            String selectedComboItem = setting.getLlmModelComboSelection();
//            System.out.println("Selected Combo Item: "+selectedComboItem);
//
//            String clientSecret = setting.getClientSecretStr();
//            System.out.println("Client Secret: "+clientSecret);

		// Implement your action logic here
		// e.g., get the current editor, caret position, etc.
//            Editor editor = e.getData(CommonDataKeys.EDITOR);
//            if (editor != null) {
//                // Perform actions on the editor content
//                Messages.showMessageDialog(editor.getProject(), "Hello from custom editor action!", "Custom Action", Messages.getInformationIcon());
//            }
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

		boolean isApplicableFileType = applicableFileTypes.contains(fileName);

		e.getPresentation().setEnabled(isApplicableFileType);
//            e.getPresentation().setEnabledAndVisible(isApplicableFileType);

//            e.getPresentation().setEnabledAndVisible(editor != null);
	}

}