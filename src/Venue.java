import java.util.ArrayList;

public class Venue {
	private int venueID;
	private String venueName;
	private ArrayList<Integer> unavailableSlots;
	
	public Venue(int venueID,String venueName) {
		this.venueID=venueID;
		this.venueName=venueName;
		unavailableSlots=new ArrayList<Integer>();
	}
	
	public int getVenueID() {
		return venueID;
	}
	
	public String getVenueName() {
		return venueName;
	}
	
	public void loadTimeslot(ArrayList<Integer> unavailableSlots) {
		this.unavailableSlots=unavailableSlots;
	}
	
	public ArrayList<Integer> getUnavaibleSlots(){
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
}
