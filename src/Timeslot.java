
public class Timeslot {
	private int timeslotID;
	private int venueID;
	private String timeslot;
	
	public Timeslot(int timeslotID, String timeslot, int venueID) {
		this.timeslotID=timeslotID;
		this.timeslot=timeslot;
		this.venueID=venueID;
	}
	
	public int getTimeslotID() {
		return timeslotID;
	}
	
	public String getTimeslot() {
		return timeslot;
	}
	
	public int getVenueID() {
		return venueID;
	}
}
