package beleggingspakket.indicatoren;


import beleggingspakket.Koersen.DayPriceRecord;

import java.util.ArrayList;

// Voorlopige klasse die van een bepaalde index in de reeks de volatiliteit
// berekent
public class CalcVolatility {

    // See also https://www.fool.com/knowledge-center/how-to-calculate-annualized-volatility.aspx
    /*
    *    Daily % change = (todays price - yesterdays price)/yesterdays price * 100
    *    For example
    *        4 aug   2093.32
    *        3 aug   2098.04       daily % change on 4 aug equals -0.22%
    * */
    private ArrayList<DayPriceRecord> myClosingPrices;

    private final int nrOfDays = 90;
    private final int nrTradingDays = 250;

    public CalcVolatility(ArrayList<DayPriceRecord> aDayPriceArray) {
        myClosingPrices = aDayPriceArray;
    }


    // Calc stdev of log returns in the last nrOfDays before and including
    // price at index of closingprices list
    // log return at index i = ln(C{i}/C{i-1})
    public double calcStdDev(int index) {
        if (index < nrOfDays)
            return -1;
        double prices[] = new double[nrOfDays];
        for (int i= 1; i<= nrOfDays; i++) {
            double d = myClosingPrices.get(index - nrOfDays + i).getClose();
            prices[i-1]= d; // 0.. nrDays-1
        }
        // Calc log returns = ln(Ci/C{i-1}) where C{i} is closing price on day i
        double lnReturns[] = new double [nrOfDays-1];
        for (int i= 1; i<= nrOfDays-1; i++) {
            if (prices[i-1] >0.01) {
                lnReturns[i-1] = Math.log(prices[i]/prices[i-1]);
            } else {
                lnReturns[i-1] = 0;
            }
        }
        double result = stdevClass.stdev(lnReturns);
        return result;
    }

    public double calcVolatility(int index) {
        double stdev = calcStdDev(index);
        // this is the daily volatility of the closing price
        // annualize by nrTradingDays in a year
        double result = stdev * Math.sqrt(nrTradingDays);
        return result;
    }

}
