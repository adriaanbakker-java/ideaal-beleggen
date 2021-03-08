package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;

import java.util.ArrayList;

public class MACD_corr extends MACD {

    public MACD_corr(ArrayList<DayPriceRecord> aDayPriceArray, boolean aCorrBehaviour) throws Exception {
        super(aDayPriceArray, aCorrBehaviour);
    }


    // perioden met negatieve waarden van de MACD indicator zijn uitgesloten
    // van koopsignalen.Het passeren van de nulgrens wordt gezien als een koop-
    // dan wel verkoopsignaal. Let op: zo kan het voorkomen dat er meerdere
    // koopsignalen dan wel verkoopsignalen elkaar volgen.
    protected void calcSignals() {

        signalen = new ArrayList<>();
        for (int i=0; i <= myClosingPrices.size()-1; i++) {
            if (i >= super.slowDays ) {
                boolean s1 =   MACDlist.get(i-1) < MACDSmoothed.get(i-2);
                boolean s2 =   MACDlist.get(i) > MACDSmoothed.get(i);
                boolean s3 =   MACDlist.get(i-1) > MACDSmoothed.get(i-2);
                boolean s4 =   MACDlist.get(i) < MACDSmoothed.get(i);
                boolean koopsig = s1 && s2;
                boolean verkoopsig = s3 && s4;

                if (this.corrBehaviour) {
                    koopsig = s1 && s2 && (MACDlist.get(i) > 0);
                    verkoopsig = s3 && s4 && (MACDlist.get(i) > 0);
                    if ((MACDlist.get(i-1) < 0) && (MACDlist.get(i) >= 0)) {
                        koopsig = true;
                        verkoopsig = false;
                    }
                    if ((MACDlist.get(i-1) > 0) && (MACDlist.get(i) <= 0)) {
                        koopsig = false;
                        verkoopsig = true;
                    }
                }


                if ((koopsig) || verkoopsig) {
                    DayPriceRecord dpr = myClosingPrices.get(i);
                    IDate iDate = new IDate(dpr.getYear(),dpr.getMonth(), dpr.getDay());
                    IndicatorSignal s;
                    if (koopsig) {
                        s = new IndicatorSignal(iDate, true, dpr, i);
                        s.setIndicatorWaarde(MACDlist.get(i));
                        signalen.add(s);
                    } else {
                        s = new IndicatorSignal(iDate, false, dpr, i);
                        s.setIndicatorWaarde(MACDlist.get(i));
                        signalen.add(s);
                    }
                }
            }
        }

    }
}
