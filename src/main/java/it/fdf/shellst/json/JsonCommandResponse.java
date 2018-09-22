package it.fdf.shellst.json;

public class JsonCommandResponse {
	
	public static JsonCommandResponse SUCCESS(String message) {
		JsonCommandResponse r = new JsonCommandResponse();
		r.result = "SUCCESS";
		r.message = message;
		return r;
	}
	
	public static JsonCommandResponse FAILURE(String message) {
		JsonCommandResponse r = new JsonCommandResponse();
		r.result = "FAILURE";
		r.message = message;
		return r;
	}
	
	public String result;
	public String message;
}
