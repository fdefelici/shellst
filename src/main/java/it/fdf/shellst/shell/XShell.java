package it.fdf.shellst.shell;

import java.io.IOException;

public class XShell {
	

	private String _shell;
	private String _commandFlag;

	public XShell(String shell, String commandFlag) {
		_shell = shell;
		_commandFlag = commandFlag;
	}

	public XShellResult exec(String command) {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(_shell, _commandFlag, command);
		
		XShellResult result = new XShellResult();    
		    
		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
			result.setFailure();
			return result;
		}
		
		StreamGlobber streamGobbler = new StreamGlobber(process.getInputStream(), result);
		//Executors.newSingleThreadExecutor().submit(streamGobbler);
		new Thread(streamGobbler).run();
		
		int exitCode;
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			result.setFailure();
			return result;
			
		}
		//assert exitCode == 0;
		if (exitCode != 0) {
			result.setFailure();
			return result;
		}
		
		result.setSuccess();
		return result;
	}

	
	
}