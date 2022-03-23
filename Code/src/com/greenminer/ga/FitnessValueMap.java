package com.greenminer.ga;

import java.util.HashMap;

public abstract class FitnessValueMap {
	
	 static HashMap<String, Double> fitness = new HashMap<>(); 
     
	 public static void updateFitnessValue(String gene, Double value){
		 
	 }


	public static HashMap<String, Double> getFitness() {
		return fitness;
	}


	public static void setFitness(HashMap<String, Double> fitness) {
		FitnessValueMap.fitness = fitness;
	}
	
	public static Double getFitness(Chromosome c){
		return fitness.get(BitMapping.calculateChromoseBinaryName(c.getGenes()));
	}
	 
	 
	 
	

}
