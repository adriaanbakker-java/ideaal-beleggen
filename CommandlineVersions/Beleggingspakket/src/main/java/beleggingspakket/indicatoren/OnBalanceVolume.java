package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;

import java.util.ArrayList;

public class OnBalanceVolume {
    public ArrayList<Double> getOBVline() {
        return OBVline;
    }

    private ArrayList<Double> OBVline = new ArrayList<>();

    public ArrayList<Double> getOBVMovingAverage() {
        return OBV_movingavg;
    }

    private ArrayList<Double> OBV_movingavg = new ArrayList<>();

    private ArrayList<DayPriceRecord> DayPriceArray;

    public OnBalanceVolume(ArrayList<DayPriceRecord> aDayPriceArray) throws Exception {
        DayPriceArray =aDayPriceArray;
        calcOBVValues();
    }
    private final int AVGCNT = 20;



    private void calcOBVValues() {
        double currentValue = 0L;

        DayPriceRecord dprPrev = null;

        for (int i = 0; i<= DayPriceArray.size()-1; i++) {
             DayPriceRecord dpr = DayPriceArray.get(i);

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
