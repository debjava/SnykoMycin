package com.ddlab.rnd.setting;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ddlab.rnd.common.util.Constants;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

import lombok.Getter;
import lombok.Setter;

@State(
        name = "SnykomycinSettings",
        storages = @Storage("SnykomycinSettings.xml")
)
@Getter
@Setter
public class SynkoMycinSettings implements PersistentStateComponent<SynkoMycinSettings> {

    private String clientIdStr;
    private String clientSecretStr;
    private String oauthEndPointUri;
    private String llmModelComboSelection;
    private java.util.List<String> llmModelComboItems;
    private String llmModelType;
    private String llmModelSize;
    private String llmApiEndPointUri;

    private Map<String, String> llmModelsMap;

    // Snyk details
    private String snykUriTxt;
    private String snykTokenTxt;
    private java.util.List<String> snykOrgComboItems;
    private String snykOrgComboSelection;
    private Map<String, String> snykOrgNameIdMap;

    public static SynkoMycinSettings getInstance() {
        return com.intellij.openapi.application.ApplicationManager.getApplication().getService(SynkoMycinSettings.class);
    }

    @Override
    public @Nullable SynkoMycinSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SynkoMycinSettings state) {
        //        XmlSerializerUtil.copyBean(state, this);
        this.clientIdStr = state.clientIdStr;
        this.clientSecretStr = state.clientSecretStr;
        this.oauthEndPointUri = state.oauthEndPointUri;

        this.llmModelComboItems = state.llmModelComboItems;


        // Trying to save the LLM model type and size
        this.llmModelsMap = state.llmModelsMap;
        this.llmModelType = state.llmModelType;
        this.llmModelSize = state.llmModelSize;


        this.llmModelComboSelection = state.llmModelComboSelection;
        this.llmApiEndPointUri = state.llmApiEndPointUri;

        // For Snyk
        this.snykUriTxt = (state.snykUriTxt == null || state.snykUriTxt.isEmpty()) ? Constants.DEFAULT_SNYK_URI : state.snykUriTxt;
//        this.snykUriTxt = state.snykUriTxt;
        this.snykTokenTxt = state.snykTokenTxt;
        this.snykOrgComboItems = state.snykOrgComboItems;
        this.snykOrgComboSelection = state.snykOrgComboSelection;
        this.snykOrgNameIdMap = state.snykOrgNameIdMap;


    }
}
