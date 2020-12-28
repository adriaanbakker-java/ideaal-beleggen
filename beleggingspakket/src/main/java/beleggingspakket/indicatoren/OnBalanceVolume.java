package beleggingspakket.indicatoren;


import beleggingspakket.Koersen.DayPriceRecord;

import java.util.ArrayList;

public class OnBalanceVolume extends Indicator {
    private int AVGCNT;

    public ArrayList<Double> getOBVline() {
        return OBVline;
    }

    private ArrayList<Double> OBVline;

    public ArrayList<Double> getOBVMovingAverage() {
        return OBV_movingavg;
    }

    private ArrayList<Double> OBV_movingavg;

    public OnBalanceVolume(ArrayList<DayPriceRecord> aClosingPrices) throws Exception {
        super(aClosingPrices);
    }

    @Override
    public ArrayList<Double> getIndicatorLine() {
        return null;
    }

    @Override
    protected void initSpecifics() {
        AVGCNT = 20;
        OBV_movingavg = new ArrayList<>();
        OBVline = new ArrayList<>();
        OBV_movingavg = new ArrayList<>();
    }

    @Override
    protected void calcSignals() {

    }

    @Override
    protected void calcSignalLine() throws Exception {

    }

    @Override
    protected void calcIndicator() {
        calcOBVValues();
    }

    private void calcOBVValues() {
        double currentValue = 0L;

        DayPriceRecord dprPrev = null;

        for (int i = 0; i<= myClosingPrices.size()-1; i++) {
             DayPriceRecord dpr = myClosingPrices.get(i);

            int volume = dpr.getVolume();
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
                OBV_movingavg.add(movavg);
            } else {
                movavg = 0.0;
                int lastindex = OBVline.size()-1;
                for (int j = 0; j <= AVGCNT - 1; j++) {
                    movavg += OBVline.get(lastindex - j);
                }
                movavg /= AVGCNT;
                OBV_movingavg.add(movavg);
            }


            OBVline.add(currentValue);
            dprPrev = dpr;
        }
    }
}
