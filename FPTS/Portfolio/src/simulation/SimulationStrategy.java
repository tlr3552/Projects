package simulation;

import java.util.Set;

import models.Equity;

/**
 * 
 * @author Drew Heintz
 *
 */
public interface SimulationStrategy {

    /**
     * Step the simulation by calculating the growth of a set of equities over a
     * given period of days.
     * 
     * The function will update the value of each equity in which case
     * 
     * 
     * @param equities
     *            - set of equities of calculate growth for
     * @param days
     *            -
     */
    void step(Set<Equity> equities, int days);

}
