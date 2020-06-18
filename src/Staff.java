import java.util.ArrayList;
import java.util.Collections;
public class Staff {
	private String staffID;
	private ArrayList<Integer> unavailableSlots;
	private ArrayList<Integer> currentSlots;
	private boolean changeVenue;
	private int presentationCons;
	private int presentationDays;
	
	public Staff(String staffID) {
		this.staffID=staffID;
		unavailableSlots=new ArrayList<>();
		currentSlots=new ArrayList<>();
	}
	
	//reset the current slots of the staff
	public void resetCurrentSlot() {
		currentSlots=new ArrayList<>();
	}
	
	//add a new time slot to the current slots of the staff
	public void addSlot(int timeSlotID) {
		currentSlots.add(timeSlotID);
	}
	
	public ArrayList<Integer> getSlots(){
		return currentSlots;
	}
	
	//return the number days of presentations of the staff
	public int getNumOfPresentationDays() {
		int numOfPresentationDays=0;
		boolean workingDays[]=new boolean[] 
				{false,false,false,false,false};
		Collections.sort(currentSlots);
		for(int timeslotID:currentSlots) {
			int timeslotIndex=(timeslotID-1)/60;
			if(workingDays[timeslotIndex]==false) {
				numOfPresentationDays+=1;
				workingDays[timeslotIndex]=true;
			}
		}
		return numOfPresentationDays;
	}
	
	//return the consecutive presentation slots of the staff
	public int getConsecutiveSlot() {
		int consecutiveCount=0;
		int maxCount=0;
		Collections.sort(currentSlots);
		for(int timeslotID:currentSlots) {
			if(consecutiveCount==0) {
				consecutiveCount=1;
			}
			int timeslotIndex=timeslotID-1;
			int day=timeslotIndex/60;
			int column=timeslotIndex%15;
			for(int tempTimeslotID:currentSlots) {
					int tempTimeslotIndex=tempTimeslotID-1;
					int tempDay=tempTimeslotIndex/60;
					int tempColumn=tempTimeslotIndex%15;
					if(day==tempDay && column+1==tempColumn) {
						consecutiveCount++;
					}
			}
			if(consecutiveCount>maxCount) {
				maxCount=consecutiveCount;
			}
			consecutiveCount=0;
		}
		return maxCount;
	}
	
	//check if the staff changed venue during consecutive presentations
	public boolean venueChanged() {
		Collections.sort(currentSlots);
		for(int currentTimeslot:currentSlots) {
			int currentIndex=currentTimeslot-1;
			int day=currentIndex/60;
			int columnIndex=currentIndex%15;
			for(int tempTimeslot:currentSlots) {
					int tempIndex=tempTimeslot-1;
					int tempDay=tempIndex/60;
					int tempColumnIndex=tempIndex%15;
					if(day==tempDay && (columnIndex+1)==tempColumnIndex) { 
						if(tempIndex/15!=currentIndex/15) {
							return true;
						}
					}
			}
		}
		return false;
	}
	
	//load the unavailable time slot of the staff
	public void loadTimeslot(ArrayList<Integer> unavailableSlot) {
		this.unavailableSlots=unavailableSlot;
	}
	
	//get the unavailable time slot of the staff
	public ArrayList<Integer> getUnavailableSlots(){
		return unavailableSlots;
	}
	
	public String getStaffID() {
		return staffID;
	}
	
	//check if the staff is unavailable at the specified time slot
	public boolean isUnavailable(int timeslotID) {
		for(int i=0;i<unavailableSlots.size();i++) {
			if(unavailableSlots.get(i)==timeslotID) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isChangeVenue() {
		return changeVenue;
	}
	
	public void setChangeVenue(boolean changeVenue) {
		this.changeVenue = changeVenue;
	}

	public int getPresentationCons() {
		return presentationCons;
	}

	public void setPresentationCons(int presentationCons) {
		this.presentationCons = presentationCons;
	}

	public int getPresentationDays() {
		return presentationDays;
	}

	public void setPresentationDays(int presentationDays) {
		this.presentationDays = presentationDays;
	}
}
