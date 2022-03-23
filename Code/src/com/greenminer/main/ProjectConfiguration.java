package com.greenminer.main;

public class ProjectConfiguration {
	
	public static String projectName = "abdul680_agram";
	
	public static String testSuiteName = "abdul680_agram_logcat";
	
	public static String mainAPKName = "us.achromaticmetaphor.agram_19.apk";

	public static String batchName; // gets resolved in Population.calculateFitness(Chromosome); because every transformation has different batch

	public static String savedResultFile;
	
	public static double truthValue = 76.96350407919996;
	
	public static double maxFitness = 0;
	
	public static int populationSize = 10;
	
	public static int elitismValue = 2;
	
	public static int geneSize = 17;
	
	public static double minimumThresholdFitness = (double) 0.8; //To keep only those with minimum fitness in population
	
	public static int startingGeneration = 3; // Keep default value to 2

}