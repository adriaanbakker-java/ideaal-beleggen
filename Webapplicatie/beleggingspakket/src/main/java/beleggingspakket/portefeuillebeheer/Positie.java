package beleggingspakket.portefeuillebeheer;

public class Positie {
    private String ticker;
    private Integer aantal;

    public Positie(String ticker, Integer aantal) {
        this.ticker = ticker;
        this.aantal = aantal;
    }
}
