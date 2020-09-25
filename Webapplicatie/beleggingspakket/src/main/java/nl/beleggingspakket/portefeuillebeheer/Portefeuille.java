package nl.beleggingspakket.portefeuillebeheer;

import nl.beleggingspakket.util.Util;

import java.util.HashMap;

public class Portefeuille {
    public HashMap<String, Integer> getPosities() {
        return posities;
    }

    private HashMap<String, Integer> posities = new HashMap<>();

    public Portefeuille() {
    }

    public void addToPositie(String ticker, int aantal) {
        if (!posities.containsKey(ticker)) {
            posities.put(ticker, aantal);
        } else {
            Integer totAantal = posities.get(ticker) + aantal;
            posities.replace(ticker, totAantal);
        }
    }

    public PositieDTO geefPositieDTO(String aTicker, double aKoers) {
        int aantal = posities.get(aTicker).intValue();
        double waarde = aKoers * aantal;
        PositieDTO dto = new PositieDTO(
                aTicker,
                Integer.toString(aantal),
                Util.toCurrency(aKoers),
                Util.toCurrency(waarde));
        return dto;
    }


}
