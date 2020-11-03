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
            boolean isOptie) {
        if (!positieAantalMap.containsKey(instrumentnaam)) {
           Positie pos = new Positie(instrumentnaam, !isOptie);
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
                true
        );
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

    public void slaOp(FileWriter writer) throws Exception {
        System.out.println("orders ->sla de orders op");
        try {
            for (Map.Entry<String, Positie> entry : getPosities()) {
                String instrumentnaam = entry.getKey();
                Positie pos = entry.getValue();
                int aantal = pos.getPOS();

                String sPositie = "POSITIE," + instrumentnaam + ", ";
                if (pos.isAandeel()) {
                    sPositie  += " AANDEEL,";
                } else {
                    sPositie += "OPTIE,";
                }
                sPositie += aantal + "," + pos.getHuidigeKoers();
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
            String[] positionelements = line.split(",");
            if (positionelements.length < 5) {
                throw new Exception("5 elementen op regel verwacht");
            }
            String sInstrumentnaam = positionelements[1].trim();
            String sInstrumentsoort = positionelements[2].trim();
            boolean bIsOptie = (sInstrumentsoort.equals("OPTIE"));
            String sAantal = positionelements[3].trim();
            double dHuidigeKoers = Util.toDouble(positionelements[4].trim());
            int aantal = Integer.parseInt(sAantal);
            addToPositie(sInstrumentnaam, aantal, dHuidigeKoers, bIsOptie);
        } catch (Exception e) {
            throw new Exception("addPositionLineFromDisk():" + line + "+" + e.getLocalizedMessage());
        }

    }

    public void deletePositions() {
        positieAantalMap.clear();
    }

}
