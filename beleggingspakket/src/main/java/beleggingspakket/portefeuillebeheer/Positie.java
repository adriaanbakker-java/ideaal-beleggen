package beleggingspakket.portefeuillebeheer;

import java.util.ArrayList;

public class Positie {
    private String instrumentnaam; // ticker van het aandeel (naam van het instrument)
    private boolean isAandeel;     // in geval van aandeel kan huidige koers worden gebruikt
    private int POS; // huidige positie
    private double GAK; // gemiddelde aankoopkoers
    private int TA; // totaal aantal aangekocht
    private int TV; // totaal aantal verkocht
    private Double huidigeKoers;

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

    public boolean isAandeel() {
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

    private double GVK; // gemiddelde verkoopkoers
    private double GWV; // gerealiseerde winst/verlies
    private double OWV; // ongerealiseerde winst/verlies

    public Positie(String instrumentnaam, boolean isAandeel) {
        this.instrumentnaam = instrumentnaam;
        this.isAandeel = isAandeel;
        this.POS = 0;
    }

    public void berekenWinstVerliesInstrument(ArrayList<Transaction> transactions, Double huidigeKoers) {
        this.huidigeKoers = huidigeKoers;
        this.POS = 0;
        this.TA = 0;
        this.TV = 0;
        this.GAK = 0.0;
        this.GVK = 0.0;

        for (Transaction t: transactions) {
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
    } // berekenWinstVerlies
}
