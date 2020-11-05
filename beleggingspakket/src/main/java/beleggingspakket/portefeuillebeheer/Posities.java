package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Posities {
    private HashMap<String, Positie> positieAantalMap = new HashMap<>();
    public Set<Map.Entry<String, Positie>> getPosities() {
        return positieAantalMap.entrySet();
    }

    // voeg aantal toe aan nieuwe positie als die nog niet bestaat
    public void addToPositie(
            String instrumentnaam,
            int aantal,
            double laatsteKoers,
            boolean isOptie,
            int contractgrootte) {
        if (!positieAantalMap.containsKey(instrumentnaam)) {
           Positie pos = new Positie(instrumentnaam, !isOptie, contractgrootte);
           pos.setPOS(aantal);
           pos.setHuidigeKoers(laatsteKoers);

           positieAantalMap.put(instrumentnaam, pos);
        } else {
            Positie pos = positieAantalMap.get(instrumentnaam);
            Integer totAantal = pos.getPOS() + aantal;
            pos.setPOS(totAantal);
            pos.setHuidigeKoers(laatsteKoers);
            positieAantalMap.replace(instrumentnaam, pos);
        }
    }

    public void addToPositie(Transaction optieTransactie) {
        addToPositie(optieTransactie.getInstrumentname(),
                optieTransactie.getNrOfItems(),
                optieTransactie.getPrice(),
                true,
                optieTransactie.getContractgrootte()
        );
    }

    public void addToPositie(Positie pos) {
        addToPositie(
                pos.getInstrumentnaam(),
                pos.getPOS(),
                pos.getHuidigeKoers(),
                !pos.getIsAandeel(),
                pos.getContractgrootte());
    }

    public PositieDTO geefPositieDTO(String aTicker, double aKoers) {
        int aantal = positieAantalMap.get(aTicker).getPOS();
        double waarde = aKoers * aantal;
        PositieDTO dto = new PositieDTO(
                aTicker,
                Integer.toString(aantal),
                Util.toCurrency(aKoers),
                Util.toCurrency(waarde));
        return dto;
    }

    public void slaPositiesOpNaarDisk(FileWriter writer) throws Exception {
        System.out.println("posities opslaan op schijf");
        try {
            for (Map.Entry<String, Positie> entry : getPosities()) {
               Positie pos = entry.getValue();
               String sPositie = pos.toString();
                writer.write(sPositie + "\n");
            }
        } catch (Exception e) {
            throw new Exception("Exception in slaOp() order:" + e.getLocalizedMessage());
        }
    }

    // POSITIE, <instrumentnaam>, <instrumentsoort>, aantal, huidige koers
    // POSITIE, ING, AANDEEL, 10, 6.25
    // POSITIE, C PHILIPS 1/2021 40.5, OPTIE, -1, 22.95
    public void addPositionLineFromDisk(String line) throws Exception {
        try {
            System.out.println("aanmaken positie via string:" + line);
            Positie pos = Positie.maakPositie(line);
            addToPositie(pos.getInstrumentnaam(), pos.getPOS(), pos.getHuidigeKoers(),
                    !pos.getIsAandeel(), pos.getContractgrootte());
        } catch (Exception e) {
            throw new Exception("addPositionLineFromDisk():" + line + "+" + e.getLocalizedMessage());
        }

    }

    public void deletePositions() {
        positieAantalMap.clear();
    }

}
