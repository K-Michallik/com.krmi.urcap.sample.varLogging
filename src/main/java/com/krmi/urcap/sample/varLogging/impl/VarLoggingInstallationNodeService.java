package com.krmi.urcap.sample.varLogging.impl;

import java.util.Locale;

import com.krmi.urcap.sample.varLogging.daemon.VarLoggingDaemonService;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class VarLoggingInstallationNodeService implements SwingInstallationNodeService<VarLoggingInstallationNodeContribution, VarLoggingInstallationNodeView>{

    private final VarLoggingDaemonService daemonService;

    public VarLoggingInstallationNodeService(VarLoggingDaemonService daemonService) {
        this.daemonService = daemonService;
    }

    @Override
    public void configureContribution(ContributionConfiguration configuration) {
    }

    @Override
    public String getTitle(Locale locale) {
        // TODO Auto-generated method stub
        return "Variable Logging";
    }

    @Override
    public VarLoggingInstallationNodeView createView(ViewAPIProvider apiProvider) {
        Style style = new V5Style();
        return new VarLoggingInstallationNodeView(style);
    }

    @Override
    public VarLoggingInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider,
            VarLoggingInstallationNodeView view, DataModel model, CreationContext context) {
        // TODO Auto-generated method stub
        return new VarLoggingInstallationNodeContribution(apiProvider, view, model, daemonService, context);
    }
    
}
