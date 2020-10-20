package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Posities {
    private HashMap<String, Integer> posities = new HashMap<>();

    public Set<Map.Entry<String, Integer>> getPosities() {
        return posities.entrySet();
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

    public void slaOp(FileWriter writer) throws Exception {
        System.out.println("orders ->sla de orders op");
        try {
            for (Map.Entry<String, Integer> entry : getPosities()) {
                String ticker = entry.getKey();
                int aantal = entry.getValue();

                String sPositie = "POSITIE," + ticker + ", " + aantal;
                writer.write(sPositie + "\n");
            }
        } catch (Exception e) {
            throw new Exception("Exception in slaOp() order:" + e.getLocalizedMessage());
        }
    }
}
