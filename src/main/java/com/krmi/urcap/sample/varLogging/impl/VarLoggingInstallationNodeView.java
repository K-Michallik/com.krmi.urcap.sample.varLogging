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
        
    }




}
