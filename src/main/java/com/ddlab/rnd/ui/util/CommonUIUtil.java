package com.ddlab.rnd.ui.util;

import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;

public class CommonUIUtil {

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

    public static void validateSnykInputsFromSetting() {

    }

    public static void validateAiInputsFromSetting() {
        SynkoMycinSettings setting = SynkoMycinSettings.getInstance();
        String clientId = setting.getClientIdStr();
        String clientSecret = setting.getClientSecretStr();
        String oAuthTokenUrl = setting.getOauthEndPointUri();
        String aiApiEndPointUri = setting.getLlmApiEndPointUri();

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
    }
}
