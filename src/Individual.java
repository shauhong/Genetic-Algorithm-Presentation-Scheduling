import java.util.Arrays;

public class Individual {
	private int[] chromosome;
	private int fitness = Integer.MAX_VALUE;

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
