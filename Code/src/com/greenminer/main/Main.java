package com.greenminer.main;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.greenminer.ga.BitMapping;
import com.greenminer.ga.Chromosome;
import com.greenminer.ga.Population;
import com.greenminer.logger.Logger;

/**
 * 
 * @author abdulali
 * @since 22 November 2018
 *
 *	Note: Chromsome mapping from BitVector to transformationType exists in com.greenminer.ga.BitMapping.passes 
 *
 */
public class Main {

	public static void main(String[] args) {
		
		//experimentStartTime
		printTime();

		Logger.getInstance().writeLog("Calculating Ground Truth before starting the program");
		
		Population groundTruthPopulation = new Population(1, ProjectConfiguration.geneSize, false, false);
		ArrayList<Chromosome> chromosomes = new ArrayList<>(0);
		Chromosome c = new Chromosome(ProjectConfiguration.geneSize);
		chromosomes.add(c);
		groundTruthPopulation.setChromosomes(chromosomes);
		groundTruthPopulation.calculateTruthFitnessValue();
		
		Logger.getInstance().writeLog("===========================================================");
		Logger.getInstance().writeLog("===========================================================");
		Logger.getInstance().writeLog("============ GREEN EVOLUTIONARY COMPUTATION ===============");
		Logger.getInstance().writeLog("========================= STARTED =========================");
		Logger.getInstance().writeLog("===========================================================");
		Logger.getInstance().writeLog("===========================================================");
		
		Logger.getInstance().writeLog("===> Generation: 1  <======");
		Population p = new Population(ProjectConfiguration.populationSize,ProjectConfiguration.geneSize,true, true);
		p.calculateFitnessValue();
		Logger.getInstance().writeLog("\n==============\n==============\n");
		int noOfGeneration = ProjectConfiguration.startingGeneration;
		ProjectConfiguration.maxFitness = p.getMaxFitness();
		p.printPopulation();
		
		/*Population p = new Population(POPULATION_SIZE, GENE_SIZE, false, true);
		ProjectConfiguration.truthValue = 60.23252553620001;
		ProjectConfiguration.maxFitness = 11.147142644657555;
		int noOfGeneration = 5;
		System.out.println("Starting again from generation number:- "+noOfGeneration);*/
		int fitnessNotImproved = 0;
		
		while(fitnessNotImproved<10 && noOfGeneration<20){
			Logger.getInstance().writeLog("===> Generation: " + noOfGeneration + "  <======");
			noOfGeneration++;
			p.removeWeakChromsomes();
			boolean success = p.keepTopPopulation(); //success means previous population was a waste and now creating new population.
			
			if (success) {

				if (p.getChromosomes().size() > (ProjectConfiguration.populationSize / 2)) {
					p.crossoverPopulation();
				}

				p.elitism();
				p.mutatePopulation();
				p.normalizeSizeOfPopulation();
			}
			
			p.calculateFitnessValue();
			p.setAllToppersFalse();
			p.printPopulation();
			
			if(p.getMaxFitness()<=ProjectConfiguration.maxFitness){
				fitnessNotImproved++;
			}else{
				ProjectConfiguration.maxFitness = p.getMaxFitness();
			}
			Logger.getInstance().writeLog("Max fitness for this generation:- "+ ProjectConfiguration.maxFitness);
			Logger.getInstance().writeLog("\n       ==============\n       ==============\n");
		}
		
		Logger.getInstance().writeLog("\n======================================================");
		Logger.getInstance().writeLog("==========   Max Fitness Achieved: " + ProjectConfiguration.maxFitness);
	
		//experimentEndTime
		printTime();
	
	}

	private static void printTime() {
	    Date date = new Date();

	    String strDateFormat = "hh:mm:ss a";

	    DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

	    String formattedDate= dateFormat.format(date);

	    Logger.getInstance().writeLog("========== TIME :- " + formattedDate);
		
	}

}
