package simulation;

/**
 * 
 * @author Drew Heintz
 *
 */
public enum MarketStrategy {
    Bull("Bull Market")
    {
        @Override
        public double calculatePercent(double annumPercent) {
            return Math.abs(annumPercent);
        }
    },
    Bear("Bear Market")
    {
        @Override
        public double calculatePercent(double annumPercent) {
            return -Math.abs(annumPercent);
        }
    },
    NoGrowth("No-Growth Market")
    {
        @Override
        public double calculatePercent(double annumPercent) {
            return 0;
        }
    };
    
    
    private final String nameText;
    
    private MarketStrategy(String nameText) {
        this.nameText = nameText;
    }
    
    public String toString() {
        return nameText;
    }
    
    /**
     * Calculate the growth percent for the strategy from a given annum percent
     * 
     * @param annumPercent - what percent will the market grow/decline
     * @return what percent the market will grow/decline based on the strategy
     */
    public abstract double calculatePercent(double annumPercent);
}
