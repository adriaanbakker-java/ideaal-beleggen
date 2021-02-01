package beleggingspakket.indicatoren;

/*
momentum indicator is simply the latest closing price minus the
closing price ten days ago.
*/



import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;

import java.util.ArrayList;

public class Momentum extends Indicator {

    private ArrayList<Double> momentumLine;
    private int nrOfDays;

    public Momentum(ArrayList<DayPriceRecord> aDayPriceArray) throws Exception {
        super (aDayPriceArray);
        //DayPriceArray =aDayPriceArray;
        //calcMomentumLine();
    }

    @Override
    public ArrayList<Double> getIndicatorLine() {
        return momentumLine;
    }

    @Override
    protected void initSpecifics() {
        nrOfDays = 10;
    }

    @Override
    protected void calcSignals() {
        signalen = new ArrayList<>();
        for (int i=nrOfDays + 1; i <= myClosingPrices.size()-1; i++) {
            double prev = momentumLine.get(i-1);
            double val = momentumLine.get(i);
            boolean koopsig = ((prev < 0) && (val > 0));
            boolean verkoopsig = ((prev > 0) && (val < 0));

            if ((koopsig) || verkoopsig) {
                DayPriceRecord dpr = myClosingPrices.get(i);
                IDate iDate = new IDate(dpr.getYear(),dpr.getMonth(), dpr.getDay());
                if (koopsig)
                    signalen.add(new IndicatorSignal(iDate, true, dpr, i));
                else
                    signalen.add(new IndicatorSignal(iDate, false, dpr, i));
            }
        }
    }

    @Override
    protected void calcSignalLine() throws Exception {
            // momentum heeft geen signalline
    }

    @Override
    protected void calcIndicator() {
        calcMomentumLine();
    }


    private void calcMomentumLine() {
        double price = 0.0;


        momentumLine = new ArrayList<>();
        DayPriceRecord dpr  = null;
        DayPriceRecord dprPrev  = null;

        for (int i = 0; i<= myClosingPrices.size()-1; i++) {

            if (i <nrOfDays ) {
               price = 0.0;
            } else {
                dpr = myClosingPrices.get(i);
                dprPrev = myClosingPrices.get(i-nrOfDays);
                price = dpr.getClose() - dprPrev.getClose();
            }


            momentumLine.add(price);
        }
    }
}
