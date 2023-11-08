package com.krmi.urcap.sample.varLogging.impl;


import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;

import javax.swing.*;

public class VarLoggingInstallationNodeView implements SwingInstallationNodeView<VarLoggingInstallationNodeContribution> {

    private final Style style;
    
    public VarLoggingInstallationNodeView(Style style) {
        this.style = style;
    }

    @Override
    public void buildUI(JPanel jPanel, VarLoggingInstallationNodeContribution contribution) {
        // Add your UI components to the jPanel here
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(createDescription());
    }


    private JTextArea createDescription() {
        JTextArea urcapInfoText = new JTextArea();
        urcapInfoText.setEditable(false);
        urcapInfoText.setWrapStyleWord(true);
        urcapInfoText.setLineWrap(true);
        urcapInfoText.setText("This URCap allows the user to record variables and then convert them into a CSV file.\r\n" +
            "The following functions are added to the program preamble for invocation during program execution:\r\n\r\n" +
            "1. clear_vars(): Clears all recorded variables in the daemon.\r\n" +
            "2. send_var(var_name, var_val): Sends the variable name and value to the daemon.\r\n" +
            "3. write_to_csv(): Writes the currently-recorded variables to a CSV and stores them in the programs folder. Clears all variable data in the daemon afterwards.");
        return urcapInfoText;
    }

}
