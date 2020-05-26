public class Presentation {
	private int presentationID;
	private String staffID[];
	private int timeslotID;
	
	public Presentation(int presentationID,String staffID[]) {
		this.presentationID=presentationID;
		this.staffID=staffID;
	}
	
	public int getPresentationID() {
		return presentationID;
	}
	
	public String[] getStaffID() {
		return staffID;
	}
	
	public void setTimeslotID(int timeslotID) {
		this.timeslotID=timeslotID;
	}
	
	public int getTimeslotID() {
		return timeslotID;
	}

}
