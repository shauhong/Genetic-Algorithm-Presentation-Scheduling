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
		Collections.sort(currentSlots);
		int startDay=(currentSlots.get(0)-1)/60;
		int lastDay=(currentSlots.get(currentSlots.size()-1)-1)/60;
		numOfPresentationDays=(lastDay-startDay)+1;
		return numOfPresentationDays;
	}
	
	public int getConsecutiveSlot() {
		Collections.sort(currentSlots);
		int consecutiveCount=0;
		int maxCount=0;
		int prevTimeslotID=-1;
		for(int timeslotID:currentSlots) {
			if(consecutiveCount==0) {
				consecutiveCount++;
				if(consecutiveCount>maxCount) {
					maxCount=consecutiveCount;
				}
			}
			else if(prevTimeslotID+1==timeslotID) {
				consecutiveCount++;
				if(consecutiveCount>maxCount) {
					maxCount=consecutiveCount;
				}
			}else {
				consecutiveCount=0;
			}
			prevTimeslotID=timeslotID;
		}
		return consecutiveCount;
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
