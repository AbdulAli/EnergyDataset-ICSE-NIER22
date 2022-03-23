package com.greenminer.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static Logger single_instance = null;

	File source = new File("Log");

	FileWriter fw;

	private Logger() {
		System.out.println("Logger initialized");
	}

	private void startLogger() {
		File source = new File("Log");
		try {
			source.createNewFile();
			fw = new FileWriter(source, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Logger getInstance() {
		if (single_instance == null)
			single_instance = new Logger();

		return single_instance;
	}

	public void writeLog(String line) {
		startLogger();
		try {
			System.out.println(line);
			fw.append(line);
			fw.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		stopLogger();
	}

	private void stopLogger(){
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
