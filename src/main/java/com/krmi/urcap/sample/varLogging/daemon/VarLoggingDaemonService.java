package com.krmi.urcap.sample.varLogging.daemon;

import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.DaemonService;

import java.net.MalformedURLException;
import java.net.URL;


public class VarLoggingDaemonService implements DaemonService {

	private DaemonContribution daemonContribution;

	public VarLoggingDaemonService() {
	}

	@Override
	public void init(DaemonContribution daemonContribution) {
		this.daemonContribution = daemonContribution;
		try {
			daemonContribution.installResource(new URL("file:daemon/"));
		} catch (MalformedURLException e) {
			}
	}

	@Override
	public URL getExecutable() {
		try {
			// Two equivalent example daemons are available:
			return new URL("file:daemon/var_logging_daemon.py"); // Python executable
//			return new URL("file:com/ur/urcap/examples/mydaemonswing/impl/daemon/HelloWorld"); // C++ executable
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public DaemonContribution getDaemon() {
		return daemonContribution;
	}

}
