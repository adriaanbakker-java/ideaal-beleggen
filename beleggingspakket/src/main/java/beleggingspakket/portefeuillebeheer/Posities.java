package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

import java.io.FileWriter;
import java.util.*;

public class Posities {
    private HashMap<String, Positie> positieAantalMap = new HashMap<>();
    public Set<Map.Entry<String, Positie>> getPosities() {
        return positieAantalMap.entrySet();
    }


    public void pasKoersAan(int volgnr, double nieuweKoers) throws Exception {
        boolean found = false;
        for (Positie pos: positieAantalMap.values()) {
            if (pos.getSeqNr() == volgnr) {
                pos.setHuidigeKoers(nieuweKoers);
                found = true;
            }
        }
        if (!found)
            throw new Exception("positie met volgnr " + volgnr + " niet gevonden");
    }
    // Voeg een nieuwe positie toe
    // (wordt eigenlijk geacht nog niet te bestaan)
    private void addToPosities(Positie aPositie) {
        String instrumentnaam = aPositie.getInstrumentnaam();
        if (!positieAantalMap.containsKey(instrumentnaam)) {
            positieAantalMap.put(instrumentnaam, aPositie);
        } else {
            System.out.println("waarschuwing - positie hoort nog niet te bestaan in addToPosities()");
            Positie pos = positieAantalMap.get(instrumentnaam);
            Integer totAantal = pos.getPOS() + aPositie.getPOS();
            pos.setPOS(totAantal);
            pos.setHuidigeKoers(aPositie.getHuidigeKoers());
            positieAantalMap.replace(instrumentnaam, pos);
        }
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

    public void addToPositie(Transaction aTransactie) {
        addToPositie(aTransactie.getInstrumentname(),
                aTransactie.getNrOfItems(),
                aTransactie.getPrice(),
                aTransactie.getIsOptieTransactie(),
                aTransactie.getContractgrootte()
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
            /*addToPositie(pos.getInstrumentnaam(), pos.getPOS(), pos.getHuidigeKoers(),
                    !pos.getIsAandeel(), pos.getContractgrootte());*/
            addToPosities(pos);
        } catch (Exception e) {
            throw new Exception("addPositionLineFromDisk():" + line + "+" + e.getLocalizedMessage());
        }

    }

    public void deletePositions() {
        positieAantalMap.clear();
    }

}
