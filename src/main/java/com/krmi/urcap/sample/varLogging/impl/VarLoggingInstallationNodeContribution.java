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

    private static final String FUNCTIONNAME_SENDVAR = "send_var";
    private static final String FUNCTIONNAME_CLEARVARS = "clear_vars";
    private static final String FUNCTIONNAME_WRITETOCSV = "write_to_csv";
    private static final String VARNAME = "var_name";
	private static final String VARVAL = "var_val";

    public VarLoggingInstallationNodeContribution(InstallationAPIProvider apiProvider, VarLoggingInstallationNodeView view, 
    DataModel model, VarLoggingDaemonService daemonService, CreationContext context) {
        this.api = apiProvider.getInstallationAPI();
        this.view = view;
        this.daemonService = daemonService;
        this.model = model;

        xmlRpcDaemonInterface = new XmlRpcVarLoggingDaemonInterface("127.0.0.1", PORT);
        startDaemon();

        addFunction(FUNCTIONNAME_CLEARVARS);
        addFunction(FUNCTIONNAME_SENDVAR, VARNAME, VARVAL);
        addFunction(FUNCTIONNAME_WRITETOCSV);

        
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
        
        // URScript code to call the function "send_var" with the arguments "var_name" and "var_val"
        writer.appendLine("def " + FUNCTIONNAME_SENDVAR + "(" + VARNAME + ", " + VARVAL + "):");
        writer.appendLine("local return_value = " + XMLRPC_VARIABLE + ".send_var(" + VARNAME + ", " + VARVAL + ")");
        writer.appendLine("return return_value");
        writer.appendLine("end");

        // URScript code to call the function "clear_vars"
        writer.appendLine("def " + FUNCTIONNAME_CLEARVARS + "():");
        writer.appendLine("local return_value = " + XMLRPC_VARIABLE + ".clear_vars()");
        writer.appendLine("return return_value");
        writer.appendLine("end");

        // URScript code to call the function "write_to_csv"
        writer.appendLine("def " + FUNCTIONNAME_WRITETOCSV + "():");
        writer.appendLine("local return_value = " + XMLRPC_VARIABLE + ".write_to_csv()");
        writer.appendLine("return return_value");
        writer.appendLine("end");
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

    private void addFunction(String name, String... argumentNames) {
		FunctionModel functionModel = api.getFunctionModel();
		if(functionModel.getFunction(name) == null) {
			try {
				functionModel.addFunction(name, argumentNames);
			} catch (FunctionException e) {
				// See e.getMessage() for explanation
			}
		}
	}
}

