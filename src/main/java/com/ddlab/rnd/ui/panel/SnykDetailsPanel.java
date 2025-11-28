package com.ddlab.rnd.ui.panel;

import com.ddlab.rnd.ui.util.BasicUiUtil;
import com.ddlab.rnd.ui.util.SnykUiUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
@Getter
@Setter
public class SnykDetailsPanel extends JPanel {
	private JTextField snykUriTxt;
	private JTextField snykTokentxt;
    private JComboBox<String> orgNameComboBox;

	public SnykDetailsPanel() {
		setBorder(new TitledBorder(null, "Snyk Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		GridBagLayout gbl_snykPanel = new GridBagLayout();
		gbl_snykPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_snykPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_snykPanel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_snykPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gbl_snykPanel);

		JLabel snykUriLbl = new JLabel("*Snyk Endpoint URI:");
		GridBagConstraints gbc_snykUriLbl = new GridBagConstraints();
		gbc_snykUriLbl.insets = new Insets(0, 0, 5, 5);
		gbc_snykUriLbl.anchor = GridBagConstraints.EAST;
		gbc_snykUriLbl.gridx = 0;
		gbc_snykUriLbl.gridy = 0;
		add(snykUriLbl, gbc_snykUriLbl);

		snykUriTxt = new JTextField();
		GridBagConstraints gbc_snykUriTxt = new GridBagConstraints();
		gbc_snykUriTxt.insets = new Insets(0, 0, 5, 5);
		gbc_snykUriTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_snykUriTxt.gridx = 1;
		gbc_snykUriTxt.gridy = 0;
		add(snykUriTxt, gbc_snykUriTxt);
		snykUriTxt.setColumns(10);

		JLabel snykTokenLbl = new JLabel("*Snyk Token:");
		GridBagConstraints gbc_snykTokenLbl = new GridBagConstraints();
		gbc_snykTokenLbl.anchor = GridBagConstraints.EAST;
		gbc_snykTokenLbl.insets = new Insets(0, 0, 5, 5);
		gbc_snykTokenLbl.gridx = 0;
		gbc_snykTokenLbl.gridy = 1;
		add(snykTokenLbl, gbc_snykTokenLbl);

		snykTokentxt = new JTextField();
		GridBagConstraints gbc_snykTokentxt = new GridBagConstraints();
		gbc_snykTokentxt.insets = new Insets(0, 0, 5, 5);
		gbc_snykTokentxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_snykTokentxt.gridx = 1;
		gbc_snykTokentxt.gridy = 1;
		add(snykTokentxt, gbc_snykTokentxt);
		snykTokentxt.setColumns(10);

		JLabel orgNameLbl = new JLabel("Org Name:");
		GridBagConstraints gbc_orgNameLbl = new GridBagConstraints();
		gbc_orgNameLbl.anchor = GridBagConstraints.EAST;
		gbc_orgNameLbl.insets = new Insets(0, 0, 0, 5);
		gbc_orgNameLbl.gridx = 0;
		gbc_orgNameLbl.gridy = 2;
		add(orgNameLbl, gbc_orgNameLbl);

		orgNameComboBox = new JComboBox<String>();
		GridBagConstraints gbc_orgNameComboBox = new GridBagConstraints();
		gbc_orgNameComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_orgNameComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_orgNameComboBox.gridx = 1;
		gbc_orgNameComboBox.gridy = 2;
		add(orgNameComboBox, gbc_orgNameComboBox);

		JButton snykOrgGetBtn = new JButton("Get Orgs");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 2;
		add(snykOrgGetBtn, gbc_btnNewButton);

        snykOrgGetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orgNameComboBox.removeAllItems(); // clear existing items
                String snykUri = snykUriTxt.getText();
                String snykToken = snykTokentxt.getText();
                log.debug("Snyk URI: " + snykUri);
                log.debug("Snyk Token: " + snykToken);

//                populateOrgNames(snykUri, snykToken);
                populateOrgNamesWithProgress(snykUri, snykToken);
            }
        });
	}

    private void populateOrgNamesWithProgress(String snykUri, String snykToken) {

        ProgressManager.getInstance().run(new Task.Modal(null, "Fetching Snyk Org Names ...", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Please wait, fetching Snyk Org Names...");

                // Simulate long-running work
                populateOrgNames(snykUri, snykToken);
            }
        });


    }

    private void populateOrgNames(String snykUri, String snykToken) {
        java.util.List<String> snykOrgGroupNames = null;
        try {
            snykOrgGroupNames = SnykUiUtil.getSnykOrgGroupNames(snykUri, snykToken);
        } catch (Exception e) {
            log.error("Error while getting Snyk org and group names", e);
            e.printStackTrace();
        }
        if(snykToken != null) {
            for (String snykOrgGroupName : snykOrgGroupNames) {
                orgNameComboBox.addItem(snykOrgGroupName);
            }
        }


//        java.util.List<String> llmComboItems = BasicUiUtil.getOrgNames();
//        for (String comboItem : llmComboItems) {
//            orgNameComboBox.addItem(comboItem);
//        }
    }

}
