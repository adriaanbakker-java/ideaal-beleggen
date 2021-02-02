package beleggingspakket.portefeuillebeheer;

import beleggingspakket.util.IDate;
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
    private ArrayList<IDate> gebHigher;
    private ArrayList<IDate> gebLower;

    private int Kh; // aantal keren  koers "higher" dan k0 + delta% na n dagen
    private int Kl; // aantal keren koers "lower" dan k0 - delta% na n dagen

    StatistiekUitkomst() {
        aantalGebeurtenissen = 0;
        Kh = 0;
        Kl = 0;
        gebHigher = new ArrayList<>();
        gebLower = new ArrayList<>();
    }

    public void incHigher(IDate iDate) {
        Kh++;
        gebHigher.add(iDate);
    }
    public void incLower(IDate iDate) {
        Kl++;
        gebLower.add(iDate);
    }

    private String printDatelist(String aLabel, ArrayList<IDate> arr) {
        String sResult = aLabel + "\n";
        for (int i=0; i<=arr.size()-1; i++) {
            sResult = sResult + arr.get(i);
            if (i< arr.size()-1) {
                sResult += ",";
            }
            if (i%10 == 0) {
                sResult += "\n";
            }
        }
        return sResult;
    }

    public String printDates() {
        String result = printDatelist("Dates higher:", gebHigher);
        result += "\n\n";
        result += printDatelist("Dates lower:", gebLower);
        return result;
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
