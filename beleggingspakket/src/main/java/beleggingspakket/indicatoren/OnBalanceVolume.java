package beleggingspakket.indicatoren;


import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;

import java.util.ArrayList;

public class OnBalanceVolume extends Indicator {
    private int AVGCNT;

    private ArrayList<Double> indicatorLine;
    @Override
    public ArrayList<Double> getIndicatorLine() {
        return indicatorLine;
    }

    private ArrayList<Double> indicatorMALine;
    public ArrayList<Double> getIndicatorMALine() {
        return indicatorMALine;
    }

    public OnBalanceVolume(ArrayList<DayPriceRecord> aClosingPrices) throws Exception {
        super(aClosingPrices);
    }

    @Override
    protected void initSpecifics() {
        AVGCNT = 20;
        indicatorLine = new ArrayList<>();
        indicatorMALine = new ArrayList<>();
    }

    @Override
    protected void calcSignals() {
        signalen = new ArrayList<>();
        for (int i=0; i <= myClosingPrices.size()-1; i++) {
            if (i >= AVGCNT ) {
                boolean s1 =   indicatorLine.get(i-1) < indicatorMALine.get(i-2);
                boolean s2 =   indicatorLine.get(i) > indicatorMALine.get(i);
                boolean s3 =   indicatorLine.get(i-1) > indicatorMALine.get(i-2);
                boolean s4 =   indicatorLine.get(i) < indicatorMALine.get(i);
                boolean koopsig = s1 && s2;
                boolean verkoopsig = s3 && s4;

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
    }

    @Override
    protected void calcSignalLine() throws Exception {
        // is part of calcIndicator()
    }


    @Override
    protected void calcIndicator() {
        double currentValue = 0L;

        DayPriceRecord dprPrev = null;

        for (int i = 0; i<= myClosingPrices.size()-1; i++) {
             DayPriceRecord dpr = myClosingPrices.get(i);

            if (dprPrev != null) {
                if (dpr.getClose() > dprPrev.getClose()) {
                    currentValue += dpr.getVolume();
                } else if (dpr.getClose() < dprPrev.getClose()) {
                    currentValue -= dpr.getVolume();
                }
            }
            // dure methode maar makkelijk te implementeren: bereken voortschrijdend
            // gemiddelde uit de laatste 20 waarden
            double movavg = 0.0;
            if (i < AVGCNT) {
                movavg = 0.0;
                indicatorMALine.add(movavg);
            } else {
                movavg = 0.0;
                int lastindex = indicatorLine.size()-1;
                for (int j = 0; j <= AVGCNT - 1; j++) {
                    movavg += indicatorLine.get(lastindex - j);
                }
                movavg /= AVGCNT;
                indicatorMALine.add(movavg);
            }
            indicatorLine.add(currentValue);
            dprPrev = dpr;
        }
    }
}
