package it.fdf.shellst.shell;

public class XShellFactory {
	
	public static XShell createForCurrentOs() {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		String shell;
		String cmdFlag;
		if (isWindows) {
			shell = "cmd.exe";
			cmdFlag = "/c";
		}
		else {
			shell = "sh";
			cmdFlag = "-c";			
		}
		return new XShell(shell, cmdFlag);
	}
	
}
