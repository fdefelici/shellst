package it.fdf.shellst.app;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.before;
import static spark.Spark.halt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.servlet.MultipartConfigElement;

import com.google.gson.Gson;

import it.fdf.shellst.config.XProps;
import it.fdf.shellst.config.XPropsFactory;
import it.fdf.shellst.json.JsonCommand;
import it.fdf.shellst.json.JsonCommandResponse;
import it.fdf.shellst.json.JsonTransformer;
import it.fdf.shellst.shell.XShell;
import it.fdf.shellst.shell.XShellFactory;
import it.fdf.shellst.shell.XShellResult;
import spark.Spark;

public class ShellstApp {
	
	private XShell _shell;
	private XProps _props;

	public void start() {
		_shell = XShellFactory.createForCurrentOs();
		_props = XPropsFactory.createFromSystem();
		
		setup_port();
		setup_token();
		
		get_helo();
		post_shell_exec();
		post_shell_copy();
	}

	private void setup_token() {
		if (_props.token.isEmpty()) return;
		before("/shell/*", (req, res) -> {
			String token = req.headers("TOKEN");
			if (!_props.token.equals(token)) halt(401, "Missing or Wrong authorization token!!!");
		});
	}

	private void setup_port() {
		Spark.port(_props.port);
	}
	
	private void get_helo() {
		get("/", (req, res) -> "Sheelst is working!!!");
	}

	private void post_shell_copy() {
		post("/shell/copy", "application/json", (req, res) -> {
			
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
			
			String fullFilePath = req.queryParams("path");
			InputStream fileStream = req.raw().getPart("file").getInputStream();
			
			if (fullFilePath == null || fullFilePath.isEmpty()) {
				res.status(404);
				return JsonCommandResponse.FAILURE("file path param not found");
			}
			
			if (fileStream == null) {
				res.status(404);
				return JsonCommandResponse.FAILURE("file stream param not found");
			}
			
			File file = new File(fullFilePath);
			try {
				file.createNewFile();
			} catch (IOException e) {
				res.status(404);
				return JsonCommandResponse.FAILURE(e.getMessage());
			}

			Path filePath = file.toPath();
			try {
				Files.copy(fileStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				res.status(404);
				return JsonCommandResponse.FAILURE(e.getMessage());
			}
			
			res.status(200);
			return JsonCommandResponse.SUCCESS("File copied at: " + file.getAbsolutePath());
    	}, new JsonTransformer());
	}

	private void post_shell_exec() {
		post("/shell/exec", "application/json", (req, res) -> {
			String command;

			if (req.queryParams().contains("cmd")) {  //passed by query string
				command = req.queryParams("cmd");
				
			} else { //passed as Json	
				Gson gson = new Gson();
				JsonCommand jsonInput = gson.fromJson(req.body(), JsonCommand.class);
				command = jsonInput.cmd;
			}
			
    		if (command == null || command.isEmpty()) {
    			res.status(404);
    			return JsonCommandResponse.FAILURE("invalid command");
    		}
			
			XShellResult shellOutput = _shell.exec(command);
			if (shellOutput.isFailure()) {
				res.status(500);
    			return JsonCommandResponse.FAILURE(shellOutput.asString());
			} 
			
    		res.status(200);
    		return JsonCommandResponse.SUCCESS(shellOutput.asString());
    	}, new JsonTransformer());
	}

}
