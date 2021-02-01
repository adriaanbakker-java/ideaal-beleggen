package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.Util;

import java.util.ArrayList;

public class StatistiekUitkomst {
    public int getAantalGebeurtenissen() {
        return aantalGebeurtenissen;
    }

    public void incAantalGebeurtenissen() {
        this.aantalGebeurtenissen++;
    }

    private int aantalGebeurtenissen;

    private int Kh; // aantal keren  koers "higher" dan k0 + delta% na n dagen
    private int Kl; // aantal keren koers "lower" dan k0 - delta% na n dagen

    StatistiekUitkomst() {
        aantalGebeurtenissen = 0;
        Kh = 0;
        Kl = 0;
    }

    public void incHigher() {
        Kh++;
    }
    public void incLower() {
        Kl++;
    }

    public String print() {
        double kanshoger = (1.0*Kh)/aantalGebeurtenissen;
        double kanslager = (1.0)*Kl/aantalGebeurtenissen;
        String result =
                " aantal hoger"  + Kh  + " " +
                " aantal lager:"  + Kl  + " " +
                " kans op hoger:" + Util.toCurrency( kanshoger)  + " " +
                " kans op lager:" + Util.toCurrency(kanslager) + "\n"
                ;
        return  result;
    }
}
