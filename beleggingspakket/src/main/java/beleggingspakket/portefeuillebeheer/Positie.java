package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

import java.util.ArrayList;

public class Positie {
    private static int PositionSeq = 1000;



    private int seqNr;
    private String instrumentnaam; // ticker van het aandeel (naam van het instrument)
    private boolean isAandeel;     // in geval van aandeel kan huidige koers worden gebruikt
    private int contractgrootte;
    private int POS; // huidige positie
    private double GAK; // gemiddelde aankoopkoers
    private int TA; // totaal aantal aangekocht
    private int TV; // totaal aantal verkocht
    private Double huidigeKoers;

    private double GVK; // gemiddelde verkoopkoers
    private double GWV; // gerealiseerde winst/verlies
    private double OWV; // ongerealiseerde winst/verlies


    public double geefHuidigeWaarde() {
        if (getIsAandeel()) {
            return POS * huidigeKoers;
        } else {
            return POS * huidigeKoers * contractgrootte;
        }
    }

    public int getContractgrootte() {
        return contractgrootte;
    }

    // koers van instrument wordt na het doen van een transactie aangepast via de portefeuillebeheercontroller
    // voor de koers van een aandeel wordt de koershistorie geraadpleegd bij het tonen van de posities op het scherm
    // in de portefeuillecontroller. De koers van een optie wordt handmatig bijgehouden en bijgewerkt na een transactie.
    public void setHuidigeKoers(Double huidigeKoers) {
        this.huidigeKoers = huidigeKoers;
    }

    // positie wordt na het doen van een transactie aangepast via de portefeuillebeheercontroller
    public void setPOS(int POS) {
        this.POS = POS;
    }



    public Double getHuidigeKoers() {
        return huidigeKoers;
    }

    public String getInstrumentnaam() {
        return instrumentnaam;
    }

    public boolean getIsAandeel() {
        return isAandeel;
    }

    public int getPOS() {
        return POS;
    }

    public double getGAK() {
        return GAK;
    }

    public int getTA() {
        return TA;
    }

    public int getTV() {
        return TV;
    }

    public double getGVK() {
        return GVK;
    }

    public double getGWV() {
        return GWV;
    }

    public double getOWV() {
        return OWV;
    }

    public int getSeqNr() {
        return seqNr;
    }

    public Positie(String instrumentnaam, boolean isAandeel, int contractgrootte) {
        this.seqNr = PositionSeq++;
        this.instrumentnaam = instrumentnaam;
        this.isAandeel = isAandeel;
        this.POS = 0;
        this.contractgrootte = contractgrootte;
    }

    public void berekenWinstVerliesInstrument(ArrayList<Transaction> transactions, Double huidigeKoers) {
        this.huidigeKoers = huidigeKoers;
        this.POS = 0;
        this.TA = 0;
        this.TV = 0;
        this.GAK = 0.0;
        this.GVK = 0.0;

        for (Transaction t: transactions) {
            GWV = 0;
            OWV = 0;
            if (t.getInstrumentname().equals(this.instrumentnaam)) {
                if (!t.isSaleOrder()) {
                   POS += t.getNrOfItems();
                   TA  += t.getNrOfItems();
                   GAK += t.getNrOfItems() * t.getPrice();
                } else {
                    POS -= t.getNrOfItems();
                    TV  += t.getNrOfItems();
                    GVK += t.getNrOfItems() * t.getPrice();
                }
            }
        }
        if (TA > 0)
            GAK /= TA;
        if (TV > 0)
            GVK /= TV;
        GWV = TV * (GVK - GAK);
        POS = TA - TV;
        OWV = POS * (huidigeKoers - GAK);
        if (!isAandeel) {
            GWV *= contractgrootte;
            OWV *= contractgrootte;
        }
    } // berekenWinstVerlies

    public String toString() {
        String sPositie = "POSITIE," + instrumentnaam + ", ";
        if (getIsAandeel()) {
            sPositie  += " AANDEEL,";
        } else {
            sPositie += "OPTIE,";
        }
        sPositie += POS + "," + getHuidigeKoers();
        if (!getIsAandeel())
            sPositie += "," + contractgrootte;
        return sPositie;
    }

    public static Positie maakPositie(String line) throws Exception {
        String[] positionelements = line.split(",");
        if (positionelements.length < 5) {
            throw new Exception("5 elementen op regel verwacht");
        }
        String sInstrumentnaam = positionelements[1].trim();
        String sInstrumentsoort = positionelements[2].trim();
        boolean bIsOptie = (sInstrumentsoort.equals("OPTIE"));
        String sAantal = positionelements[3].trim();
        int aantal = Integer.parseInt(sAantal);
        double dHuidigeKoers = Util.toDouble(positionelements[4].trim());
        int contractgrootte = 0;
        if (bIsOptie) {
            if (positionelements.length < 6)
                throw new Exception("bij optietransactie 6 elementen op regel verwacht");
            contractgrootte = Integer.parseInt(positionelements[5].trim());
        }
        Positie pos = new Positie(sInstrumentnaam, !bIsOptie, contractgrootte);
        pos.setHuidigeKoers(dHuidigeKoers);
        return pos;
    }
}
