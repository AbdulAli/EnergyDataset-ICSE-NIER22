package com.greenminer.ga;

public class Chromosome {
	
	public boolean genes[];
	
	private boolean topper = false;
	
	private boolean newChromsome;
	
	public Chromosome(int geneSize) {
		genes = new boolean[geneSize];
	}

	public Chromosome(boolean[] genes) {
		this.genes =genes; 
	}

	public boolean[] getGenes() {
		boolean[] gs = new boolean[this.genes.length];
		
		for(int i=0;i<genes.length;i++){
			gs[i] = genes[i];
		}
		
		return gs;
	}

	public void setGenes(boolean[] genes) {
		this.genes = genes;
	}

	public boolean isTopper() {
		return topper;
	}

	public void setTopper(boolean topper) {
		this.topper = topper;
	}

	public Chromosome copy() {
		Chromosome x =new Chromosome(genes.length);
		x.genes = this.getGenes();
		x.topper = false;
		return x;
	}

	public boolean isNewChromsome() {
		return newChromsome;
	}

	public void setNewChromsome(boolean newChromsome) {
		this.newChromsome = newChromsome;
	}
	
	
	
	
}
