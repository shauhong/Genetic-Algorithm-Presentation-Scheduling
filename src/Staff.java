import java.util.ArrayList;
import java.util.Collections;
public class Staff {
	private String staffID;
	private ArrayList<Integer> unavailableSlots;
	private ArrayList<Integer> currentSlots;
	private boolean changeVenue;
	private int presentationCons;
	private int presentationDays;
	private int currentCons;
	private boolean exceedCons;
	
	public Staff(String staffID) {
		this.staffID=staffID;
		unavailableSlots=new ArrayList<>();
		currentSlots=new ArrayList<>();
		currentCons=0;
		exceedCons=false;
	}
	
	public void resetCurrentSlot() {
		currentSlots=new ArrayList<>();
	}
	
	public void addSlot(int timeSlotID) {
		currentSlots.add(timeSlotID);
	}
	
	public ArrayList<Integer> getSlots(){
		return currentSlots;
	}
	
	public boolean isChangeVenue() {
		return changeVenue;
	}
	
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
	
	public boolean venueChanged() {
		//SC03
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
	
	
	public void loadTimeslot(ArrayList<Integer> unavailableSlot) {
		this.unavailableSlots=unavailableSlot;
	}
	
	public void setCurrentCons(int currentCons) {
		this.currentCons=currentCons;
	}
	
	public int getCurrentCons() {
		return currentCons;
	}
	
	public void setExceedCons(boolean exceedCons) {
		this.exceedCons=exceedCons;
	}
	
	public boolean isExceedCons() {
		return exceedCons;
	}
	public String getStaffID() {
		return staffID;
	}
	
	public ArrayList<Integer> getUnavailableSlots(){
		return unavailableSlots;
	}
	
	public boolean isUnavailable(int timeslotID) {
		for(int i=0;i<unavailableSlots.size();i++) {
			if(unavailableSlots.get(i)==timeslotID) {
				return true;
			}
		}
		return false;
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
