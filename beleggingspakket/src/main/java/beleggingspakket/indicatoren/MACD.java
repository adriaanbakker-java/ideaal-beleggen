package beleggingspakket.indicatoren;


import beleggingspakket.Koersen.DayPriceRecord;
import beleggingspakket.util.IDate;


import java.util.ArrayList;

/*
 * The Formula for MACD:
 *
 * MACD=12-Period EMA − 26-Period EMA
 *
 * MACD is calculated by subtracting the long-term EMA (26 periods) from
 * the short-term EMA (12 periods).
 * An exponential moving average (EMA) is a type of moving average (MA)
 * that places a greater weight
 * and significance on the most recent data points.
 *
 * The exponential moving average is also referred to as the
 * exponentially weighted moving average.
 * An exponentially weighted moving average reacts more significantly to recent price
 * changes than
 * a simple moving average (SMA), which applies an equal weight to
 * all observations in the period.
 */
public class MACD {
    ArrayList<DayPriceRecord> myClosingPrices;
    private int slowDays = 26;
    private int fastDays = 12;


    public ArrayList<Double> getMACDlist() {
        return MACDlist;
    }

    private ArrayList<Double> fastEMA = null;
    private ArrayList<Double> slowEMA = null;
    private ArrayList<Double> MACDlist = null;

    public ArrayList<Double> getMACDSmoothed() {
        return MACDSmoothed;
    }

    public ArrayList<MacdSignaal> getMACDSignalen() {
        return MACDSignalen;
    }

    private ArrayList<MacdSignaal> MACDSignalen = null;
    private ArrayList<Double> MACDSmoothed = null;


    public MACD(ArrayList<DayPriceRecord> aDayPriceArray) throws Exception {
        myClosingPrices = aDayPriceArray;
        calcMACD(myClosingPrices);
        calcMACDSignal();
        calcSignalen();
    }

    // Calculate buy- and sell signal moments
    private void calcSignalen() {
        MACDSignalen = new ArrayList<>();
        for (int i=0; i <= myClosingPrices.size()-1; i++) {
            if (i >= slowDays ) {
               boolean s1 =   MACDlist.get(i-1) < MACDSmoothed.get(i-2);
               boolean s2 =   MACDlist.get(i) > MACDSmoothed.get(i);
               boolean s3 =   MACDlist.get(i-1) > MACDSmoothed.get(i-2);
               boolean s4 =   MACDlist.get(i) < MACDSmoothed.get(i);
               boolean koopsig = s1 && s2;
               boolean verkoopsig = s3 && s4;

               if ((koopsig) || verkoopsig) {
                   DayPriceRecord dpr = myClosingPrices.get(i);
                   IDate iDate = new IDate(dpr.getYear(),dpr.getMonth(), dpr.getDay());
                   if (koopsig)
                       MACDSignalen.add(new MacdSignaal(iDate, true));
                   else
                       MACDSignalen.add(new MacdSignaal(iDate, false));
               }
            }
        }
    }

    void calcMACDSignal() throws Exception {
        if (MACDlist == null ) {
            throw new Exception("MACD calcMACDSignal: eerst MACD laten berekenen");
        }
        MACDSmoothed =  calcEma(MACDlist, 9);
    }

    void calcMACD(ArrayList<DayPriceRecord> aClosingPrices) {
        MACDlist = new ArrayList<>();
        ArrayList<Double> closingPrices = new ArrayList<>();
        for (DayPriceRecord dpr: aClosingPrices) {
            closingPrices.add(dpr.getClose());
        }
        fastEMA = calcEma(closingPrices, fastDays);
        slowEMA = calcEma(closingPrices, slowDays);
        for (int i = 0; i<= closingPrices.size()-1; i++) {
            double macdValue = 0;
            if (i >= slowDays) {
                macdValue = fastEMA.get(i) - slowEMA.get(i);
            }
            MACDlist.add(macdValue);
        }
    }

    ArrayList<Double>  calcEma(ArrayList<Double> aClosingPrices, int nrOfDays) {
        ArrayList<Double> result = new ArrayList<>();
        double avg = 0;
        double ema = 0;
        double smoothingFactor = 2.0/(nrOfDays + 1);
        for (int i= 0; i<=nrOfDays-1; i++) {
            avg += aClosingPrices.get(i);
            result.add(0.0);
        }
        avg /= nrOfDays;
        ema = avg;
        result.set(nrOfDays-1, ema);
        for (int i = nrOfDays; i <= aClosingPrices.size() -1 ; i++) {
            ema = aClosingPrices.get(i) * smoothingFactor + ema * (1-smoothingFactor);
            result.add(ema);
        }
        return result;
    }

}
