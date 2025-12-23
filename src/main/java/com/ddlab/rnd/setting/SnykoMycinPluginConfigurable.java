package com.ddlab.rnd.setting;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import com.ddlab.rnd.setting.ui.SnykoMycinSettingComponent;
import com.ddlab.rnd.ui.util.BasicUiUtil;
import com.intellij.openapi.options.Configurable;

public class SnykoMycinPluginConfigurable implements Configurable {

    private JPanel panel;
    private SnykoMycinSettingComponent component;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "My Plugin Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        panel = createUIAndGetPanel();
        return panel;
    }

    @Override
    public boolean isModified() {
        SynkoMycinSettings settings = SynkoMycinSettings.getInstance();
        return BasicUiUtil.isAiPanelModified(settings,component)
                || BasicUiUtil.isSnykPanelModified(settings,component);
    }

    @Override
    public void apply() {
        SynkoMycinSettings settings = SynkoMycinSettings.getInstance();

        BasicUiUtil.saveAiPanelSetting(settings,component);
        BasicUiUtil.saveSnykPanelSetting(settings,component);
    }

    @Override
    public void reset() {
        SynkoMycinSettings settings = SynkoMycinSettings.getInstance();

        BasicUiUtil.resetAiPanel(settings,component);
        BasicUiUtil.resetSnykPanel(settings,component);

    }

    // ~~~~~~~~ private methods ~~~~~~~~

    private JPanel createUIAndGetPanel() {
        component = new SnykoMycinSettingComponent();
        return component.getMainPanel();
    }

//    @Override
//    public void disposeUIResources() {
//        panel = null;
//        textField = null;
//        comboBox = null;
//    }
}
