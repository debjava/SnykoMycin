package com.ddlab.rnd.ui.panel;

import com.ddlab.rnd.ui.util.BasicUiUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

		GridBagLayout gbl_aiDetailsPanel = new GridBagLayout();
		gbl_aiDetailsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_aiDetailsPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_aiDetailsPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_aiDetailsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gbl_aiDetailsPanel);
		
		JLabel clienIdLbl = new JLabel("*Client Id:");
		GridBagConstraints gbc_clienIdLbl = new GridBagConstraints();
		gbc_clienIdLbl.insets = new Insets(0, 0, 5, 5);
		gbc_clienIdLbl.anchor = GridBagConstraints.EAST;
		gbc_clienIdLbl.gridx = 0;
		gbc_clienIdLbl.gridy = 0;
		add(clienIdLbl, gbc_clienIdLbl);
		
		clientIdTxt = new JTextField();
		GridBagConstraints gbc_clientIdTxt = new GridBagConstraints();
		gbc_clientIdTxt.insets = new Insets(0, 0, 5, 5);
		gbc_clientIdTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_clientIdTxt.gridx = 1;
		gbc_clientIdTxt.gridy = 0;
		add(clientIdTxt, gbc_clientIdTxt);
		clientIdTxt.setColumns(10);
		
		JLabel clientSecretLbl = new JLabel("*Client Secret:");
		GridBagConstraints gbc_clientSecretLbl = new GridBagConstraints();
		gbc_clientSecretLbl.anchor = GridBagConstraints.EAST;
		gbc_clientSecretLbl.insets = new Insets(0, 0, 5, 5);
		gbc_clientSecretLbl.gridx = 0;
		gbc_clientSecretLbl.gridy = 1;
		add(clientSecretLbl, gbc_clientSecretLbl);
		
		clientSecretTxt = new JTextField();
		GridBagConstraints gbc_clientSecretTxt = new GridBagConstraints();
		gbc_clientSecretTxt.insets = new Insets(0, 0, 5, 5);
		gbc_clientSecretTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_clientSecretTxt.gridx = 1;
		gbc_clientSecretTxt.gridy = 1;
		add(clientSecretTxt, gbc_clientSecretTxt);
		clientSecretTxt.setColumns(10);
		
		JLabel oauthLbl = new JLabel("*OAuth End Point:");
		GridBagConstraints gbc_oauthLbl = new GridBagConstraints();
		gbc_oauthLbl.anchor = GridBagConstraints.EAST;
		gbc_oauthLbl.insets = new Insets(0, 0, 5, 5);
		gbc_oauthLbl.gridx = 0;
		gbc_oauthLbl.gridy = 2;
		add(oauthLbl, gbc_oauthLbl);
		
		oauthEndPointTxt = new JTextField();
		GridBagConstraints gbc_oauthEndPointTxt = new GridBagConstraints();
		gbc_oauthEndPointTxt.insets = new Insets(0, 0, 5, 5);
		gbc_oauthEndPointTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_oauthEndPointTxt.gridx = 1;
		gbc_oauthEndPointTxt.gridy = 2;
		add(oauthEndPointTxt, gbc_oauthEndPointTxt);
		oauthEndPointTxt.setColumns(10);


        JLabel llmApiEndPointLbl = new JLabel("*LLM Api EndPoint:");
        GridBagConstraints gbc_llmApiEndPointLbl = new GridBagConstraints();
        gbc_llmApiEndPointLbl.anchor = GridBagConstraints.EAST;
        gbc_llmApiEndPointLbl.insets = new Insets(0, 0, 5, 5);
        gbc_llmApiEndPointLbl.gridx = 0;
        gbc_llmApiEndPointLbl.gridy = 3;
        add(llmApiEndPointLbl, gbc_llmApiEndPointLbl);


        llmApiEndPointTxt = new JTextField();
        GridBagConstraints gbc_llmApiEndPointTxt = new GridBagConstraints();
        gbc_llmApiEndPointTxt.gridwidth = 2;
        gbc_llmApiEndPointTxt.insets = new Insets(0, 0, 5, 5);
        gbc_llmApiEndPointTxt.fill = GridBagConstraints.HORIZONTAL;
        gbc_llmApiEndPointTxt.gridx = 1;
        gbc_llmApiEndPointTxt.gridy = 3;
        add(llmApiEndPointTxt, gbc_llmApiEndPointTxt);
        llmApiEndPointTxt.setColumns(10);



		
		JLabel llmModelLbl = new JLabel("LLM Models:");
		GridBagConstraints gbc_llmModelLbl = new GridBagConstraints();
		gbc_llmModelLbl.anchor = GridBagConstraints.EAST;
		gbc_llmModelLbl.insets = new Insets(0, 0, 5, 5);
		gbc_llmModelLbl.gridx = 0;
		gbc_llmModelLbl.gridy = 4;
		add(llmModelLbl, gbc_llmModelLbl);
		
		llmModelcomboBox = new JComboBox<String>();
		GridBagConstraints gbc_llmModelcomboBox = new GridBagConstraints();
		gbc_llmModelcomboBox.insets = new Insets(0, 0, 5, 5);
		gbc_llmModelcomboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_llmModelcomboBox.gridx = 1;
		gbc_llmModelcomboBox.gridy = 4;
		add(llmModelcomboBox, gbc_llmModelcomboBox);
		
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
                llmModelcomboBox.removeAllItems(); // clear existing items
//                populateLLMModels();
                populateLLMModelsWithProgress();
            }
        });
		
//		JLabel llmApiEndPointLbl = new JLabel("LLM Api EndPoint:");
//		GridBagConstraints gbc_llmApiEndPointLbl = new GridBagConstraints();
//		gbc_llmApiEndPointLbl.anchor = GridBagConstraints.EAST;
//		gbc_llmApiEndPointLbl.insets = new Insets(0, 0, 5, 5);
//		gbc_llmApiEndPointLbl.gridx = 0;
//		gbc_llmApiEndPointLbl.gridy = 4;
//		add(llmApiEndPointLbl, gbc_llmApiEndPointLbl);
		
//		llmApiEndPointTxt = new JTextField();
//		GridBagConstraints gbc_llmApiEndPointTxt = new GridBagConstraints();
//		gbc_llmApiEndPointTxt.gridwidth = 2;
//		gbc_llmApiEndPointTxt.insets = new Insets(0, 0, 5, 5);
//		gbc_llmApiEndPointTxt.fill = GridBagConstraints.HORIZONTAL;
//		gbc_llmApiEndPointTxt.gridx = 1;
//		gbc_llmApiEndPointTxt.gridy = 4;
//		add(llmApiEndPointTxt, gbc_llmApiEndPointTxt);
//		llmApiEndPointTxt.setColumns(10);
		
//		JButton testBtn = new JButton("Test");
//		GridBagConstraints gbc_testBtn = new GridBagConstraints();
//		gbc_testBtn.insets = new Insets(0, 0, 5, 0);
//		gbc_testBtn.gridx = 3;
//		gbc_testBtn.gridy = 4;
//		add(testBtn, gbc_testBtn);

	}

    private void populateLLMModelsWithProgress() {
        ProgressManager.getInstance().run(new Task.Modal(null, "Fetching LLM Models ...", true) {
            @Override
            public void run(ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Please wait, fetching LLM Models...");

                // Simulate long-running work
                populateLLMModels();
            }
        });
    }

    private void populateLLMModels() {
        String clientId = clientIdTxt.getText();
        String clientSecret = clientSecretTxt.getText();
        String oauthEndPointUri = oauthEndPointTxt.getText();
        String aiApiEndPointUri = llmApiEndPointTxt.getText();

//        java.util.List<String> llmComboItems = BasicUiUtil.getLLMModels(clientId, clientSecret);
        java.util.List<String> llmComboItems = BasicUiUtil.getActualLLMModels(clientId, clientSecret,oauthEndPointUri,aiApiEndPointUri);
        for (String comboItem : llmComboItems) {
            llmModelcomboBox.addItem(comboItem);
        }
    }

}
