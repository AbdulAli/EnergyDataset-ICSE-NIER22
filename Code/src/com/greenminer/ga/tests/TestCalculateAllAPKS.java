package com.greenminer.ga.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.greenminer.ga.mathematics.ApkTransformationEnergy;
import com.greenminer.ga.mathematics.Result;
import com.greenminer.ga.mathematics.ResultStash;
import com.opencsv.CSVReader;

public class TestCalculateAllAPKS {

	public static HashMap<String, ResultStash> energyStashMap = new HashMap<>(); // First
																					// argument
																					// version,
																					// Second
																					// argument
																					// ResultStash.java

	public static TreeMap<String, Result> energyMap = new TreeMap<>(); // First
																		// argument
																		// version,
																		// Second
																		// argument
																		// Result.java

	public static ArrayList<ApkTransformationEnergy> apkEnergyList = new ArrayList<>();

	public static void main(String[] args) {

		StringBuffer output = new StringBuffer("");
		File source = new File("/home/abdulali/Desktop/CMPUT680/Project/ThreeTransformationResults/");

		File[] listOfFiles = source.listFiles(); // have all .csv files

		for (File file : listOfFiles) {
			String csvFile = file.getAbsolutePath();

			CSVReader reader = null;
			try {
				reader = new CSVReader(new FileReader(csvFile));
				String[] line;
				System.out.println(csvFile);
				if ((line = reader.readNext()) != null) {
					//reader.readNext();
				}
				while ((line = reader.readNext()) != null) {
					// System.out.println("Test [vesion= " + line[1] + ",
					// joules= " + line[7] + " , duration=" + line[10] + "]");

					insertInMap(line[0]+"."+line[1], line[7], line[10]);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		calculateEnergy();

		printEnergyValues();

		splitTreeMapForSeperateApks();

		caculateApkListPerformance();

	}

	private static void caculateApkListPerformance() {

		double result;

		for (ApkTransformationEnergy apk : apkEnergyList) {

			double minimum = apk.inline < apk.aggregate ? apk.inline : apk.aggregate;
			minimum = minimum < apk.singleInterface ? minimum : apk.singleInterface;
			minimum = minimum < apk.string ? minimum : apk.string;

			result = ((apk.original - minimum) / apk.original) * 100;

			System.out.println("\n====================\nPerformance Improvement\n" + apk.version + ": " + result);
		}

	}

	private static void splitTreeMapForSeperateApks() {

		int count = 0;

		ApkTransformationEnergy apk = new ApkTransformationEnergy();
		Iterator it = energyMap.entrySet().iterator();
		while (it.hasNext()) {
			count++;

			if (count == 1)
				apk = new ApkTransformationEnergy();

			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println("Version: " + pair.getKey());

			if (((String) pair.getKey()).contains("aggregate")) {
				apk.aggregate = ((Result) pair.getValue()).joules;
			} else if (((String) pair.getKey()).contains("inline")) {
				apk.inline = ((Result) pair.getValue()).joules;
			} else if (((String) pair.getKey()).contains("interface")) {
				apk.singleInterface = ((Result) pair.getValue()).joules;
			} else if (((String) pair.getKey()).contains("string")) {
				apk.string = ((Result) pair.getValue()).joules;
			} else {
				apk.original = ((Result) pair.getValue()).joules;
				apk.version = (String) pair.getKey();
			}

			// System.out.println("Joules: " + ((Result)pair.getValue()).joules
			// + "\tDuration: "+ ((Result)pair.getValue()).duration);
			it.remove(); // avoids a ConcurrentModificationException

			if (count == 5) {
				count = 0;
				apkEnergyList.add(apk);
			}

		}
	}

	@SuppressWarnings("rawtypes")
	private static void printEnergyValues() {

		Iterator it = energyMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println("Version: " + pair.getKey());
			System.out.println("Joules: " + ((Result) pair.getValue()).joules + "\tDuration: "
					+ ((Result) pair.getValue()).duration);

			// it.remove(); // avoids a ConcurrentModificationException
		}
	}

	@SuppressWarnings("rawtypes")
	private static void calculateEnergy() {
		Iterator it = energyStashMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println("Version: " + pair.getKey());

			double joules = calculateMedian(((ResultStash) pair.getValue()).joules);
			double duration = calculateMedian(((ResultStash) pair.getValue()).duration);

			energyMap.put((String) pair.getKey(), new Result(joules, duration));

			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public static double calculateMedian(ArrayList<Double> doubleArray) {
		double[] numArray = new double[doubleArray.size()];

		for (int i = 0; i < doubleArray.size(); i++) {
			numArray[i] = doubleArray.get(i).doubleValue();
		}

		Arrays.sort(numArray);
		double median;
		if (numArray.length % 2 == 0)
			median = ((double) numArray[numArray.length / 2] + (double) numArray[numArray.length / 2 - 1]) / 2;
		else
			median = (double) numArray[numArray.length / 2];

		return median;
	}

	private static void insertInMap(String version, String joules, String duration) {

		Double joulesValue = Double.parseDouble(joules);
		Double durationValue = Double.parseDouble(duration);

		if (energyStashMap.containsKey(version)) {

			energyStashMap.get(version).duration.add(durationValue);
			energyStashMap.get(version).joules.add(joulesValue);

		} else {

			ResultStash r = new ResultStash();
			r.duration.add(durationValue);
			r.joules.add(joulesValue);
			energyStashMap.put(version, r);

		}

	}

}
