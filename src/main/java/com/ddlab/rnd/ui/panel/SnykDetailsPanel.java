package com.ddlab.rnd.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.ui.util.CommonUIUtil;
import com.ddlab.rnd.ui.util.SnykUiUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class SnykDetailsPanel extends JPanel {
    private JTextField snykUriTxt;
    private JTextField snykTokentxt;
    private JComboBox<String> orgNameComboBox;
    private Map<String, String> snykOrgNameIdMap = new HashMap<>();

    public SnykDetailsPanel() {
        setBorder(new TitledBorder(null, "Snyk Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        createPanelLayout();

        createSnykUriLabel();

        createSnykUriText();

        createSnykTokenLabel();

        createSnykTokenTxt();

        createOrgNameLbl();

        createOrgNameComboBox();

        createSnykOrgButton();
    }

    // ~~~~~~~~~~~~~~~~~~~~ UI Specific private methods ~~~~~~~~~~~~~~~~~~~~

    private void createPanelLayout() {
        GridBagLayout gbl_snykPanel = new GridBagLayout();
        gbl_snykPanel.columnWidths = new int[]{0, 0, 0};
        gbl_snykPanel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_snykPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_snykPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gbl_snykPanel);
    }

    private void createSnykUriLabel() {
        JLabel snykUriLbl = new JLabel("*Snyk Endpoint URI:");
        GridBagConstraints gbc_snykUriLbl = new GridBagConstraints();
        gbc_snykUriLbl.insets = new Insets(0, 0, 5, 5);
        gbc_snykUriLbl.anchor = GridBagConstraints.EAST;
        gbc_snykUriLbl.gridx = 0;
        gbc_snykUriLbl.gridy = 0;
        add(snykUriLbl, gbc_snykUriLbl);
    }


    private void createSnykUriText() {
        snykUriTxt = new JTextField();
        snykUriTxt.setText("https://snyk.io/api/v1");
        GridBagConstraints gbc_snykUriTxt = new GridBagConstraints();
        gbc_snykUriTxt.insets = new Insets(0, 0, 5, 5);
        gbc_snykUriTxt.fill = GridBagConstraints.HORIZONTAL;
        gbc_snykUriTxt.gridx = 1;
        gbc_snykUriTxt.gridy = 0;
        add(snykUriTxt, gbc_snykUriTxt);
        snykUriTxt.setColumns(10);
         // default snyk uri
    }

    private void createSnykTokenLabel() {
        JLabel snykTokenLbl = new JLabel("*Snyk Token:");
        GridBagConstraints gbc_snykTokenLbl = new GridBagConstraints();
        gbc_snykTokenLbl.anchor = GridBagConstraints.EAST;
        gbc_snykTokenLbl.insets = new Insets(0, 0, 5, 5);
        gbc_snykTokenLbl.gridx = 0;
        gbc_snykTokenLbl.gridy = 1;
        add(snykTokenLbl, gbc_snykTokenLbl);
    }

    private void createSnykTokenTxt() {
        snykTokentxt = new JTextField();
        GridBagConstraints gbc_snykTokentxt = new GridBagConstraints();
        gbc_snykTokentxt.insets = new Insets(0, 0, 5, 5);
        gbc_snykTokentxt.fill = GridBagConstraints.HORIZONTAL;
        gbc_snykTokentxt.gridx = 1;
        gbc_snykTokentxt.gridy = 1;
        add(snykTokentxt, gbc_snykTokentxt);
        snykTokentxt.setColumns(10);
    }

    private void createOrgNameLbl() {
        JLabel orgNameLbl = new JLabel("Org Name:");
        GridBagConstraints gbc_orgNameLbl = new GridBagConstraints();
        gbc_orgNameLbl.anchor = GridBagConstraints.EAST;
        gbc_orgNameLbl.insets = new Insets(0, 0, 0, 5);
        gbc_orgNameLbl.gridx = 0;
        gbc_orgNameLbl.gridy = 2;
        add(orgNameLbl, gbc_orgNameLbl);
    }

    private void createOrgNameComboBox() {
        orgNameComboBox = new JComboBox<String>();
        GridBagConstraints gbc_orgNameComboBox = new GridBagConstraints();
        gbc_orgNameComboBox.insets = new Insets(0, 0, 0, 5);
        gbc_orgNameComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_orgNameComboBox.gridx = 1;
        gbc_orgNameComboBox.gridy = 2;
        add(orgNameComboBox, gbc_orgNameComboBox);
    }

    private void createSnykOrgButton() {
        JButton snykOrgGetBtn = new JButton("Get Orgs");
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.gridx = 2;
        gbc_btnNewButton.gridy = 2;
        add(snykOrgGetBtn, gbc_btnNewButton);

        snykOrgGetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndPopulateOrgNames();
            }
        });
    }

    private void validateAndPopulateOrgNames() {
        validateInputs();
        orgNameComboBox.removeAllItems();
        populateSnykOrgNamesProgressively();
    }

    private void populateSnykOrgNamesProgressively() throws RuntimeException {
        ProgressManager.getInstance().run(new Task.Modal(null, Constants.PROD_TITLE, true) {
            @Override
            public void run(ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText("Please wait, fetching Snyk Org Names ...");
                    populateOrgNames();
                } catch (Exception ex) {
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
            }

        });
    }


    private void validateInputs() {
        String snykUri = snykUriTxt.getText();
        String snykToken = snykTokentxt.getText();
        if (snykUri == null || snykUri.isEmpty()) {
            Messages.showErrorDialog("Snyk URI cannot be empty", Constants.PROD_TITLE);
            throw new IllegalArgumentException("Snyk URI cannot be empty!");
        }
        if (snykToken == null || snykToken.isEmpty()) {
            Messages.showErrorDialog("Snyk Token cannot be empty", Constants.PROD_TITLE);
            throw new IllegalArgumentException("Snyk Token cannot be empty!");
        }
    }

    private void populateOrgNames() throws RuntimeException {
        String snykUri = snykUriTxt.getText();
        String snykToken = snykTokentxt.getText();
        java.util.List<String> snykOrgGroupNames = null;
        try {
            snykOrgGroupNames = SnykUiUtil.getSnykOrgGroupNames(snykUri, snykToken);
            // Manipulate snyk org and group names and store in Map with group name as key and id as value
            snykOrgGroupNames.stream().map(s -> s.split("~")).forEach(s -> snykOrgNameIdMap.put(s[1] + "~" + s[2], s[0]));
            snykOrgNameIdMap.forEach((k, v) -> {
                orgNameComboBox.addItem(k);
            });
        } catch (Exception e) {
            log.error("Error while getting Snyk org and group names", e);
            throw new RuntimeException("Unable to fetch Snyk org and group names: " + e.getMessage());
        }

    }

}
