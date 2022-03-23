package com.greenminer.ga.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TestManualFitnessFile {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		boolean energyCalculationFailed = false;
		StringBuffer output = new StringBuffer("");
		File source = new File("ManualFitness"); 
		try (BufferedReader br = new BufferedReader(new FileReader(source))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	energyCalculationFailed = Boolean.parseBoolean(line.split("=")[1]);    	
		    }
		}
		System.out.println(energyCalculationFailed);

		FileWriter fw = new FileWriter(source, false);
		fw.write("The energy calculation failed =false");
		fw.close();
	}

}
