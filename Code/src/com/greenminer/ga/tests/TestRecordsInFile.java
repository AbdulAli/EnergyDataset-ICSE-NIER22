package com.greenminer.ga.tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.greenminer.main.ProjectConfiguration;
import com.opencsv.CSVReader;

public class TestRecordsInFile {

	public static void main(String[] args) throws IOException {

		int numberOfRecords = 0;
		String csvFile = "/home/abdulali/Desktop/CMPUT680/Project/Result/abdul680_acrylicpaint/10111100110101111.csv";

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			if ((line = reader.readNext()) != null) {
				//reader.readNext();
			}
			while ((line = reader.readNext()) != null) {
				numberOfRecords++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(numberOfRecords);
	}
}
