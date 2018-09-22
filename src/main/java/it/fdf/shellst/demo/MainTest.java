package it.fdf.shellst.demo;

import java.io.File;

import it.fdf.shellst.shell.XShellFactory;
import it.fdf.shellst.shell.XShellResult;

public class MainTest {

	public static void main(String[] args) {
		XShellResult res = XShellFactory.createForCurrentOs().exec("dir");
		res.print();
    }
}
