package com.ddlab.rnd.ui.util;

import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUIUtil {


    public static void showAppSuccessfulMessage(String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Messages.showInfoMessage(message, Constants.SNYKO_TITLE);
        });
    }

    public static void showAppErrorMessage(String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Messages.showErrorDialog(message, Constants.ERR_TITLE);
        });
    }

    public static void showErrorNotifiation(String msg) {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Snykomycin Notification Group")
                .createNotification(msg, NotificationType.ERROR)
                .notify(projects[0]);
    }

    public static void validateAiInputsFromSetting() {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String clientId = setting.getClientIdStr();
        String clientSecret = setting.getClientSecretStr();
        String oAuthTokenUrl = setting.getOauthEndPointUri();
        String aiApiEndPointUri = setting.getLlmApiEndPointUri();
        String snykApiEndPointUri = setting.getSnykUriTxt();
        String snykToken = setting.getSnykTokenTxt();

        if (clientId == null || clientId.isEmpty()) {
            Messages.showErrorDialog("Client Id cannot be empty", Constants.ERR_TITLE);
            throw new IllegalArgumentException("ClientId cannot be empty!");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            Messages.showErrorDialog("Client Secret cannot be empty", Constants.ERR_TITLE);
            throw new IllegalArgumentException("ClientSecret cannot be empty!");
        }
        if (oAuthTokenUrl == null || oAuthTokenUrl.isEmpty()) {
            Messages.showErrorDialog("OAuth End Point cannot be empty", Constants.ERR_TITLE);
            throw new IllegalArgumentException("OAuth End Point cannot be empty!");
        }
        if (aiApiEndPointUri == null || aiApiEndPointUri.isEmpty()) {
            Messages.showErrorDialog("LLM Api End Point cannot be empty", Constants.ERR_TITLE);
            throw new IllegalArgumentException("LLM Api End Point cannot be empty!");
        }

        if(snykApiEndPointUri == null || snykApiEndPointUri.isEmpty()) {
            Messages.showErrorDialog("Snyk API URI cannot be empty", Constants.ERR_TITLE);
            throw new IllegalArgumentException("Snyk API URI cannot be empty!");
        }

        if (snykToken == null || snykToken.isEmpty()) {
            Messages.showErrorDialog("Snyk Token cannot be empty", Constants.ERR_TITLE);
            throw new IllegalArgumentException("Snyk Token cannot be empty!");
        }

    }

    public static void createBackFile(Project project, String buildFileName) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FMT);
        createBackupAndCopy(project, buildFileName);

        // Refresh the project
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null) {
            baseDir.refresh(true, true); // recursive refresh
        }
    }

    private static void createBackupAndCopy(Project project, String buildFileName) {
        String formattedDate = new SimpleDateFormat(Constants.DATE_FMT).format(new Date());
        String projectBasePath = project.getBasePath();
        File backupDir = new File(projectBasePath + File.separator + Constants.BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        Path srcBasePath = Paths.get(projectBasePath, buildFileName);
        Path backDestnFilePath = Paths.get(backupDir.getAbsolutePath(), buildFileName + "." + formattedDate);

        try {
//            Files.write(backFilePath, buildFileText.getBytes());
            Files.copy(srcBasePath, backDestnFilePath);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void updateBuildFileContents(PsiFile psiFile, Project project, String buildContents) {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if(document != null) {
                document.setText(buildContents);
            }
        });
    }
}
