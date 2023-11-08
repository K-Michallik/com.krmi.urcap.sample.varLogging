package com.krmi.urcap.sample.varLogging.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.krmi.urcap.sample.varLogging.daemon.VarLoggingDaemonService;
import com.ur.urcap.api.contribution.DaemonService;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;



/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext context) throws Exception {
		VarLoggingDaemonService daemonService = new VarLoggingDaemonService();
		VarLoggingInstallationNodeService installationNodeService = new VarLoggingInstallationNodeService(daemonService);

		context.registerService(SwingInstallationNodeService.class, installationNodeService, null);
		context.registerService(DaemonService.class, daemonService, null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
	}
}

