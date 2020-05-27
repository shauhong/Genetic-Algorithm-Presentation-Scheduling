import java.util.LinkedHashMap;
import java.util.Map;

public class GeneticAlgorithm {
	private int populationSize;
	private double mutationRate;
	private double crossoverRate;
	private int elitismCount;
	private double temperature=1.0;
	private double coolingRate;
	private Map<Individual,Integer> fitnessHash;
	private int tournamentSize;
	private double selectProbability;
	
	public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
			int tournamentSize, double selectProbability, double coolingRate) {
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
		this.tournamentSize = tournamentSize;
		this.selectProbability=selectProbability;
		this.coolingRate=coolingRate;
		this.fitnessHash=new LinkedHashMap<Individual,Integer>(){
			protected boolean removeEldestEntry(Map.Entry<Individual,Integer>eldest) {
				return this.size()>10000;
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
		int fitness=cloneSchedule.calcPenalty();
		individual.setFitness(fitness);
//		System.out.println("Fitness: "+fitness);
		this.fitnessHash.put(individual, fitness);
		return fitness;

	}
	
	public boolean isTerminationConditionMet(int generationsCount, int maxGenerations) {
		return (generationsCount > maxGenerations);
	}

	public boolean isTerminationConditionMet(Population population) {
		return population.getFittest(0).getFitness() == 0 ;
	}

	public Individual selectParent(Population population, double selectProbability) {
		// Create tournament
		Population tournament = new Population(this.tournamentSize);

		// Add random individuals to the tournament
		population.shuffle();
		for (int i = 0; i < this.tournamentSize; i++) {
			Individual tournamentIndividual = population.getIndividual(i);
			tournament.setIndividual(i, tournamentIndividual);
		}

		// Return the best
//		return tournament.getFittest(0);
		for(int i=0; i<this.tournamentSize; i++) {
			Individual tournamentIndividual = tournament.getFittest(i);
			if(selectProbability>Math.random()) {
				return tournamentIndividual;
			}
		}
		return tournament.getFittest(this.tournamentSize-1);
	}
	
	public Population mutatePopulation(Population population, Schedule schedule) {
		// Initialize new population
		Population newPopulation = new Population(this.populationSize);

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual individual = population.getFittest(populationIndex);

			// Create random individual to swap genes with
			Individual randomIndividual = new Individual(schedule);
				for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
					// Skip mutation if this is an elite individual
					if (populationIndex >= this.elitismCount) {
						// Does this gene need mutation?
						if (this.mutationRate*temperature > Math.random()) {
							// Swap for new gene
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

	public Population crossoverPopulation(Population population,Schedule schedule) {
		// Create new population
		Population newPopulation = new Population(population.size());

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual parent1 = population.getFittest(populationIndex);

			// Apply crossover to this individual?
			if (this.crossoverRate> Math.random() && populationIndex >= this.elitismCount) {
				// Initialize offspring
				Individual offspring = new Individual(parent1.getChromosomeLength());
				
				// Find second parent
				Individual parent2 = selectParent(population,selectProbability);

				// Loop over genome
				for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
					// Use half of parent1's genes and half of parent2's genes
					if (0.5 > Math.random()) {
						offspring.setGene(geneIndex, parent1.getGene(geneIndex));
					} else {
						offspring.setGene(geneIndex, parent2.getGene(geneIndex));
					}
				}
				// Add offspring to new population
				newPopulation.setIndividual(populationIndex, offspring);
			} else {
				// Add individual to new population without applying crossover
				newPopulation.setIndividual(populationIndex, parent1);
			}
		}
		return newPopulation;
	}
	
	public void coolTemperature() {
		temperature=temperature*(1-coolingRate);
	}
}
