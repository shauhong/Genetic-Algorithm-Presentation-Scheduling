import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {
	private Individual population[];
	private int populationFitness=-Integer.MAX_VALUE;
	
	public Population(int populationSize) {
		this.population=new Individual[populationSize];
	}
	
	public Population(int populationSize,int chromosomeLength) {
		this.population = new Individual[populationSize];
		for (int i=0;i<populationSize;i++) {
			Individual individual=new Individual(chromosomeLength);
			this.population[i]=individual;
		}
	}
	
	public Population(int populationSize, Schedule schedule) {
		this.population=new Individual[populationSize];
		for(int i=0;i<populationSize;i++) {
			Individual individual = new Individual(schedule);
			this.population[i]=individual;
		}
	}
	
	public Individual[] getIndividuals() {
		return this.population;
	}
	
	//Return the individual based on the offset, from the best to worst individuals
	public Individual getFittest(int offset) {
		// Sort the population based on fitness value
		Arrays.sort(this.population, new Comparator<Individual>() {
			@Override
			public int compare(Individual o1, Individual o2) {
				if (o1.getFitness() > o2.getFitness()) {
					return -1;
				} else if (o1.getFitness() < o2.getFitness()) {
					return 1;
				}
				return 0;
			}
		});

		// Return the offset from the fittest individual
		return this.population[offset];
	}
	
	public void setPopulationFitness(int fitness) {
		this.populationFitness=fitness;
	}
	
	public int getPopulationFitness() {
		return populationFitness;
	}
	
	public int size() {
		return this.population.length;
	}
	
	public Individual setIndividual(int offset,Individual individual) {
		return population[offset]=individual;
	}
	
	public Individual getIndividual(int offset) {
		return population[offset];
	}
	
	//Shuffle the entire population into random order
	public void shuffle() {
		Random rnd = new Random();
		for(int i=0;i<population.length;i++) {
			int index = rnd.nextInt(i+1);
			Individual temp=population[index];
			population[index] = population[i];
			population[i]=temp;
		}
	}
}
