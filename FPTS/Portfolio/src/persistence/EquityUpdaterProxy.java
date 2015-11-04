package persistence;

import java.time.LocalTime;

/**
 * Proxy for the RealEquityUpdater - only updates the data
 * if a certain amount of time has passed since the last update
 * 
 * @author Bill Dybas
 *
 */
public class EquityUpdaterProxy implements EquityUpdater{
	private RealEquityUpdater reu;
	private LocalTime lastUpdate;
	private int updateInterval;

	public EquityUpdaterProxy(){
		this(UpdateFrequency.Five.getSeconds()); 
	}
	
	public EquityUpdaterProxy(int seconds){
		this.updateInterval = seconds;
		this.lastUpdate = LocalTime.now();
	}
	
	@Override
	public void update() {
		// Check if enough time has passed
		if(LocalTime.now().minusSeconds(updateInterval).compareTo(lastUpdate) == 1){
			// Check if an updater has been made
			if(this.reu == null){
				this.reu = new RealEquityUpdater();
			}
			this.reu.update();
		}
		else{
			return;
		}
	}
	
	public void setUpdateInterval(int seconds){
		this.updateInterval = seconds;
	}

}
