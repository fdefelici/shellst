package it.fdf.shellst.config;

public class XPropsFactory {

	public static XProps createFromSystem() {
		XProps props = new XProps();
	
		props.port = argOrEnvOrDefault("port", 4567);
		props.token = argOrEnvOrDefault("token", "");
		
		return props;
	}
	
	private static int argOrEnvOrDefault(String name, int defaultValue) {
		String value = argOrEnvOrDefault(name, Integer.toString(defaultValue));
		return Integer.parseInt(value);
	}
	
	private static String argOrEnvOrDefault(String name, String defaultValue) {
		String arg = System.getProperty(name);
		if (arg != null) return arg; 
		
		String env = System.getenv(name);
		if (env != null) return env;
		
		return defaultValue;
	}	
	
	
}
