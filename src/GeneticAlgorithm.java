import java.util.LinkedHashMap;
import java.util.Map;

public class GeneticAlgorithm {
	private int populationSize;
	private double mutationRate;
	private double crossoverRate;
	private int elitismCount;
	private double temperature=1.0;
	private double finalTemperature=0.5;
	private double coolingRate;
	private Map<Individual,Integer> fitnessHash;
	private int tournamentSize;
	
	public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
			int tournamentSize, double coolingRate) {
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
		this.tournamentSize = tournamentSize;
		this.coolingRate=coolingRate;
		this.fitnessHash=new LinkedHashMap<Individual,Integer>(){
			protected boolean removeEldestEntry(Map.Entry<Individual,Integer>eldest) {
				return this.size()>1000;
			}
		};
	}
	
	public Population initPopulation(int chromosomeLength) {
		Population population = new Population(chromosomeLength);
		return population;
	}
	
	public Population initPopulation(Schedule schedule) {
		Population population = new Population(populationSize,schedule);
		return population;
	}
	
	public void evalPopulation(Population population, Schedule schedule) {
		Individual[] individuals=population.getIndividuals();
		int populationFitness=0;
		for(int i=0;i<individuals.length;i++) {
			populationFitness+=calcFitness(individuals[i],schedule);
		}
		population.setPopulationFitness(populationFitness);
	}
	
	public int calcFitness(Individual individual, Schedule schedule) {
		Integer storedFitness=this.fitnessHash.get(individual);
		if(storedFitness!=null) {
			return storedFitness;
		}
		Schedule cloneSchedule=new Schedule(schedule);
		cloneSchedule.createScheduledPresentations(individual);
		int fitness=-cloneSchedule.calcPenalty();
		individual.setFitness(fitness);
		this.fitnessHash.put(individual, fitness);
		return fitness;

	}
	
	public boolean isTerminationConditionMet(int generationsCount, int maxGenerations) {
		return (generationsCount > maxGenerations);
	}

	public boolean isTerminationConditionMet(Population population) {
		return population.getFittest(0).getFitness() == 0 ;
	}
	
	public Individual selectParent(Population population) {
		// Create tournament
		Population tournament = new Population(this.tournamentSize);

		// Add random individuals to the tournament
		population.shuffle();
		for (int i = 0; i < this.tournamentSize; i++) {
			Individual tournamentIndividual = population.getIndividual(i);
			tournament.setIndividual(i, tournamentIndividual);
		}

		// Return the best individual in the tournament
		return tournament.getFittest(0);
	}
	
	public Population mutatePopulation(Population population, Schedule schedule) {
		// Initialize new population
		Population newPopulation = new Population(this.populationSize);

		// Loop over the current population based on fitness value
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual individual = population.getFittest(populationIndex);

			// Create new random individual to swap genes
			Individual randomIndividual = new Individual(schedule);
				for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
					// Skip mutation if this is an elite individual
					if (populationIndex >= this.elitismCount) {
						// Perform mutation if the mutation rate is higher than the random value
						if (this.mutationRate * temperature> Math.random()) {
							// Swap gene
							individual.setGene(geneIndex, randomIndividual.getGene(geneIndex));
						}
				}
			}
			// Add individual to population
			newPopulation.setIndividual(populationIndex, individual);
		}
		// Return mutated population
		return newPopulation;
	}
	
	public Population crossoverPopulation(Population population) {
		// Create new population
		Population newPopulation = new Population(population.size());

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual parent = population.getFittest(populationIndex);

			// Perform crossover if the crossover rate is higher than the random value
			// and the individual is not an elite individual
			if (this.crossoverRate*temperature> Math.random() && populationIndex >= this.elitismCount) {
				// Initialize 2 offspring
				Individual offspring1 = new Individual(parent.getChromosomeLength());
				Individual offspring2 = new Individual(parent.getChromosomeLength());
				
				// Find 2 parents
				Individual parent1 = selectParent(population);
				Individual parent2 = selectParent(population);

				// Loop over gene
				for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
					// Use half of parent1's genes and half of parent2's genes
					if (0.5 > Math.random()) {
						offspring1.setGene(geneIndex, parent1.getGene(geneIndex));
						offspring2.setGene(geneIndex, parent2.getGene(geneIndex));
					} else {
						offspring1.setGene(geneIndex, parent2.getGene(geneIndex));
						offspring2.setGene(geneIndex, parent1.getGene(geneIndex));
					}
				}
				// Add the new offspring to new population
				newPopulation.setIndividual(populationIndex, offspring1);
				populationIndex++;
				if(populationIndex<population.size())
					newPopulation.setIndividual(populationIndex, offspring2);
			} else {
				// Add the elite individual to new population without undergoing crossover
				newPopulation.setIndividual(populationIndex, parent);
			}
		}
		return newPopulation;
	}
	
	public void coolTemperature() {
		if(temperature<finalTemperature) {
			temperature=finalTemperature;
		}else {
			temperature=temperature*(1-coolingRate);
		}
	}
}
