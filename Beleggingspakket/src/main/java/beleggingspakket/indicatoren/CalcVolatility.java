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


    public double calcStdDev(int index) {
        if (index < nrOfDays)
            return -1;
        double prices[] = new double[nrOfDays];
        for (int i= 1; i<= nrOfDays; i++) {
            double d = myClosingPrices.get(index - nrOfDays + i).getClose();
            prices[i-1]= d;
            //     last nrOfDays entries including entry (index - 1)
            //     index - 1 - (nrOfdays - 1)..index - 1
        }

        return stdevClass.stdev(prices);
    }

    public double calcVolatility(int index) {
        double stdev = calcStdDev(index);
        double result = stdev * Math.sqrt(nrTradingDays);
        return result;
    }

}
