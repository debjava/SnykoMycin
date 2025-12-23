package com.ddlab.rnd.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.ddlab.rnd.common.util.Constants;
import com.ddlab.rnd.ui.util.BasicUiUtil;
import com.ddlab.rnd.ui.util.CommonUIUtil;
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
public class AiDetailsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField clientIdTxt;
    private JTextField clientSecretTxt;
    private JTextField oauthEndPointTxt;
    private JTextField llmApiEndPointTxt;
    private JTextField textField;
    private JComboBox<String> llmModelcomboBox;

    public AiDetailsPanel() {
        setBorder(new TitledBorder(null, "AI Details", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        createLayout();

        createClientIdLabel();

        createClientIdText();

        createClientSecretLabel();

        createClientSecretText();

        createOAuthEndPointLabel();

        createOAuthEndPointText();

        createLLMAPiEndPointLabel();

        createLLMApiEndPointText();

        createLLMModelLabel();

        createLLMComboBox();

        createLLMGetButton();

    }

    private void createLayout() {
        GridBagLayout gbl_aiDetailsPanel = new GridBagLayout();
        gbl_aiDetailsPanel.columnWidths = new int[]{0, 0, 0};
        gbl_aiDetailsPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gbl_aiDetailsPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_aiDetailsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gbl_aiDetailsPanel);
    }

    private void createClientIdLabel() {
        JLabel clienIdLbl = new JLabel("*Client Id:");
        GridBagConstraints gbc_clienIdLbl = new GridBagConstraints();
        gbc_clienIdLbl.insets = new Insets(0, 0, 5, 5);
        gbc_clienIdLbl.anchor = GridBagConstraints.EAST;
        gbc_clienIdLbl.gridx = 0;
        gbc_clienIdLbl.gridy = 0;
        add(clienIdLbl, gbc_clienIdLbl);
    }

    private void createClientIdText() {
        clientIdTxt = new JTextField();
        GridBagConstraints gbc_clientIdTxt = new GridBagConstraints();
        gbc_clientIdTxt.insets = new Insets(0, 0, 5, 5);
        gbc_clientIdTxt.fill = GridBagConstraints.HORIZONTAL;
        gbc_clientIdTxt.gridx = 1;
        gbc_clientIdTxt.gridy = 0;
        add(clientIdTxt, gbc_clientIdTxt);
        clientIdTxt.setColumns(10);
    }

    private void createClientSecretLabel() {
        JLabel clientSecretLbl = new JLabel("*Client Secret:");
        GridBagConstraints gbc_clientSecretLbl = new GridBagConstraints();
        gbc_clientSecretLbl.anchor = GridBagConstraints.EAST;
        gbc_clientSecretLbl.insets = new Insets(0, 0, 5, 5);
        gbc_clientSecretLbl.gridx = 0;
        gbc_clientSecretLbl.gridy = 1;
        add(clientSecretLbl, gbc_clientSecretLbl);
    }

    private void createClientSecretText() {
        clientSecretTxt = new JTextField();
        GridBagConstraints gbc_clientSecretTxt = new GridBagConstraints();
        gbc_clientSecretTxt.insets = new Insets(0, 0, 5, 5);
        gbc_clientSecretTxt.fill = GridBagConstraints.HORIZONTAL;
        gbc_clientSecretTxt.gridx = 1;
        gbc_clientSecretTxt.gridy = 1;
        add(clientSecretTxt, gbc_clientSecretTxt);
        clientSecretTxt.setColumns(10);
    }

    private void createOAuthEndPointLabel() {
        JLabel oauthLbl = new JLabel("*OAuth End Point:");
        GridBagConstraints gbc_oauthLbl = new GridBagConstraints();
        gbc_oauthLbl.anchor = GridBagConstraints.EAST;
        gbc_oauthLbl.insets = new Insets(0, 0, 5, 5);
        gbc_oauthLbl.gridx = 0;
        gbc_oauthLbl.gridy = 2;
        add(oauthLbl, gbc_oauthLbl);
    }

    private void createOAuthEndPointText() {

        oauthEndPointTxt = new JTextField();
        GridBagConstraints gbc_oauthEndPointTxt = new GridBagConstraints();
        gbc_oauthEndPointTxt.insets = new Insets(0, 0, 5, 5);
        gbc_oauthEndPointTxt.fill = GridBagConstraints.HORIZONTAL;
        gbc_oauthEndPointTxt.gridx = 1;
        gbc_oauthEndPointTxt.gridy = 2;
        add(oauthEndPointTxt, gbc_oauthEndPointTxt);
        oauthEndPointTxt.setColumns(10);
    }

    private void createLLMAPiEndPointLabel() {
        JLabel llmApiEndPointLbl = new JLabel("*LLM Api EndPoint:");
        GridBagConstraints gbc_llmApiEndPointLbl = new GridBagConstraints();
        gbc_llmApiEndPointLbl.anchor = GridBagConstraints.EAST;
        gbc_llmApiEndPointLbl.insets = new Insets(0, 0, 5, 5);
        gbc_llmApiEndPointLbl.gridx = 0;
        gbc_llmApiEndPointLbl.gridy = 3;
        add(llmApiEndPointLbl, gbc_llmApiEndPointLbl);
    }

    private void createLLMApiEndPointText() {
        llmApiEndPointTxt = new JTextField();
        GridBagConstraints gbc_llmApiEndPointTxt = new GridBagConstraints();
        gbc_llmApiEndPointTxt.gridwidth = 2;
        gbc_llmApiEndPointTxt.insets = new Insets(0, 0, 5, 5);
        gbc_llmApiEndPointTxt.fill = GridBagConstraints.HORIZONTAL;
        gbc_llmApiEndPointTxt.gridx = 1;
        gbc_llmApiEndPointTxt.gridy = 3;
        add(llmApiEndPointTxt, gbc_llmApiEndPointTxt);
        llmApiEndPointTxt.setColumns(10);
    }


    private void createLLMModelLabel() {
        JLabel llmModelLbl = new JLabel("LLM Models:");
        GridBagConstraints gbc_llmModelLbl = new GridBagConstraints();
        gbc_llmModelLbl.anchor = GridBagConstraints.EAST;
        gbc_llmModelLbl.insets = new Insets(0, 0, 5, 5);
        gbc_llmModelLbl.gridx = 0;
        gbc_llmModelLbl.gridy = 4;
        add(llmModelLbl, gbc_llmModelLbl);
    }

    private void createLLMComboBox() {
        llmModelcomboBox = new JComboBox<String>();
        GridBagConstraints gbc_llmModelcomboBox = new GridBagConstraints();
        gbc_llmModelcomboBox.insets = new Insets(0, 0, 5, 5);
        gbc_llmModelcomboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_llmModelcomboBox.gridx = 1;
        gbc_llmModelcomboBox.gridy = 4;
        add(llmModelcomboBox, gbc_llmModelcomboBox);
    }

    private void createLLMGetButton() {
        JButton llmModelGetBtn = new JButton("Get Models");
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.gridwidth = 2;
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
        gbc_btnNewButton.anchor = GridBagConstraints.EAST;
        gbc_btnNewButton.gridx = 3;
        gbc_btnNewButton.gridy = 4;
        add(llmModelGetBtn, gbc_btnNewButton);

        llmModelGetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateAndPopulateLLMModels();
            }
        });
    }

    private void validateAndPopulateLLMModels() {
        validateInputs();
        llmModelcomboBox.removeAllItems();
        populateLLMModelsProgressively();
    }

    private void populateLLMModelsProgressively() throws RuntimeException {
        ProgressManager.getInstance().run(new Task.Modal(null, "Fetching LLM Models ...", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                try {
                    indicator.setIndeterminate(true);
                    indicator.setText("Please wait, fetching LLM Models ...");
                    populateLLMModels();
                } catch (Exception ex) {
                    CommonUIUtil.showAppErrorMessage(ex.getMessage());
                }
            }

        });
    }

    private void populateLLMModels() throws RuntimeException {
        String clientId = clientIdTxt.getText();
        String clientSecret = clientSecretTxt.getText();
        String oauthEndPointUri = oauthEndPointTxt.getText();
        String aiApiEndPointUri = llmApiEndPointTxt.getText();
        java.util.List<String> llmComboItems = new ArrayList<>();
        try {
            llmComboItems = BasicUiUtil.getActualLLMModels(clientId, clientSecret, oauthEndPointUri, aiApiEndPointUri);
            for (String comboItem : llmComboItems) {
                llmModelcomboBox.addItem(comboItem);
            }
        } catch (RuntimeException ex) {
            log.error("Exception while populating LLM models: ", ex);
            throw new RuntimeException("Unable to fetch LLM models: " + ex.getMessage());
        }

    }

    private void validateInputs() {
        String clientId = clientIdTxt.getText();
        String clientSecret = clientSecretTxt.getText();
        String oauthEndPointUri = oauthEndPointTxt.getText();
        String aiApiEndPointUri = llmApiEndPointTxt.getText();
        if (clientId == null || clientId.isEmpty()) {
            Messages.showErrorDialog("Client Id cannot be empty", Constants.PROD_TITLE);
            throw new IllegalArgumentException("ClientId cannot be empty!");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            Messages.showErrorDialog("Client Secret cannot be empty", Constants.PROD_TITLE);
            throw new IllegalArgumentException("ClientSecret cannot be empty!");
        }
        if (oauthEndPointUri == null || oauthEndPointUri.isEmpty()) {
            Messages.showErrorDialog("OAuth End Point cannot be empty", Constants.PROD_TITLE);
            throw new IllegalArgumentException("OAuth End Point cannot be empty!");
        }
        if (aiApiEndPointUri == null || aiApiEndPointUri.isEmpty()) {
            Messages.showErrorDialog("LLM Api End Point cannot be empty", Constants.PROD_TITLE);
            throw new IllegalArgumentException("LLM Api End Point cannot be empty!");
        }

    }

}
