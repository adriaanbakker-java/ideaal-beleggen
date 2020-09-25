package beleggingspakket.indicatoren;

/*
momentum indicator is simply the latest closing price minus the
closing price ten days ago.
*/


import beleggingspakket.Koersen.DayPriceRecord;

import java.util.ArrayList;

public class Momentum {
    public ArrayList<Double> MomentumLine () {
        return MomentumLine;
    }

    private ArrayList<Double> MomentumLine = new ArrayList<>();

    private ArrayList<DayPriceRecord> DayPriceArray;

    public Momentum(ArrayList<DayPriceRecord> aDayPriceArray) throws Exception {
        DayPriceArray =aDayPriceArray;
        calcMomentumLine();
    }




    private void calcMomentumLine() {
        double price = 0.0;
        final int nrOfDays = 10;

        DayPriceRecord dpr  = null;
        DayPriceRecord dprPrev  = null;

        for (int i = 0; i<= DayPriceArray.size()-1; i++) {

            if (i <nrOfDays ) {
               price = 0.0;
            } else {
                dpr = DayPriceArray.get(i);
                dprPrev = DayPriceArray.get(i-nrOfDays);
                price = dpr.getClose() - dprPrev.getClose();
            }


            MomentumLine.add(price);
        }
    }
}
