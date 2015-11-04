package simulation;

import java.util.Set;

import models.Equity;

/**
 * 
 * @author Drew Heintz
 *
 */
public class CombinedStrategy implements SimulationStrategy {

    private double annumPercent;

    /**
     * Constructor for the BullMarket Strategy.
     * 
     * @param annumPercent
     *            - growth percent for the next year (negative for decline)
     */
    public CombinedStrategy(double annumPercent) {
        this.annumPercent = annumPercent;
    }

    @Override
    public void step(Set<Equity> equities, int days) {
        // Calculate the growth rate for this step
        double growth = 1.0 + (annumPercent * days / TimeInterval.Year
                .getDays());
        for (Equity eq : equities) {
            eq.setCurrentPrice(eq.getCurrentPrice() * growth);
        }
    }
}
