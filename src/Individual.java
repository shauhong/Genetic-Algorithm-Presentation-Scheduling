import java.util.Arrays;

public class Individual {
	private int[] chromosome;
	private int fitness = -Integer.MAX_VALUE;
//	private int[] hcViolations=new int[4];
//	private int[] scViolations=new int[3];

	public Individual(int chromosomeLength) {
		// Create random individual
		int[] individual;
		individual = new int[chromosomeLength];
		for (int gene = 0; gene < chromosomeLength; gene++) {
			individual[gene] = gene+1;
		}
		
		this.chromosome = individual;
	}
    
	public Individual(int[] chromosome) {
		this.chromosome = chromosome;
	}
	
	public Individual(Schedule schedule) {
		chromosome=new int[schedule.getNumPresentations()];
		for(int i=0;i<schedule.getNumPresentations();i++) {
			chromosome[i]=schedule.getRandomTimeslot().getTimeslotID();
		}
	}

	public int[] getChromosome() {
		return this.chromosome;
	}

	public int getChromosomeLength() {
		return this.chromosome.length;
	}

	public void setGene(int offset, int gene) {
		this.chromosome[offset] = gene;
	}

	public int getGene(int offset) {
		return this.chromosome[offset];
	}


	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public int getFitness() {
		return this.fitness;
	}
	
//	public void setHc(int hc01,int hc02,int hc03,int hc04) {
//		this.hcViolations[0]=hc01;
//		this.hcViolations[1]=hc02;
//		this.hcViolations[2]=hc03;
//		this.hcViolations[3]=hc04;
//	}
//	
//	public void setSc(int sc01,int sc02,int sc03) {
//		this.scViolations[0]=sc01;
//		this.scViolations[1]=sc02;
//		this.scViolations[2]=sc03;
//	}
//	
//	public int[] getHc() {
//		return this.hcViolations;
//	}
//	
//	public int[] getSc() {
//		return this.scViolations;
//	}
	
	public String toString() {
		String output = "";
		for (int gene = 0; gene < this.chromosome.length; gene++) {
			output += this.chromosome[gene] + ",";
		}
		return output;
	}

	public boolean containsGene(int gene) {
		for (int i = 0; i < this.chromosome.length; i++) {
			if (this.chromosome[i] == gene) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash=Arrays.hashCode(this.chromosome);
		return hash;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object==null) {
			return false;
		}
		if(getClass()!=object.getClass()) {
			return false;
		}
		Individual individual=(Individual) object;
		return Arrays.equals(this.chromosome, individual.chromosome);
	}

	
}
