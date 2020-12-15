package beleggingspakket.indicatoren;

/*
*

The relative strength index (RSI) is computed with a two-part calculation
that starts with the following formula:

  RSI_1 = 100 - ( 100 /( 1+ (avg_gain/avg_loss)))


The average gain or loss used in the calculation is the average percentage gain or
losses during a look-back period. The formula uses positive values for the average losses.

The standard is to use 14 periods to calculate the initial RSI value.

first column is price
next column is change = price today minus price yesterday
third column, upw ( upward movement) = change if change>0, 0 otherwise
fourth column, downw ( downward movement ) = ABS(change) if change<0, 0 otherwise
(third and fourth column are only defined for row index >= 2)
fifth column is avgupw (average upward movement) is only defined for row >= 15
   equals the 14 days exponential avg of the downw column
   for row 15 avgupw = average of first 14 upwards movements
   for row >= 16 avgupw =  (upw + avgupw(yesterday) * 13) / 14
sixth column is avgdwn (average downward movement) is only defined for row >= 15
   equals the 14 days exponential avg of the downw column, calculated in a similar way as
   avgupw
seventh column is rsv (relative strength value) = avgupw / avgdwn
   is only defined for row >= 15
   is not calculated when avgdwn = 0
eighth column is RSI (relative strength)
   is only defined for rows >= 15
   equals 100 - 100 /( RSI  +  1) when avgdwn > 0
   equals 100 when avgdwn = 0

*/


import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;

import java.util.ArrayList;

public class RSI {
    public ArrayList<Double> getRSILine() {
        return RSILine;
    }

    private ArrayList<Double> RSILine = new ArrayList<>();

    private ArrayList<DayPriceRecord> DayPriceArray;
    private ArrayList<IndicatorSignal> signalen = null;


    public RSI(ArrayList<DayPriceRecord> aDayPriceArray) throws Exception {
        DayPriceArray =aDayPriceArray;
        calcRsiValues();
        calcSignalen();
    }

    // Calculate buy- and sell signal moments
    private void calcSignalen() {
        signalen = new ArrayList<>();
        for (int i=1; i <= DayPriceArray.size()-1; i++) {
            double prevVal = getRSILine().get(i-1);
            double val = getRSILine().get(i);
            if ((prevVal > 70) && (val < 70)) {
                DayPriceRecord dpr = DayPriceArray.get(i);
                IDate iDate = new IDate(dpr.getYear(),dpr.getMonth(), dpr.getDay());
                signalen.add(new IndicatorSignal(iDate, false));
            }
            if ((prevVal < 30) && (val > 30)) {
                DayPriceRecord dpr = DayPriceArray.get(i);
                IDate iDate = new IDate(dpr.getYear(),dpr.getMonth(), dpr.getDay());
                signalen.add(new IndicatorSignal(iDate, true));
            }
        }
    }


    private void calcRsiValues() {
        double price = 0.0;
        double change = 0.0;
        double upw = 0.0;
        double downw = 0.0;
        double avgdwn = 0.0;
        double avgup = 0.0;
        double rs = 0.0;
        double rsi = 0.0;

        DayPriceRecord dprPrev  = null;

        for (int i = 0; i<= DayPriceArray.size()-1; i++) {
            DayPriceRecord dpr = DayPriceArray.get(i);
            if (i > 0) {
                change = dpr.getClose() - dprPrev.getClose();
                if (change > 0.0) {
                    upw = change;
                } else {
                    upw = 0.0;
                }
                if (change < 0.0) {
                    downw = -change;
                } else {
                    downw = 0.0;
                }
            }
            if (i < 14) {
                avgup += upw;
                avgdwn += downw;
            } else if (i == 14) {
                avgup = (avgup + upw)/ 14;
                avgdwn = (avgdwn) / 14;
            } else {
                avgup = (upw + avgup*13) / 14;
                avgdwn = (downw + avgdwn*13)/14;
            }
            if (i > 15) {
                if (avgdwn > 0.00000001) {
                    rs = avgup/avgdwn;
                    rsi = 100 - 100 / (rs + 1);
                } else {
                    rsi = 100;
                }
            }

            RSILine.add(rsi);
            dprPrev = dpr;
        }
    }

    public ArrayList<IndicatorSignal> getSignalen() {
        return signalen;
    }
}
