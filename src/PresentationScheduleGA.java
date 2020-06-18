import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class PresentationScheduleGA {
	public static void main(String[] arg) {
		//Initialize schedule
		Schedule schedule = initializeSchedule();
		//Initialize Genetic Algorithm
		GeneticAlgorithm GA = new GeneticAlgorithm(100,0.01,0.9,5,15,0.001);	
		//Initialize population
		Population population=GA.initPopulation(schedule);
		//Evaluate population
		GA.evalPopulation(population, schedule);
		int generation=1; //keep track population
		//Start genetic operation
        while (!GA.isTerminationConditionMet(generation, 1000) 
                && !GA.isTerminationConditionMet(population)) {
                // Print fitness value of best individual of current generation
                System.out.println("Generation " + generation + ", Best Fitness: "
                + population.getFittest(0).getFitness());
                // Apply crossover operator
                population = GA.crossoverPopulation(population);
                // Apply mutation operator
                population = GA.mutatePopulation(population, schedule);
                // Evaluate population
                GA.evalPopulation(population, schedule);
                // Cool Temperature
                GA.coolTemperature();
                // Increment the current generation
                generation++;
            }
        int finalFitness=population.getFittest(0).getFitness();
        //Print final fitness
        printFinalResult(--generation,finalFitness);
		schedule.createScheduledPresentations(population.getFittest(0));
		Presentation presentations[]=schedule.getScheduledPresentations();	
		//Write generated schedule to external file
		writeGeneratedSchedule("GeneratedSchedule.csv",presentations,schedule.getTimeslots().size());
		//Print generated schedule
		printSchedule("GeneratedSchedule.csv");
	}
	
	public static void printFinalResult(int generation,int finalFitness) {
		System.out.println();
		System.out.println("Number of generations: "+generation);
		System.out.println("Final solution fitness: "+finalFitness);
		System.out.println("Hard constraints violated: "+Math.abs(finalFitness/1000)+
				"\tSoft constraints violated: "+Math.abs(finalFitness%1000)/10);	
	}
	
	public static void printSchedule(String fileName) {
		try {
			Scanner scn=new Scanner(new File(fileName));
			System.out.print("Day\tVenue\t");
			int time=900;
			while(time<1730) {
				if(time==1300||time==1330) {
					time+=30;
					if(time%100==60) {
						time=time+100-60;
					}
					continue;
				}
				System.out.print(time+"\t");
				time+=30;
				if(time%100==60) {
					time=time+100-60;
				}
			}
			System.out.println();
			String[] days={"Mon","Tues","Wed","Thurs","Fri"};
			for(int i=0;i<days.length;i++) {
				System.out.print(days[i]+"\t");
				String[] locs= {"VR","MR","IR","BJIM"};
				for(int j=0;j<locs.length;j++) {
					if(j>0) System.out.print("\t");
					System.out.print(locs[j]+"\t");
					String line[]=scn.nextLine().split(",");
					for(int k=0;k<line.length;k++) {
						System.out.print(line[k]+"\t");
					}
					System.out.println();
				
				}
				System.out.println();		
			}
			scn.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeGeneratedSchedule(String fileName,Presentation[] presentations, int totalTimeslot) {
		try {
			PrintWriter writer=new PrintWriter(new File(fileName));
			StringBuilder line=new StringBuilder();
			for(int timeSlot=1;timeSlot<=totalTimeslot;timeSlot++) {
				boolean found=false;
				for(int j=0;j<presentations.length;j++) {
					if(timeSlot==presentations[j].getTimeslotID()) {
						line.append("P"+presentations[j].getPresentationID());
						found=true;
						break;
					}
				}
				if(!found) {
					line.append("null");
				}
				if(timeSlot%15==0) {
					line.append("\n");
				}else {
					line.append(",");
				}
			}
			writer.write(line.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Initialize the schedule based on the external files and information listed
	public static Schedule initializeSchedule() {
		Schedule schedule = new Schedule();
		createVenue(schedule);
		createTimeslot(schedule);
		readPresentationsFromCSV("SupExaAssign.csv",schedule);
		readUnavailableVenue("HC03.csv",schedule);
		readUnavailableStaff("HC04.csv",schedule);
		readStaffConsecutive("SC01.csv",schedule);
		readNumOfDays("SC02.csv",schedule);
		readChangeOfVenue("SC03.csv",schedule);
		return schedule;
	}
	
	//Read and load from file that contains the staff preference to attend consecutive presentations
	public static void readStaffConsecutive(String fileName,Schedule schedule) {
		File file = new File(fileName);
		try {
			Scanner scn = new Scanner(file);
			while(scn.hasNextLine()) {
				String line[]=scn.nextLine().split(",");
				String staffID=line[0];
				int consecutiveDay=Integer.parseInt(line[1]);
				schedule.getStaff(staffID).setPresentationCons(consecutiveDay);	
			}
			scn.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName+" not found.");
			e.printStackTrace();
		}
	}
	
	//Read and load from file that contains the staff preference of number of days of presentation
	public static void readNumOfDays(String fileName,Schedule schedule) {
		File file = new File(fileName);
		try {
			Scanner scn = new Scanner(file);
			while(scn.hasNextLine()) {
				String line[]=scn.nextLine().split(",");
				String staffID=line[0];
				int numOfDays=Integer.parseInt(line[1]);
				schedule.getStaff(staffID).setPresentationDays(numOfDays);	
			}
			scn.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName+" not found.");
			e.printStackTrace();
		}
	}
	
	//Read and load from file that contains the staff preference to attend consecutive presentation
	//at the same venue
	public static void readChangeOfVenue(String fileName,Schedule schedule) {
		File file = new File(fileName);
		try {
			Scanner scn = new Scanner(file);
			while(scn.hasNextLine()) {
				String line[]=scn.nextLine().split(",");
				String staffID=line[0];
				String change=line[1];
				if(change.equals("yes")) {
					schedule.getStaff(staffID).setChangeVenue(true);
				}else {
					schedule.getStaff(staffID).setChangeVenue(false);
				}
			}
			scn.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName+" not found.");
			e.printStackTrace();
		}
	}
	
	//Read and load from file that contains the information of the presentations
	public static void readPresentationsFromCSV(String fileName, Schedule schedule) {
		File file=new File(fileName);
		try {
			Scanner scn = new Scanner(file);
			int presentationID=1;
			boolean skip=true;
			while(scn.hasNextLine()) {
				if(skip) {
					scn.nextLine();
					skip=false;
					continue;
				}
				String line[] = scn.nextLine().split(",");			
				String staffsID[]=new String[3];
				for(int i=1;i<line.length;i++) {
					staffsID[i-1]=line[i];
					if(!schedule.getStaffs().containsKey(line[i])) {
						schedule.addStaff(line[i]);
					}
				}
				schedule.addPresentation(presentationID, staffsID);
				presentationID++;
			}
			scn.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName+" not found.");
			e.printStackTrace();
		}

	}
	
	//Read and load from file that contains the information of the unavailability of staffs
	public static void readUnavailableStaff(String fileName,Schedule schedule) {
		File file = new File(fileName);
		try {
			Scanner scn = new Scanner(file);
			while(scn.hasNextLine()) {
				String line[]=scn.nextLine().split(",");
				String staffID=line[0];
				if(!schedule.getStaffs().containsKey(staffID)) {
					schedule.addStaff(staffID);
				}
				if(line.length>1) {
					ArrayList<Integer> unavailableSlots=new ArrayList<>();
					for(int i=1;i<line.length;i++) {
						unavailableSlots.add(Integer.parseInt(line[i]));
					}
					schedule.getStaff(staffID).loadTimeslot(unavailableSlots);
				}
			}
			scn.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName+" not found.");
			e.printStackTrace();
		}

	}
	
	//Read and load from file that contains the information on the unavailability of venue
	public static void readUnavailableVenue(String fileName,Schedule schedule) {
		File file = new File(fileName);
		try {
			Scanner scn = new Scanner(file);
			int venueID=1;
			while(scn.hasNextLine()) {
				String line[]=scn.nextLine().split(",");
				ArrayList<Integer> unavailableVenue=new ArrayList<>();
				for(int i=1;i<line.length;i++) {
					unavailableVenue.add(Integer.parseInt(line[i]));
				}
				schedule.getVenue(venueID).loadTimeslot(unavailableVenue);
				venueID++;
			}
			scn.close();
		} catch (FileNotFoundException e) {
			System.out.println(fileName+" not found.");
			e.printStackTrace();
		}

	}
	
	//Create all venue available
	public static void createVenue(Schedule schedule) {
		schedule.addVenue(1, "Viva Room, Level 7");
		schedule.addVenue(2, "Meeting Room, Level 7");
		schedule.addVenue(3, "Interaction Room, Level 7");
		schedule.addVenue(4, "BJIM Discussion Room, Level 5");
	}
	
	//Create all time slot available
	public static void createTimeslot(Schedule schedule) {
		String date[]=new String[] {"Monday","Tuesday","Wednesday","Thursday","Friday"};
		String time[]=new String[] {
				"0900-0930","0930-1000","1000-1030","1030-1100","1100-1130",
				"1130-1200","1200-1230","1230-1300","1400-1430","1430-1500",
				"1500-1530","1530-1600","1600-1630","1630-1700","1700-1730"
		};
		int venueID=1;
		int timeslotID=1;
		for(int i=0;i<300;i++) {
			String timeslot=date[(i)/60]+" "+time[(i)%15];
			schedule.addTimeslot(timeslotID, timeslot, venueID);
			timeslotID++;
			venueID=(((i+1)/15)%4)+1;
		}
	}
}
