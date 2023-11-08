package com.krmi.urcap.sample.varLogging.impl;

import com.krmi.urcap.sample.varLogging.daemon.VarLoggingDaemonService;
import com.krmi.urcap.sample.varLogging.daemon.XmlRpcVarLoggingDaemonInterface;
import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.InstallationAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.function.FunctionException;
import com.ur.urcap.api.domain.function.FunctionModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

public class VarLoggingInstallationNodeContribution implements InstallationNodeContribution {
    private final VarLoggingInstallationNodeView view;
    private InstallationAPI api;

    private DataModel model;

    private static final String XMLRPC_VARIABLE = "var_logging_daemon";
    private static final int PORT = 48010;
    private final VarLoggingDaemonService daemonService;
	private XmlRpcVarLoggingDaemonInterface xmlRpcDaemonInterface;
    private boolean pauseTimer = false;

    public VarLoggingInstallationNodeContribution(InstallationAPIProvider apiProvider, VarLoggingInstallationNodeView view, 
    DataModel model, VarLoggingDaemonService daemonService, CreationContext context) {
        this.api = apiProvider.getInstallationAPI();
        this.view = view;
        this.daemonService = daemonService;
        this.model = model;

        xmlRpcDaemonInterface = new XmlRpcVarLoggingDaemonInterface("127.0.0.1", PORT);
        startDaemon();

        
    }

    @Override
    public void openView() {
        // Called when the installation node view is opened
    }

    @Override
    public void closeView() {
        // Called when the installation node view is closed
    }

    @Override
    public void generateScript(ScriptWriter writer) {
        writer.assign(XMLRPC_VARIABLE, "rpc_factory(\"xmlrpc\", \"http://127.0.0.1:" + PORT + "/RPC2\")");
        
    }

    private void startDaemon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    pauseTimer = true;
                    awaitDaemonRunning(5000);
                    System.out.println("Daemon is running");
                }
                catch(Exception e) {
                    System.err.println("Error starting daemon: " + e.getMessage());
                }
                finally {
                    pauseTimer = false;
                }
            }
        }).start();
    }
    
    private void awaitDaemonRunning(long timeOutMilliSeconds) throws InterruptedException {
		daemonService.getDaemon().start();
		long endTime = System.nanoTime() + timeOutMilliSeconds * 1000L * 1000L;
		while(System.nanoTime() < endTime && (daemonService.getDaemon().getState() != DaemonContribution.State.RUNNING || !xmlRpcDaemonInterface.isReachable())) {
			Thread.sleep(100);
		}
	}

}

