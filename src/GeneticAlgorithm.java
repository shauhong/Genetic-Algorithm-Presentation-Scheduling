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
//	private double selectProbability;
	
	public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
			int tournamentSize, double coolingRate) {
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
		this.tournamentSize = tournamentSize;
//		this.selectProbability=selectProbability;
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
	
//	public Individual selectParent(Population population) {
//		// Get individuals
//		Individual individuals[] = population.getIndividuals();
//
//		// Spin roulette wheel
//		double populationFitness = population.getPopulationFitness();
//		double rouletteWheelPosition = Math.random() * populationFitness;
//
//		// Find parent
//		double spinWheel = 0;
//		for (Individual individual : individuals) {
//			spinWheel += individual.getFitness();
//			if (spinWheel >= rouletteWheelPosition) {
//				return individual;
//			}
//		}
//		return individuals[population.size() - 1];
//	}
	
	public Individual selectParent(Population population) {
		// Create tournament
		Population tournament = new Population(this.tournamentSize);

		// Add random individuals to the tournament
		population.shuffle();
		for (int i = 0; i < this.tournamentSize; i++) {
			Individual tournamentIndividual = population.getIndividual(i);
			tournament.setIndividual(i, tournamentIndividual);
		}

		// Return the best
		return tournament.getFittest(0);
//		for(int i=0; i<this.tournamentSize; i++) {
//			Individual tournamentIndividual = tournament.getFittest(i);
//			if(selectProbability>Math.random()) {
//				return tournamentIndividual;
//			}
//		}
//		return tournament.getFittest(this.tournamentSize-1);
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
						if (this.mutationRate * temperature> Math.random()) {
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

//	public Population crossoverPopulation(Population population) {
//		// Create new population
//		Population newPopulation = new Population(population.size());
//
//		// Loop over current population by fitness
//		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
//			Individual parent1 = population.getFittest(populationIndex);
//
//			// Apply crossover to this individual?
//			if (this.crossoverRate*temperature> Math.random() && populationIndex >= this.elitismCount) {
//				// Initialize offspring
//				Individual offspring = new Individual(parent1.getChromosomeLength());
//				
//				// Find second parent
//				Individual parent2 = selectParent(population);
//
//				// Loop over genome
//				for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
//					// Use half of parent1's genes and half of parent2's genes
//					if (0.5 > Math.random()) {
//						offspring.setGene(geneIndex, parent1.getGene(geneIndex));
//					} else {
//						offspring.setGene(geneIndex, parent2.getGene(geneIndex));
//					}
//				}
//				// Add offspring to new population
//				newPopulation.setIndividual(populationIndex, offspring);
//			} else {
//				// Add individual to new population without applying crossover
//				newPopulation.setIndividual(populationIndex, parent1);
//			}
//		}
//		return newPopulation;
//	}
	
	public Population crossoverPopulation(Population population) {
		// Create new population
		Population newPopulation = new Population(population.size());

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual parent = population.getFittest(populationIndex);

			// Apply crossover to this individual?
			if (this.crossoverRate*temperature> Math.random() && populationIndex >= this.elitismCount) {
				// Initialize offspring
				Individual offspring1 = new Individual(parent.getChromosomeLength());
				Individual offspring2 = new Individual(parent.getChromosomeLength());
				
				// Find second parent
				Individual parent1 = selectParent(population);
				Individual parent2 = selectParent(population);

				// Loop over genome
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
				// Add offspring to new population
				newPopulation.setIndividual(populationIndex, offspring1);
				populationIndex++;
				if(populationIndex<population.size())
					newPopulation.setIndividual(populationIndex, offspring2);
				
				
				
			} else {
				// Add individual to new population without applying crossover
				newPopulation.setIndividual(populationIndex, parent);
			}
		}
		return newPopulation;
	}
	
	public Population simulatedAnnealing(Population population, Schedule schedule) {
		Population newPopulation = new Population(population.size());
		for(int populationIndex=0;populationIndex<population.size();populationIndex++) {
			Individual individual = population.getFittest(populationIndex);
			if(populationIndex>=this.elitismCount) {
				int chromosome[] = new int[individual.getChromosomeLength()];
				int index1 = (int) (Math.random()*individual.getChromosomeLength());
				int index2 = (int) (Math.random()*individual.getChromosomeLength());
//				System.out.println(index1+" swap with "+index2);
				for(int i=0;i<individual.getChromosomeLength();i++) {
					chromosome[i]=individual.getChromosome()[i];
//					System.out.print(chromosome[i]+", ");
				}
//				System.out.println();
				//swap
				int temp=chromosome[index1];
				chromosome[index1]=chromosome[index2];
				chromosome[index2]=temp;
				Individual newIndividual= new Individual(chromosome);
//				System.out.println("Length: "+newIndividual.getChromosomeLength());
//				System.out.println(newIndividual.getChromosomeLength());
				int fitness=calcFitness(individual,schedule);
//				System.out.println("Ori: "+fitness);
				int newFitness=calcFitness(newIndividual,schedule);
//				System.out.println("New: "+newIndividual.getFitness());
//				System.out.println("New: "+newFitness);
				if(newFitness>fitness) {
					newPopulation.setIndividual(populationIndex, newIndividual);
				}else {
					double acceptProb=Math.exp((double)(newFitness-fitness)/temperature);
					if(Math.random()<acceptProb) {
						newPopulation.setIndividual(populationIndex, newIndividual);
					}else {
						newPopulation.setIndividual(populationIndex,individual);
					}
				}
			}else {
				newPopulation.setIndividual(populationIndex, individual);
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
