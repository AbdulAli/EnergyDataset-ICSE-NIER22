package com.greenminer.ga;

import java.util.ArrayList;

public abstract class BitMapping {
	
	 /*"ReBindRefsPass",
     "BridgePass",
     "SynthPass",
     "FinalInlinePass",
     "DelSuperPass",
     "SimpleInlinePass",
     "PeepholePass",
     "ConstantPropagationPass",
     "LocalDcePass",
     "RemoveUnreachablePass",
     "RemoveGotosPass",
     "DedupBlocksPass",
     "SingleImplPass",
     "ReorderInterfacesPass",
     "RemoveEmptyClassesPass",
     "ShortenSrcStringsPass",
     "RegAllocPass",
     "CopyPropagationPass",
     "LocalDcePass"*/

	static String passes[] = { "ReBindRefsPass", "SynthPass", "Inline", "DelSuperPass", "PeepholePass",
			"ConstantPropagationPass", "LocalDcePass", "RemoveUnreachablePass", "RemoveGotosPass", "DedupBlocksPass",
			"SingleImplPass", "ReorderInterfacesPass", "RemoveEmptyClassesPass", "ShortenSrcStringsPass",
			"RegAllocPass", "CopyPropagationPass", "LocalDcePass"};

	public static String getPassName(int arrayIndex){
		return passes[arrayIndex];
	}
	
	public static ArrayList<String> getStringGene(boolean genes[]){
		
		ArrayList<String> stringGenes = new ArrayList<String>(0);
		
		for(int i=0; i <genes.length; i++){
			if(genes[i]==true)
			stringGenes.add(getPassName(i));
		}
		return stringGenes;
	}
	
	public static String calculateChromoseBinaryName(boolean[] genes) {
		String fileName = "";
		for (boolean g : genes) {
			fileName += g ? "1" : "0";
		}
		return fileName;
	}

	public static boolean fitnessAlreadyCalculated(boolean[] genes) {
		if (FitnessValueMap.getFitness().isEmpty())
			return true;
		
		return FitnessValueMap.getFitness().containsKey(calculateChromoseBinaryName(genes))? true: false; 
			
	}

}
