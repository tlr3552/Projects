package simulation;

import java.util.Map;

import models.Equity;
import models.Holding;

/**
 * 
 * @author Drew Heintz
 *
 */
public class SimulationDriver {

    /**
     * 
     */
    private SimulationStrategy strategy;

    /**
     * 
     */
    private Map<Equity, Holding> holdings;

    /**
     * Time interval by which to step the simulation.
     */
    private TimeInterval interval;
    
    /**
     * The number of steps to go through.
     */
    private int maxSteps;
    
    /**
     * The current step we are on.
     */
    private int currentStep;

    /**
     * Create a new SimulationDriver with the given strategy and holdings.
     * 
     * Note that the equities in the holdings map will be changed so make sure
     * they are not referenced elsewhere or you can make a copy for this map.
     * 
     * @param strategy
     *            - simulation strategy to use
     * @param holdings
     *            - holdings to run the strategy on
     * @param interval
     *            - the interval to step through
     */
    public SimulationDriver(SimulationStrategy strategy,
            Map<Equity, Holding> holdings, TimeInterval interval, int numSteps) {
        this.strategy = strategy;
        this.holdings = holdings;
        this.interval = interval;
        this.maxSteps = numSteps;
    }

    public void runToEnd() {
        while(step());
    }

    /**
     * Step the simulation once.
     * 
     * @return true if we are still stepping, false if we've reached the max number of steps
     */
    public boolean step() {
        strategy.step(holdings.keySet(), interval.getDays());
        currentStep++;
        return currentStep < maxSteps;
    }
}
