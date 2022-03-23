package com.greenminer.ga;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.greenminer.logger.FitnessLogger;
import com.greenminer.logger.Logger;
import com.greenminer.main.ProjectConfiguration;
import com.greenminer.transformation.ConfigFileUtility;
import com.greenminer.transformation.TransformationUtility;
import com.opencsv.CSVReader;

public class Population {
	

	ArrayList<Chromosome> chromosomes = new ArrayList<>(0);
	int populationSize;
	int geneSize;
	
	public Population(int populationSize, int geneSize, boolean generatePopulation, boolean loadPopulation) {

		this.populationSize = populationSize;
		this.geneSize = geneSize;

		 if(loadPopulation){
				boolean loaded = loadFitnessFile();
		}
		
		if (generatePopulation) {
			
			boolean genes[] = new boolean[geneSize];
			
			for (int i = chromosomes.size(); i < geneSize; i++) {
				genes = new boolean[geneSize];
				genes[i] = true;
				chromosomes.add(new Chromosome(genes));
			}
		} 

	}
	
	private Chromosome randomChromosome() {
		Chromosome c = new Chromosome(geneSize);
		int lengthOfChromosome = c.getGenes().length;
		boolean[] genes = new boolean[lengthOfChromosome];
		do {
			c = new Chromosome(geneSize);
			for (int i = 0; i < lengthOfChromosome; i++) {
				Random rand = new Random();
				int value = rand.nextInt(2);
				genes[i] = value == 0 ? false : true;
			}
			c.setGenes(genes);
		} while (isDuplicateChromosome(c.getGenes()) && BitMapping.fitnessAlreadyCalculated(c.getGenes()));

		return c;
	}

	public void mutatePopulation() {
		
		Logger.getInstance().writeLog("================ Mutating Population ===============");
		int probability = chromosomes.get(0).getGenes().length;
		boolean iterate = true;
		
		for (Chromosome c : chromosomes) {
			
			if(c.isTopper())
				continue;
			
			iterate = true;
			while (iterate) {
				if (c.isTopper())
					continue;

				boolean[] genes = c.getGenes();
				boolean[] mutatedGenes = new boolean[genes.length];
				Random rand = new Random();

				for (int i = 0; i < probability; i++) {
					if (rand.nextInt(probability) == 0) {
						mutatedGenes[i] = genes[i] == true ? false : true;
					}
				}

				if (!isDuplicateChromosome(mutatedGenes) && !BitMapping.fitnessAlreadyCalculated(mutatedGenes)) {
					c.setGenes(mutatedGenes);
					iterate = false;
				}
			}
		}
		Logger.getInstance().writeLog("================ Mutation ended ===============");
	}
	
	public void uniformCrossoverPopulation(Chromosome A, Chromosome B) {
		
		Logger.getInstance().writeLog("\nUniform Crossover between two chromosomes............");
		Logger.getInstance().writeLog("First chromsome: "+ BitMapping.calculateChromoseBinaryName(A.getGenes()));
		Logger.getInstance().writeLog("Second chromsome: "+ BitMapping.calculateChromoseBinaryName(B.getGenes()));
		
		int lengthOfChromosome = A.getGenes().length;

		int sliceChromosome = lengthOfChromosome / 2;

		Chromosome newA = new Chromosome(A.getGenes().length);
				A.copy();
		Chromosome newB = new Chromosome(A.getGenes().length);
				B.copy();
				
		// First half of A getting traits of B
		for (int i = sliceChromosome; i < lengthOfChromosome; i++) {
			newA.getGenes()[i] = B.getGenes()[i];
		}

		// Second half of B getting traits of A
		for (int i = sliceChromosome; i < lengthOfChromosome; i++) {
			newB.getGenes()[i] = A.getGenes()[i];
		}
		
		if (A.isTopper() && B.isTopper()) {
			return;
		} else if (A.isTopper() && !B.isTopper()) {
			if (!isDuplicateChromosome(newB.getGenes()) && !BitMapping.fitnessAlreadyCalculated(newB.getGenes())) {
				removeChromosome(B);
				newB.setTopper(false);
				addChromosome(newB);
			}
		} else if (!A.isTopper() && B.isTopper()) {
			if (!isDuplicateChromosome(newA.getGenes()) && !BitMapping.fitnessAlreadyCalculated(newA.getGenes())) {
				removeChromosome(A);
				newA.setTopper(false);
				addChromosome(newA);
			}
		} else {
			if (!isDuplicateChromosome(newA.getGenes()) && !BitMapping.fitnessAlreadyCalculated(newA.getGenes())) {
				removeChromosome(A);
				addChromosome(newA);
			}

			if (!isDuplicateChromosome(newB.getGenes()) && !BitMapping.fitnessAlreadyCalculated(newB.getGenes())) {
				removeChromosome(B);
				addChromosome(newB);
			}
		}
		
		Logger.getInstance().writeLog("\nCorsssover Details:-\n");
	    Logger.getInstance().writeLog("A: "+A.getGenes() + " top:" + A.isTopper());
	    Logger.getInstance().writeLog("B: "+B.getGenes() + " top:" + B.isTopper());
	    Logger.getInstance().writeLog("newA: "+newA.getGenes() + " top:" + newA.isTopper());
	    Logger.getInstance().writeLog("newB: "+newB.getGenes() + " top:" + newB.isTopper());
	    Logger.getInstance().writeLog("Corsssover Ended\n================================\n");
	}
	
	public void crossoverPopulation() {

		int count = 0;
		int requiredPopulationSize = ProjectConfiguration.populationSize;
		int actualPopulationSize = this.chromosomes.size();

		while (chromosomes.size()>3 && (actualPopulationSize < requiredPopulationSize)  && count<requiredPopulationSize) {
			Chromosome A = null;
			Chromosome B = null;
			
			while(A == null){
				A = tournamentSelection();
			}
			
			while(B == null){
				B = tournamentSelection();
			}

			Logger.getInstance().writeLog("\nUniform Crossover between two chromosomes............");
			Logger.getInstance().writeLog("First chromsome: " + BitMapping.calculateChromoseBinaryName(A.getGenes()));
			Logger.getInstance().writeLog("Second chromsome: " + BitMapping.calculateChromoseBinaryName(B.getGenes()));

			int lengthOfChromosome = A.getGenes().length;

			int sliceChromosome = lengthOfChromosome / 2;

			boolean[] geneA = new boolean[lengthOfChromosome];
			boolean[] geneB = new boolean[lengthOfChromosome];

			
			geneA = A.getGenes();
			geneB = B.getGenes();
			
			// First half of A getting traits of B
			for (int i = sliceChromosome; i < lengthOfChromosome; i++) {
				geneA[i] = B.getGenes()[i];
			}

			// Second half of B getting traits of A
			for (int i = sliceChromosome; i < lengthOfChromosome; i++) {
				geneB[i] = A.getGenes()[i];
			}
			
			Chromosome newA = new Chromosome(lengthOfChromosome);
			newA.setGenes(geneA);
			
			Chromosome newB = new Chromosome(lengthOfChromosome);
			newA.setGenes(geneB);

			if (!isDuplicateChromosome(newA.getGenes()) && !BitMapping.fitnessAlreadyCalculated(newA.getGenes())) {
				newA.setNewChromsome(true);
				addChromosome(newA);
			}

			if (!isDuplicateChromosome(newB.getGenes()) && !BitMapping.fitnessAlreadyCalculated(newB.getGenes())) {
				newA.setNewChromsome(true);
				addChromosome(newB);
			}

			Logger.getInstance().writeLog("Corsssover Ended\n================================\n");
			actualPopulationSize = this.chromosomes.size();
			count++;
		}
	}

	private void removeChromosome(Chromosome c) {
		for(int i=0; i<chromosomes.size(); i++){
			if(BitMapping.calculateChromoseBinaryName(chromosomes.get(i).getGenes()).compareTo(BitMapping.calculateChromoseBinaryName(c.getGenes()))==0){
				chromosomes.remove(i);
				return;
			}
		}
	}
	
	private void addChromosome(Chromosome c) {
			Logger.getInstance().writeLog("Adding Chromosome to verctor: "+ BitMapping.calculateChromoseBinaryName(c.getGenes()));
			chromosomes.add(c);
	}

	

	private boolean isDuplicateChromosome(boolean[] genes) {

		for (Chromosome temp : chromosomes) {
			if (BitMapping.calculateChromoseBinaryName(temp.getGenes())
					.compareTo(BitMapping.calculateChromoseBinaryName(genes)) == 0) {
				return true;
			}
		}

		return false;
	}

	public void printPopulation() {
		
		Logger.getInstance().writeLog("===============\nPrinting Population\n=============\n==============");
		Logger.getInstance().writeLog("===============\nSize of population: "+ chromosomes.size());
		
		for (Chromosome c : chromosomes) {
			String binaryName = BitMapping.calculateChromoseBinaryName(c.getGenes());
			Logger.getInstance().writeLog(binaryName);
			Logger.getInstance().writeLog("Fitness: "+FitnessValueMap.getFitness().get(binaryName));
			
			Logger.getInstance().writeLog("Top: "+c.isTopper());
		}
		
		Logger.getInstance().writeLog("================");
	}


	public void elitism() {

		
		Logger.getInstance().writeLog("\nPerforming elitism to save fittest for next population....");
		Chromosome[] arrayToSort = chromosomes.toArray(new Chromosome[chromosomes.size()]);
		
		
		int n = arrayToSort.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				Double a = FitnessValueMap.getFitness(arrayToSort[j]);
				Double b = FitnessValueMap.getFitness(arrayToSort[j + 1]);
				
				if ((a==null?0:a) < (b==null?0:b)) {
					// swap temp and arr[i]
					Chromosome temp = arrayToSort[j];
					arrayToSort[j] = arrayToSort[j + 1];
					arrayToSort[j + 1] = temp;
				}
			}
		}

		chromosomes = new ArrayList<Chromosome>(Arrays.asList(arrayToSort));
		//Removing all chromosomes in population and adding only the ones which are top in fitness
		//on the basis of elitism value
		
		for(int i=0 ;i<chromosomes.size(); i++){
			chromosomes.get(i).setTopper(false);
		}
		
		if(chromosomes.size()<1){
			return;
		}

		if (chromosomes.size() < (ProjectConfiguration.populationSize / 2) && chromosomes.size()>0) {
			chromosomes.get(0).setTopper(true);
			return;
		}
		
		for(int i=0;i<ProjectConfiguration.elitismValue;i++){
			chromosomes.get(i).setTopper(true);
		}
	
		Logger.getInstance().writeLog("......... Elitism Ended ............\n");
	}


	public void calculateFitnessValue() {

		Logger.getInstance().writeLog("\nCalculating fitness value .....");
		for (Chromosome c : chromosomes) {
			if (FitnessValueMap.getFitness(c) == null) {
				
				double fitnessValue=0;
				double fitnessPercentage=0;
				try {
					
					 fitnessValue = calculateFitness(c);

					Logger.getInstance().writeLog("============== Fitness of Chromosome: " +  BitMapping.calculateChromoseBinaryName(c.getGenes())  +   "  ===============");
					Logger.getInstance().writeLog("============== Actual energy value: " + fitnessValue);
					
					fitnessPercentage = ((ProjectConfiguration.truthValue - fitnessValue)/ProjectConfiguration.truthValue ) * 100;
					
					Logger.getInstance().writeLog("============== Fitness/improvement value: " + fitnessPercentage);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				FitnessValueMap.getFitness().put(BitMapping.calculateChromoseBinaryName(c.getGenes()), fitnessPercentage);
				FitnessLogger.getInstance().writeLog(BitMapping.calculateChromoseBinaryName(c.getGenes()), new Double(fitnessValue).toString());
			} else{
				Logger.getInstance().writeLog("Fitness values of population already exist\n");
			}
		}
	}


	private boolean loadFitnessFile() {
	
		String csvFile = "Fitness";
		int noOfRecordsInFile = 0;
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			
			while ((line = reader.readNext()) != null) {
				if(line[0].contains("0") || line[0].contains("1"))
				{
					noOfRecordsInFile++;
					Chromosome c = convertStringToChromosome(line[0]); 
					
					addChromosome(c);

					double fitnessValue =  new Double(line[1]);
					double fitnessPercentage = ((ProjectConfiguration.truthValue - fitnessValue)/ProjectConfiguration.truthValue ) * 100;
					FitnessValueMap.getFitness().put(BitMapping.calculateChromoseBinaryName(c.getGenes()), fitnessPercentage);
				}
			}
		} catch (IOException e) {
			return false;
		}
		
		for(int i=0; i<(noOfRecordsInFile - populationSize); i++){
			removeChromosome(chromosomes.get(i));
		}
		
		return true;
		
	}
	
	private Chromosome convertStringToChromosome(String c){
		String[] letters = c.split("");
		boolean[] genes = new boolean[letters.length];
		
		for(int i=0; i<letters.length; i++){
			if(letters[i].contains("0")){
				genes[i] = false;;
			}else{
				genes[i] = true;
			}
		}
		
		Chromosome ch = new Chromosome(genes.length);
		ch.setGenes(genes);
		
		return ch;
		
	}

	private double calculateFitness(Chromosome c) throws IOException {
		String transformationType = BitMapping.calculateChromoseBinaryName(c.getGenes());
		int randomId = (int) (Math.random() * 256);
		ProjectConfiguration.batchName = ProjectConfiguration.projectName + randomId + "-" + transformationType;
		
		Logger.getInstance().writeLog("Generating configurations ....");
		generateConfiguration(c);
		Logger.getInstance().writeLog("Configurations generated ....");
		
		Logger.getInstance().writeLog("Running Transformation ....");
		runTransformation(c);
		Logger.getInstance().writeLog("Transformation completed ....");
		
		Logger.getInstance().writeLog("Making Test Case to greenminer....");
		runTestSuite(c);
		Logger.getInstance().writeLog("Test Case request sent ....");
		
		Logger.getInstance().writeLog("Running Test Case ....");
		boolean completed = waitForTestSuiteToComplete();
		Logger.getInstance().writeLog("Batch processing ended ...");
	
		if(!completed)
			return 0;

		/*if(calculationFailed()){
			Logger.getInstance().writeLog("Fitness is zero as calculation Failed");
			return 0;
		}*/
	
		Logger.getInstance().writeLog("Aggregating results ....");
		aggregateResults(c);
		Logger.getInstance().writeLog("Results Aggregated ....");
		
		int numberOfRecords = recordsInFile();
		
		while(numberOfRecords<2){
			waitForTestSuiteToComplete();
			Logger.getInstance().writeLog("Aggregating results ....");
			aggregateResults(c);
			Logger.getInstance().writeLog("Results Aggregated ....");
			numberOfRecords = recordsInFile();
		}
		
		Logger.getInstance().writeLog("Parsing Result File");
		double fitnessValue = parseResultFile(); 
		
		/*Logger.getInstance().writeLog("\n=================================");
		Logger.getInstance().writeLog("For Chromsome: " + transformationType);
		Logger.getInstance().writeLog("Fitness value calculated: "+fitnessValue);
		Logger.getInstance().writeLog("=================================\n");*/
		
		return fitnessValue;
	}

	/*private boolean calculationFailed() throws IOException {
		boolean energyCalculationFailed = false;
		File source = new File("ManualFitness");
		try {
			try (BufferedReader br = new BufferedReader(new FileReader(source))) {
				String line;

				while ((line = br.readLine()) != null) {
					energyCalculationFailed = Boolean.parseBoolean(line.split("=")[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileWriter fw = new FileWriter(source, false);
		fw.write("The energy calculation failed =false");
		fw.close();
		return energyCalculationFailed;
	}*/

	private double parseResultFile() {

		String csvFile = ProjectConfiguration.savedResultFile;

		ArrayList<Double> fitnessValues = new ArrayList<Double>(0);

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			if ((line = reader.readNext()) != null) {
				reader.readNext();
			}
			while ((line = reader.readNext()) != null) {
				//  joules=  line[7] 
				fitnessValues.add(Double.parseDouble(line[7]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return com.greenminer.ga.mathematics.Math.calculateMedian(fitnessValues);

	}
	
	private int recordsInFile() {

		
		int numberOfRecords = 0;
		String csvFile = ProjectConfiguration.savedResultFile;


		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(csvFile));
			String[] line;
			if ((line = reader.readNext()) != null) {
				//reader.readNext();
			}
			while ((line = reader.readNext()) != null) {
				//  joules=  line[7] 
				numberOfRecords++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return numberOfRecords;

	}

	private boolean waitForTestSuiteToComplete() {

		long waitTime = 2;
		int stuck = 0;
		System.setProperty("webdriver.gecko.driver",
				"/home/abdulali/Desktop/CMPUT680/Project/680WS/geckodriver-v0.23.0-linux64/geckodriver");
		String originalBatch = ProjectConfiguration.batchName;
		WebDriver driver;
		while (true) {
			
			stuck++;
			if(stuck>10){
				Logger.getInstance().writeLog("%%%%%%%%%%%%%%%%%%%% Couldn't run test remove APK %%%%%%%%%%%%%%%%%%%%%%%");
				return false;
			}
			
			try {
				TimeUnit.MINUTES.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			driver = new FirefoxDriver();
			driver.get("https://pizza.cs.ualberta.ca/gm/queue.html");

			Document doc = Jsoup.parse(driver.getPageSource());

			Element batchElement = null;
			if (doc.select("h3").size() != 0) {
				batchElement = doc.select("h3").first();
				String batchName = batchElement.childNodes().get(0).toString();
				driver.close();
				if (!batchName.contains(originalBatch)) {
					return true;
				}else{
					Logger.getInstance().writeLog("Batch " + originalBatch + " still being processed .... will check after " +waitTime+ " mins ....");
				}
			} else {
				driver.close();
				return true;
			}

		}
	}

	private void aggregateResults(Chromosome c) throws IOException {
		
		try {
			TimeUnit.MINUTES.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		File input = new File("/tmp/input.html");
		input.createNewFile();
		System.setProperty("webdriver.gecko.driver", "/home/abdulali/Desktop/CMPUT680/Project/680WS/geckodriver-v0.23.0-linux64/geckodriver");
		String transformationType = BitMapping.calculateChromoseBinaryName(c.getGenes());
		WebDriver driver = new FirefoxDriver();
		try{
		driver.manage().timeouts().pageLoadTimeout(120,TimeUnit.SECONDS);
		String linkToParse = "https://pizza.cs.ualberta.ca/gm/graphs.py?"
				+ "batch="
				+ ProjectConfiguration.batchName
				+ "&device=&"
				+ "test="
				+ ProjectConfiguration.testSuiteName
				+ "&graph=graph";
		Logger.getInstance().writeLog(linkToParse);
		driver.get(linkToParse);
		Document doc = Jsoup.parse(driver.getPageSource());
		Element totalCsvLink = doc.select("a[href$=totals.csv]").first(); // a with href
		String link = "https://pizza.cs.ualberta.ca/gm/" + totalCsvLink.attr("href");
		String resultFolder = "/home/abdulali/Desktop/CMPUT680/Project/Result/"+ProjectConfiguration.projectName+"/";
		ProjectConfiguration.savedResultFile = resultFolder+  transformationType +".csv";
		
		URL website = new URL(link);
		File file = new File(ProjectConfiguration.savedResultFile);
		file.createNewFile();
		FileUtils.copyURLToFile(website, file);
		driver.close();
		} catch (org.openqa.selenium.TimeoutException e){
			Logger.getInstance().writeLog("Exception in thread \"main\" org.openqa.selenium.TimeoutException: Timeout loading page!");
			driver.close();
			aggregateResults(c);
		}
	}

	/*private double takeInputFitnessValue(Chromosome c) {
		String transformationType = BitMapping.calculateChromoseBinaryName(c.getGenes());
		Scanner scanner = new Scanner(System.in);
		System.out.print("Type in the fitness for -> "+ProjectConfiguration.batchName + "-" +transformationType+": ");
		String input = scanner.nextLine();
		Double inputValue = Double.parseDouble(input);
		Logger.getInstance().writeLog("Input = " + inputValue);
		return inputValue;
	}*/

	private void runTestSuite(Chromosome c) throws IOException {
		String transformationType = BitMapping.calculateChromoseBinaryName(c.getGenes());
		String appFolder = ProjectConfiguration.projectName;
		String testSuiteName = ProjectConfiguration.testSuiteName;
		String numberOfRepititions = "3";
		String version = transformationType;
		
		try {
			TimeUnit.MINUTES.sleep((long) 0.1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//Run testcases
		String command = "curl 'https://pizza.cs.ualberta.ca/gm/queue.pl?add_tests' -H 'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:63.0) "
				+ "Gecko/20100101 Firefox/63.0' -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' -H 'Accept-Language: en-CA,en-US;q=0.7,en;q=0.3' --compressed -H "
				+ "'Referer: https://pizza.cs.ualberta.ca/gm/add_tests.html' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Connection: keep-alive' -H 'Upgrade-Insecure-Requests: 1'"
				+ " --data '"
				+ "app="
				+ appFolder
				+ "&"
				+ "test="
				+ testSuiteName
				+ "&"
				+ "repetitions="
				+ numberOfRepititions
				+ "&"
				+ "batch_name="
				+ ProjectConfiguration.batchName
				+ "&"
				+ "versions="
				+ version
				+ "&how=suggest'";

		Logger.getInstance().writeLog("\nCommand Running: "+ command);
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
		builder.redirectErrorStream(true);
		Process p = builder.start();
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) {
				break;
			}
			sb.append(line);
		}
		Logger.getInstance().writeLog("Running CurL command: "+sb);
		
	}

	private void runTransformation(Chromosome c) {
		String outputApkPath = TransformationUtility.runSingleTransformation(c);
		TransformationUtility.sendTransformedFileToServer(outputApkPath);
	}

	private void generateConfiguration(Chromosome c) {
		ConfigFileUtility.generateConfigurationFile(c);
	}

	public Chromosome tournamentSelection() {
		Logger.getInstance().writeLog("Tournament selection started");
		Population tournament = new Population(populationSize,geneSize, false, false);
		
		
		
		for (int i = 0; i < this.chromosomes.size() / 2; i++) {
			int randomId = (int) (Math.random() * this.chromosomes.size());
			
			if (!this.chromosomes.get(randomId).isNewChromsome())
				Logger.getInstance().writeLog("Adding chr to tournament vector:- " + BitMapping.calculateChromoseBinaryName(this.chromosomes.get(randomId).getGenes()));
				tournament.chromosomes.add(this.chromosomes.get(randomId));
		}
		
		Chromosome fittest = null;
		
		if (tournament.chromosomes.size() > 1){
			fittest = getFittest(tournament.chromosomes.get(0), tournament.chromosomes.get(1));
		} else if (tournament.chromosomes.size() == 1){
			return tournament.chromosomes.get(0);
		}
			
		Logger.getInstance().writeLog("Returning Fittest for Tournament");
		return fittest;
	}

	private Chromosome getFittest(Chromosome c1, Chromosome c2) {
		return (FitnessValueMap.getFitness(c1) > FitnessValueMap.getFitness(c2)) ? c1 : c2;
	}

	public double getMaxFitness() {
		Chromosome[] arrayToSort = chromosomes.toArray(new Chromosome[chromosomes.size()]);
		int n = arrayToSort.length;
		for (int i = 0; i < n - 1; i++)
			for (int j = 0; j < n - i - 1; j++)
				if (FitnessValueMap.getFitness(arrayToSort[j]) < FitnessValueMap.getFitness(arrayToSort[j + 1])) {
					// swap temp and arr[i]
					Chromosome temp = arrayToSort[j];
					arrayToSort[j] = arrayToSort[j + 1];
					arrayToSort[j + 1] = temp;
				}
		return FitnessValueMap.getFitness(arrayToSort[0]);
	}

	public ArrayList<Chromosome> getChromosomes() {
		return chromosomes;
	}

	public void setChromosomes(ArrayList<Chromosome> chromosomes) {
		this.chromosomes = chromosomes;
	}

	public void calculateTruthFitnessValue() {
		Logger.getInstance().writeLog("\nCalculating fitness value .....");
		Chromosome c = chromosomes.get(0);
		if (FitnessValueMap.getFitness(c) == null) {
			double fitnessValue=0;
			double fitnessPercentage=0;
			try {
				fitnessValue = ProjectConfiguration.truthValue > 0 ? ProjectConfiguration.truthValue:calculateFitness(c);
				
				Logger.getInstance().writeLog("============== Fitness of Chromosome: " +  BitMapping.calculateChromoseBinaryName(c.getGenes())  +   "  ===============");
				Logger.getInstance().writeLog("============== Actual energy value: " + fitnessValue);
				
				fitnessPercentage = 0;
				
				Logger.getInstance().writeLog("============== Fitness/improvement value: " + fitnessPercentage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			FitnessValueMap.getFitness().put(BitMapping.calculateChromoseBinaryName(c.getGenes()), fitnessPercentage);
			
			if (ProjectConfiguration.truthValue == 0)
				FitnessLogger.getInstance().writeLog(BitMapping.calculateChromoseBinaryName(c.getGenes()), new Double(fitnessValue).toString());
			
			ProjectConfiguration.truthValue = fitnessValue;
			Logger.getInstance().writeLog("Ground fitness value:- "+fitnessValue);
		} else{
			Logger.getInstance().writeLog("Fitness values of population already exist\n");
		}
	}

	public void removeWeakChromsomes() {
		
		Logger.getInstance().writeLog("......... Removing less fit chromosomes ............\n");
		ArrayList<Chromosome> cs = new ArrayList<>();
		for(Chromosome c: chromosomes){
			if(FitnessValueMap.getFitness(c) < ProjectConfiguration.minimumThresholdFitness){
				cs.add(c);
			}
		}
		
		for(Chromosome c : cs){
			removeChromosome(c);
		}
		
		
	}

	public boolean keepTopPopulation() {
		
		Logger.getInstance().writeLog("......... Removing Half Weak Population ............\n");
		
		if(chromosomes.size() == 0){
			Logger.getInstance().writeLog("......... Previous populatin was a waste .. generating new pop ............\n");
			for (int i = chromosomes.size(); i < populationSize; i++) {
				addChromosome(randomChromosome());
			}
			return false;
		}
		
		Chromosome[] arrayToSort = chromosomes.toArray(new Chromosome[chromosomes.size()]);

		int n = arrayToSort.length;
		for (int i = 0; i < n - 1; i++)
			for (int j = 0; j < n - i - 1; j++)
				if (FitnessValueMap.getFitness(arrayToSort[j]) > FitnessValueMap.getFitness(arrayToSort[j + 1])) {
					// swap temp and arr[i]
					Chromosome temp = arrayToSort[j];
					arrayToSort[j] = arrayToSort[j + 1];
					arrayToSort[j + 1] = temp;
				}
	
		chromosomes = new ArrayList<Chromosome>(Arrays.asList(arrayToSort));
		
		for(int i=0 ;i<chromosomes.size()/2; i++){
			removeChromosome(chromosomes.get(i));
		}
		
		Logger.getInstance().writeLog("......... Removed Half Population ............\n");

		return true;
	}

	public void normalizeSizeOfPopulation() {

		Logger.getInstance().writeLog("================ Normalizing Population size ===============");

		for(int i= chromosomes.size(); i<populationSize; i++){
			chromosomes.add(randomChromosome());
		}
		Logger.getInstance().writeLog("================ Normalized population size ===============");
	}

	public void setAllToppersFalse() {
		for(Chromosome c : chromosomes){
			c.setTopper(false);
		}
		
	}

}