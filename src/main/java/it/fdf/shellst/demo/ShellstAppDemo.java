package it.fdf.shellst.demo;

import static spark.Spark.post;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.fdf.shellst.json.JsonCommand;
import it.fdf.shellst.json.JsonCommandResponse;
import it.fdf.shellst.json.JsonTransformer;
import it.fdf.shellst.shell.XShell;
import it.fdf.shellst.shell.XShellFactory;
import it.fdf.shellst.shell.XShellResult;
import spark.Spark;

public class ShellstAppDemo {
	
	private XShell _shell;

	public void init() {
		_shell = XShellFactory.createForCurrentOs();
		
		int port = 4567;
		String portStr = System.getProperty("port")!=null?System.getProperty("port"):System.getenv("port");
		if (portStr != null) port = Integer.parseInt(portStr);
		
		Spark.port(port);
		
		get_helo();
		post_shell_exec();
		post_shell_copy();
		
		Spark.get("/chunked", (req, res) -> {
			HttpServletResponse rawRes = res.raw();
			
			rawRes.setContentType("text/html");
			rawRes.setHeader("Transfer-Encoding", "chunked");
		

            int totalTime = 1;

            ServletOutputStream servOut = rawRes.getOutputStream();
            ChunkedOutputStream outputStream = new ChunkedOutputStream(servOut);
            
            //String join = String.join("", Collections.nCopies(1000, "1"));
            String join = "ciao porco";
            
            
            int count = 20;
			while (true) {
				Thread.sleep(2000);
				StringBuilder buffer = new StringBuilder();
				buffer.append("CHUNK_"+count).append("\n");
				buffer.append(join).append("\n\n");
				outputStream.write(buffer.toString().getBytes());
				outputStream.flush();
				
				count--;
				if (count == 0) {
					outputStream.done();
					break;
				}
				
				//outputStream.flush();
				/*
                if (totalTime % 3000 == 0) {
                    byte[] bytes = new String("3333333333333333333333\n").getBytes("utf8");
                    outputStream.write(bytes, 0, bytes.length);
                    
                }

                if (totalTime % 5000 == 0) {
                	byte[] bytes = new String("5555555555555555555555\n").getBytes("utf8");
                    outputStream.write(bytes, 0, bytes.length);
                }

                Thread.sleep(1000);
                totalTime += 1000;
                break;
                */
            }
            return null;
            //return null;
			//return "";
		});
		
	}

	private void get_helo() {
		Spark.get("/", (req, res) -> "Sheelst is working!!!");
	}

	private void post_shell_copy() {
		post("/shell/copy", "application/json", (req, res) -> {
			
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
			
			String fullFilePath = req.queryParams("path");
			InputStream fileStream = req.raw().getPart("file").getInputStream();
			
			if (fullFilePath == null || fullFilePath.isEmpty()) {
				res.status(404);
				JsonCommandResponse response = new JsonCommandResponse();
				response.result = "FAILURE";
				response.message = "file path param not found";
				return response;
			}
			
			if (fileStream == null) {
				res.status(404);
				JsonCommandResponse response = new JsonCommandResponse();
				response.result = "FAILURE";
				response.message = "file stream param not found";
				return response;
			}
			
			

			File file = new File(fullFilePath);
			try {
				file.createNewFile();
			} catch (IOException e) {
				res.status(404);
				JsonCommandResponse response = new JsonCommandResponse();
				response.result = "FAILURE";
				response.message = e.getMessage();
				return response;
			}

			
			Path filePath = file.toPath();
			try {
				Files.copy(fileStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				res.status(404);
				JsonCommandResponse response = new JsonCommandResponse();
				response.result = "FAILURE";
				response.message = e.getMessage();
				return response;
			}
			
			res.status(200);
			JsonCommandResponse response = new JsonCommandResponse();
			response.result = "SUCCESS";
			response.message = "File copied at: " + file.getAbsolutePath();
			return response;
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
    			JsonCommandResponse response = new JsonCommandResponse();
    			response.result = "FAILURE";
    			response.message = "invalid command";
    			return response;
    		}
			
			XShellResult shellOutput = _shell.exec(command);
			if (shellOutput.isFailure()) {
				res.status(500);
    			JsonCommandResponse response = new JsonCommandResponse();
    			response.result = "FAILURE";
    			response.message = shellOutput.asString();
    			return response;
			} 
			
    		res.status(200);
    		JsonCommandResponse jsonOutput = new JsonCommandResponse();
			jsonOutput.result = "command execution success";
			return jsonOutput;
    	}, new JsonTransformer());
	}

}
