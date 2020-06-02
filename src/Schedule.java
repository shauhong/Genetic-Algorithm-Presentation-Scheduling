import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Schedule {
	private HashMap<Integer, Venue> venues;
	private HashMap<String, Staff> staffs;
	private HashMap<Integer, Presentation> presentations;
	private HashMap<Integer, Timeslot> timeslots;
	private int numPresentations=0;
	private Presentation scheduledPresentations[];
	
	public Schedule() {
		this.venues=new HashMap<>();
		this.staffs=new HashMap<>();
		this.timeslots=new HashMap<>();
		this.presentations=new HashMap<>();
	}
	
	public Schedule(Schedule cloneable) {
		this.venues=cloneable.getVenues();
		this.staffs=cloneable.getStaffs();
		this.timeslots=cloneable.getTimeslots();
		this.presentations=cloneable.getPresentations();
	}

	public HashMap<Integer, Venue> getVenues() {
		return venues;
	}

	public HashMap<String, Staff> getStaffs() {
		return staffs;
	}
	public HashMap<Integer, Presentation> getPresentations(){
		return presentations;
	}
	
	public Presentation[] getScheduledPresentations() {
		return scheduledPresentations;
	}

	public HashMap<Integer, Timeslot> getTimeslots() {
		return timeslots;
	}
	
	public void addVenue(int venueID,String venueName) {
		this.venues.put(venueID,new Venue(venueID,venueName));
	}
	
	public void addStaff(String staffID) {
		this.staffs.put(staffID, new Staff(staffID));
	}
	
	public void addTimeslot(int timeslotID,String timeslot, int venueID) {
		this.timeslots.put(timeslotID, new Timeslot(timeslotID,timeslot,venueID));
	}
	
	public void addPresentation(int presentationID,String[] staffID) {
		this.presentations.put(presentationID, new Presentation(presentationID,staffID));
	}
	
	public Venue getVenue(int venueID) {
		return venues.get(venueID);
	}
	
	public Staff getStaff(String staffID) {
		return staffs.get(staffID);
	}
	
	public Presentation getPresentation(int presentationID) {
		return presentations.get(presentationID);
	}
	
	public Timeslot getTimeSlot(int timeslotID) {
		return timeslots.get(timeslotID);
	}
	
	public Timeslot getRandomTimeslot() {
		Object[] timeslotArray = timeslots.values().toArray();
		Timeslot timeslot = (Timeslot)timeslotArray[(int) (timeslotArray.length * Math.random())]; 
		return timeslot;
	}
	
	public int getNumPresentations() {
		this.numPresentations= presentations.size();
		return numPresentations;
	}
	
	public void createScheduledPresentations(Individual individual) {
		Presentation scheduledPresentations[]=new Presentation[this.getNumPresentations()];
		int chromosome[]=individual.getChromosome();
		int chromosomeIndex=0;
		int presentationIndex=0;
		for(int i=0;i<numPresentations;i++) {
			scheduledPresentations[presentationIndex]=presentations.get(presentationIndex+1);
			scheduledPresentations[presentationIndex].setTimeslotID(chromosome[chromosomeIndex]);
			String staffsID[]=scheduledPresentations[presentationIndex].getStaffID();
			for(int j=0;j<staffsID.length;j++) {
				Staff tempStaff=staffs.get(staffsID[j]);				
				tempStaff.addSlot(chromosome[chromosomeIndex]);
			}
			chromosomeIndex++;
			presentationIndex++;
		}
		this.scheduledPresentations=scheduledPresentations;
	}
	
	public int calcPenalty() {
		int penalty=0;
		ArrayList<String> SC01=new ArrayList<>();
		ArrayList<String> SC02=new ArrayList<>();
		ArrayList<String> SC03=new ArrayList<>();
		for(int i=0;i<scheduledPresentations.length;i++) {
			Presentation presentation=scheduledPresentations[i];
			String staffsID[]=presentation.getStaffID();
			int timeslotID=presentation.getTimeslotID();
//			System.out.println("Ts: "+this.getTimeSlot(timeslotID).getVenueID());
//			int venueID=this.getTimeSlot(timeslotID).getVenueID();
			int venueID=this.getTimeSlot(presentation.getTimeslotID()).getVenueID();
			Staff staffs[]=new Staff[staffsID.length];
			Venue venue = this.getVenue(venueID);	
			for(int j=0;j<staffsID.length;j++) {
				staffs[j]=this.getStaff(staffsID[j]);
			}
					
			//Hard constraints
			penalty+=this.samePresentationTime(i, timeslotID);
			penalty+=this.concurrentStaff(i, timeslotID, staffsID);
			penalty+=this.unavailableVenue(venue,timeslotID);
			penalty+=this.unavailableStaff(staffs,timeslotID);
			//Soft constraints
			penalty+=this.consecutivePresentations(SC01, staffs);
			penalty+=this.numOfDays(SC02, staffs);
			penalty+=this.changeOfVenue(SC03, staffs);
		}
		for(String staffID:staffs.keySet()) {
			staffs.get(staffID).resetCurrentSlot();
		}
		
		return penalty;
	}
	
	public int calHardConstraints() {
		int penalty=0;
		return penalty;
	}
	
	public int samePresentationTime(int i, int timeslotID) {
		int penalty=0;
		//HC01
		for(int j=0;j<scheduledPresentations.length;j++) {
			if(j!=i && timeslotID==scheduledPresentations[j].getTimeslotID()) {
//				System.out.println("Scheduled Presentation ID: "+scheduledPresentations[j].getPresentationID());
//				System.out.println("Same presentation");
				penalty+=100;
			}
		}
		return penalty;
	}
	
	public int concurrentStaff(int i,int timeslotID, String[] staffsID) {
		int penalty=0;
		String timeslot = this.getTimeSlot(timeslotID).getTimeslot();
		//HC02
		for(int j=0;j<scheduledPresentations.length;j++) {
			int tempTimeslotID=scheduledPresentations[j].getTimeslotID();
			String tempTimeslot=this.getTimeSlot(tempTimeslotID).getTimeslot();
			if(i!=j && tempTimeslot.equals(timeslot)) {
				String tempStaffsID[]=scheduledPresentations[j].getStaffID();
				for(int k=0;k<tempStaffsID.length;k++) {
					for(int l=0;l<tempStaffsID.length;l++) {
						if(tempStaffsID[k].equals(staffsID[l])) {
//							System.out.println("Concurrent staff: "+tempStaffsID[k]);
							penalty+=100;
						}
					}
				}
			}
		}
		return penalty;
	}
	
	public int unavailableVenue(Venue venue, int timeslotID) {
		int penalty=0;
		//HC03
		ArrayList<Integer> unavailableVenueSlots=venue.getUnavaibleSlots();
		for(int unavailableTimeslotID:unavailableVenueSlots) {
			if(unavailableTimeslotID==timeslotID) {
//				System.out.println(venue.getVenueName()+" unavailable for presentation "
//			+presentationID+" at "+timeslotID);
				penalty+=100;
			}
		}
		return penalty;
	}
	
	public int unavailableStaff(Staff[] staffs, int timeslotID) {
		int penalty=0;
		//HC04
		ArrayList<Integer> unavailableStaffSlots=new ArrayList<>();
		for(int j=0;j<staffs.length;j++) {
			unavailableStaffSlots.addAll(staffs[j].getUnavailableSlots());
		}
		for(int unavailableTimeslotID:unavailableStaffSlots) {
			if(unavailableTimeslotID==timeslotID) {
//				System.out.println("Staff unavailable for presentation "
//						+presentationID+" at "+timeslotID);
				penalty+=100;
			}
		}
		return penalty;
	}
	
	
	public int consecutivePresentations(ArrayList<String> checkedStaff, Staff[] staffs) {
		int penalty=0;
		//SC01
		for(int j=0;j<staffs.length;j++) {
			if(!checkedStaff.contains(staffs[j].getStaffID())) {
				checkedStaff.add(staffs[j].getStaffID());
				if(staffs[j].getConsecutiveSlot()>staffs[j].getPresentationCons()) {
//					System.out.println(staffs[j].getStaffID()+" consecutive presentations exceeded");
					penalty+=1;
				}
			}
		}
		return penalty;
	}
	
	public int numOfDays(ArrayList<String> checkedStaff, Staff[] staffs) {
		int penalty=0;
		//SC02
		for(int j=0;j<staffs.length;j++) {
			if(!checkedStaff.contains(staffs[j].getStaffID())) {
				checkedStaff.add(staffs[j].getStaffID());
				if(staffs[j].getNumOfPresentationDays()>staffs[j].getPresentationDays()) {
//					System.out.println(staffs[j].getStaffID()+" number of days exceeded.");
					penalty+=1;
				}
			}
		}
		return penalty;
	}
	
	public int changeOfVenue(ArrayList<String> checkedStaff,Staff[] staffs) {
		int penalty=0;
		//SC03
		for(int j=0;j<staffs.length;j++) {
			if(!checkedStaff.contains(staffs[j].getStaffID())) {
				checkedStaff.add(staffs[j].getStaffID());
				if(staffs[j].isChangeVenue()) {
					if(staffs[j].venueChanged()) {
//						System.out.println(staffs[j].getStaffID()+" changed venue.");
						penalty+=1;
					}
				}
			}
		}
		return penalty;
	}
	
	public String toString() {
		String str="";
		for(int key:timeslots.keySet()) {
			str+=key;
			str+=" "+timeslots.get(key).getTimeslotID()+" "+timeslots.get(key).getTimeslot()
					+" "+timeslots.get(key).getVenueID()+"\n";
		}
		for(int key:presentations.keySet()) {
			str+=key;
			str+=" "+presentations.get(key).getStaffID()[0]+" "
					+presentations.get(key).getStaffID()[1]+" "
					+presentations.get(key).getStaffID()[2]+"\n";
		}
		return str;
	}
}
