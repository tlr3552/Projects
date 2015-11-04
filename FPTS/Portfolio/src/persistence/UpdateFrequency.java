package persistence;

public enum UpdateFrequency {
	/**
	 * One Minute
	 */
	One(60),
	
	/**
	 * Five Minutes
	 */
	Five(300),
	
	/**
	 * Ten Minutes
	 */
	Ten(600),
	
	/**
	 * Thirty Minutes
	 */
	Thirty(1800),
	
	/**
	 * Sixty Minutes
	 */
	Sixty(3600);
	
	
	/**
	 * Number of days this time interval is equivalent to.
	 */
	private int seconds;
	
	private UpdateFrequency(int numSeconds) {
		this.seconds = numSeconds;
	}
	
	public int getSeconds(){
		return seconds;
	}
}
