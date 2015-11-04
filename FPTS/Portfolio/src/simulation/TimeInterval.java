package simulation;

/**
 * 
 * @author Drew Heintz
 * 
 */
public enum TimeInterval {
	/**
	 * A single day.
	 */
	Day(1),
	
	/**
	 * One month. Treated as 30 days.
	 */
	Month(30),
	
	/**
	 * One year. Treated as 365 days.
	 */
	Year(365);
	
	
	/**
	 * Number of days this time interval is equivalent to.
	 */
	private int days;
	
	private TimeInterval(int numDays) {
		this.days = numDays;
	}
	
	public int getDays() {
		return days;
	}
}
