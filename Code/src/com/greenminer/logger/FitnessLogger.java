package com.greenminer.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FitnessLogger {

	private static FitnessLogger single_instance = null;

	File source = new File("Fitness");

	FileWriter fw;

	private FitnessLogger() {
		System.out.println("Fitness logger initialized");
	}

	private void startLogger() {
		File source = new File("Fitness");
		try {
			source.createNewFile();
			fw = new FileWriter(source, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static FitnessLogger getInstance() {
		if (single_instance == null)
			single_instance = new FitnessLogger();

		return single_instance;
	}

	public void writeLog(String chromosome, String fitness) {
		startLogger();
		String line = chromosome +","+fitness;
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
