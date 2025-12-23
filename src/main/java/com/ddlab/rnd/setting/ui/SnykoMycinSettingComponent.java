package com.ddlab.rnd.setting.ui;


import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ddlab.rnd.ui.panel.AiDetailsPanel;
import com.ddlab.rnd.ui.panel.SnykDetailsPanel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnykoMycinSettingComponent {

    private JPanel mainPanel;
    private JPanel aiDetailsPanel;
    private JPanel snykPanel;

    public SnykoMycinSettingComponent() {
        mainPanel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        mainPanel.setLayout(gridBagLayout);

        aiDetailsPanel = new AiDetailsPanel();
        GridBagConstraints gbc_aiDetailsPanel = new GridBagConstraints();
        gbc_aiDetailsPanel.insets = new Insets(0, 0, 5, 0);
        gbc_aiDetailsPanel.fill = GridBagConstraints.BOTH;
        gbc_aiDetailsPanel.gridx = 0;
        gbc_aiDetailsPanel.gridy = 0;
        mainPanel.add(aiDetailsPanel, gbc_aiDetailsPanel);

        JLabel commonInfoLbl = new JLabel("* mark fields are required to fill");
        commonInfoLbl.setForeground(new Color(255, 0, 0));
        GridBagConstraints gbc_commonInfoLbl = new GridBagConstraints();
        gbc_commonInfoLbl.insets = new Insets(0, 0, 5, 0);
        gbc_commonInfoLbl.gridx = 0;
        gbc_commonInfoLbl.gridy = 1;
        mainPanel.add(commonInfoLbl, gbc_commonInfoLbl);

        snykPanel = new SnykDetailsPanel();
        GridBagConstraints gbc_snykPanel = new GridBagConstraints();
        gbc_snykPanel.fill = GridBagConstraints.BOTH;
        gbc_snykPanel.gridx = 0;
        gbc_snykPanel.gridy = 2;
        mainPanel.add(snykPanel, gbc_snykPanel);
    }
}
