package com.ddlab.rnd.ui.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import com.ddlab.rnd.ai.AIAssistant;
import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.exception.InvalidTokenException;
import com.ddlab.rnd.setting.SynkoMycinSettings;
import com.ddlab.rnd.setting.ui.SnykoMycinSettingComponent;
import com.ddlab.rnd.ui.panel.AiDetailsPanel;
import com.ddlab.rnd.ui.panel.SnykDetailsPanel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicUiUtil {

//    public static List<String> getLLMModels() {
//        List<String> comboItems = List.of("Model A", "Model B", "Model C","Model D","Model E");
//        return comboItems;
//    }

    public static Map<String, String> getlLLMModelDetails(String clientId, String clientSecret, String tokenUrl, String aiApriEndPointUrl) throws InvalidTokenException {
        // First get the bearer token
        Map<String, String> llmModelsMap = Map.of();
        try {
            AIAssistant aiAssistant = new AIAssistant(clientId, clientSecret, tokenUrl);
            String bearerToken = aiAssistant.getBearerToken();
            if(bearerToken == null) {
                throw new InvalidTokenException("Bearer token is null, please re-check client Id, client secret, token url ");
            }
            llmModelsMap = aiAssistant.getAllLLMModelsMap(bearerToken, aiApriEndPointUrl);
            log.debug("llmMdelsMap: {}", llmModelsMap);
        } catch (RuntimeException re) {
            log.error("Exception while getting LLM models: ", re);
            throw re;
//            Messages.showErrorDialog(re.getMessage(), "SnykoMycin Error");
        }
        return llmModelsMap;
    }

    @Deprecated
    public static List<String> getActualLLMModels(String clientId, String clientSecret, String tokenUrl, String aiApriEndPointUrl) throws InvalidTokenException {
        List<String> comboItems = List.of();

        // First get the bearer token
        try {
            AIAssistant aiAssistant = new AIAssistant(clientId, clientSecret, tokenUrl);
            String bearerToken = aiAssistant.getBearerToken();
            if(bearerToken == null) {
                throw new InvalidTokenException("Bearer token is null, please re-check client Id, client secret, token url ");
            }
            comboItems = aiAssistant.getAllLLMModels(bearerToken, aiApriEndPointUrl);
        } catch (RuntimeException re) {
            log.error("Exception while getting LLM models: ", re);
            re.printStackTrace();
            throw re;
//            Messages.showErrorDialog(re.getMessage(), "SnykoMycin Error");
        }
        return comboItems;
    }


//    public static List<String> getLLMModels(String clientId, String clientSecret) {
//        log.debug("Client Id: "+clientId);
//        log.debug("Client Secret: "+clientSecret);
//        List<String> comboItems = List.of();
//
//        if(clientId == null || clientId.isEmpty()) {
//            System.out.println("Client Id is empty ..");
//            log.debug("Client Id is empty or blank..");
//            Messages.showErrorDialog("Client Id cannot empty or blank", "Error title");
//        }
//        comboItems = List.of("Model A", "Model B", "Model C","Model D","Model E");
//        return comboItems;
//    }

    public static boolean isNullOrEmptyOrBlank(String str) {
        return str == null || str.isBlank();
    }


    public static List<String> getOrgNames() {
        List<String> comboItems = List.of("Org-A", "Org-B", "Org-C","Org-D","Org-E");
        return comboItems;
    }

    public static List<String> getActualOrgNames(String snykUri, String snykToken) {
        snykToken = "token " + snykToken;
        List<String> comboItems = List.of("Org-A", "Org-B", "Org-C","Org-D","Org-E");
        return comboItems;
    }

    public static void saveAiPanelSetting(SynkoMycinSettings settings, SnykoMycinSettingComponent component) {
        // AI Part
        AiDetailsPanel aiPanel = (AiDetailsPanel) component.getAiDetailsPanel();
        settings.setClientIdStr(aiPanel.getClientIdTxt().getText());
        settings.setClientSecretStr(aiPanel.getClientSecretTxt().getText());
        settings.setOauthEndPointUri(aiPanel.getOauthEndPointTxt().getText());
        settings.setLlmApiEndPointUri(aiPanel.getLlmApiEndPointTxt().getText());

        JComboBox<String> llmModelComboBox = aiPanel.getLlmModelcomboBox();
        List<String> allLlmModelComboItems = getComboBoxItems(llmModelComboBox);
        settings.setLlmModelComboItems(allLlmModelComboItems);
        settings.setLlmModelComboSelection((String) llmModelComboBox.getSelectedItem());

        // Save the LLM Model Map, Type and Size
        settings.setLlmModelsMap(aiPanel.getLlmModelMap());// Save the Map for future use
        settings.setLlmModelType(aiPanel.getModelTypeText().getText());
        settings.setLlmModelSize(aiPanel.getModelSizeText().getText());

    }

    public static void saveSnykPanelSetting(SynkoMycinSettings settings, SnykoMycinSettingComponent component) {
        // Snyk Part
        SnykDetailsPanel snykPanel = (SnykDetailsPanel) component.getSnykPanel();
        settings.setSnykUriTxt(snykPanel.getSnykUriTxt().getText());
        settings.setSnykTokenTxt(snykPanel.getSnykTokentxt().getText());

        JComboBox<String> snykOrgComboBox = snykPanel.getOrgNameComboBox();
        List<String> snykOrgComboItems = getComboBoxItems(snykOrgComboBox);
        settings.setSnykOrgComboItems(snykOrgComboItems);
        settings.setSnykOrgComboSelection((String) snykOrgComboBox.getSelectedItem());

        // TODO also compare two different list or maps
        // Or check from the Snyk Panel
        if(settings.getSnykOrgNameIdMap() == null) {
            settings.setSnykOrgNameIdMap(snykPanel.getSnykOrgNameIdMap());
        } else {
            settings.setSnykOrgNameIdMap(settings.getSnykOrgNameIdMap());
        }
    }

    public static boolean isAiPanelModified(SynkoMycinSettings settings, SnykoMycinSettingComponent component) {
        // For AI Panel
        AiDetailsPanel aiPanel = (AiDetailsPanel) component.getAiDetailsPanel();

        String selectedLlmModel = (String) aiPanel.getLlmModelcomboBox().getSelectedItem();

        return !aiPanel.getClientIdTxt().getText().equals(settings.getClientIdStr())
                || !aiPanel.getClientSecretTxt().getText().equals(settings.getClientSecretStr())
                || !aiPanel.getOauthEndPointTxt().getText().equals(settings.getOauthEndPointUri())
                || !aiPanel.getLlmApiEndPointTxt().getText().equals(settings.getLlmApiEndPointUri())
                || !(selectedLlmModel != null && selectedLlmModel.equals(settings.getLlmModelComboSelection()));
    }

    public static boolean isSnykPanelModified(SynkoMycinSettings settings, SnykoMycinSettingComponent component) {
        // For Snyk Panel
        SnykDetailsPanel snykPanel = (SnykDetailsPanel) component.getSnykPanel();
        String selectedSnykOrg = (String) snykPanel.getOrgNameComboBox().getSelectedItem();

        return !snykPanel.getSnykUriTxt().getText().equals(settings.getSnykUriTxt())
                || !snykPanel.getSnykTokentxt().getText().equals(settings.getSnykTokenTxt())
                || !(selectedSnykOrg != null && selectedSnykOrg.equals(settings.getSnykOrgComboSelection()));
//                .equals(settings.getSnykOrgComboSelection());
//                || !snykPanel.getOrgNameComboBox().getSelectedItem().toString()

    }

    public static void resetAiPanel(SynkoMycinSettings settings, SnykoMycinSettingComponent component) {
        // For AI
        AiDetailsPanel aiPanel = (AiDetailsPanel) component.getAiDetailsPanel();
        aiPanel.getClientIdTxt().setText(settings.getClientIdStr());
        aiPanel.getClientSecretTxt().setText(settings.getClientSecretStr());
        aiPanel.getOauthEndPointTxt().setText(settings.getOauthEndPointUri());
        aiPanel.getLlmApiEndPointTxt().setText(settings.getLlmApiEndPointUri());

        JComboBox<String> llmModelComboBox = aiPanel.getLlmModelcomboBox();
        List<String> llmModelComboItems = settings.getLlmModelComboItems();
        if(llmModelComboItems != null) {
            llmModelComboItems.forEach(value -> llmModelComboBox.addItem(value));
        }
        llmModelComboBox.setSelectedItem(settings.getLlmModelComboSelection());

        aiPanel.getModelTypeText().setText(settings.getLlmModelType());
        aiPanel.getModelSizeText().setText(settings.getLlmModelSize());

//        aiPanel.getModelSizeTxt().setText(settings.getLlmModelSize());
//        aiPanel.getModelTypeTxt().setText(settings.getLlmModelType());

    }

    public static void resetSnykPanel(SynkoMycinSettings settings, SnykoMycinSettingComponent component) {
        // For Snyk
        SnykDetailsPanel snykPanel = (SnykDetailsPanel) component.getSnykPanel();
        if(settings.getSnykUriTxt() == null || settings.getSnykUriTxt().isEmpty()) {
            snykPanel.getSnykUriTxt().setText(Constants.DEFAULT_SNYK_URI);
        } else {
            snykPanel.getSnykUriTxt().setText(settings.getSnykUriTxt());
        }
        snykPanel.getSnykTokentxt().setText(settings.getSnykTokenTxt());

        JComboBox<String> snykOrgNameComboBox = snykPanel.getOrgNameComboBox();
        List<String> snykOrgComboItems = settings.getSnykOrgComboItems();
        if(snykOrgComboItems != null) {
            snykOrgComboItems.forEach(value -> snykOrgNameComboBox.addItem(value));
        }
        snykOrgNameComboBox.setSelectedItem(settings.getSnykOrgComboSelection());
    }

    private static List<String> getComboBoxItems(JComboBox<String> comboBox) {
        ComboBoxModel<String> model = comboBox.getModel();
        List<String> items = new ArrayList<>();

        for (int i = 0; i < model.getSize(); i++) {
            items.add(String.valueOf(model.getElementAt(i)));
        }
        return items;
    }
}
