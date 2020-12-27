package beleggingspakket.indicatoren;

import beleggingspakket.Koersen.DayPriceRecord;

import java.util.ArrayList;

public abstract class Indicator {

    ArrayList<DayPriceRecord> myClosingPrices;
    public ArrayList<IndicatorSignal> signalen = null;

    public Indicator(ArrayList<DayPriceRecord> aDayPriceArray) throws Exception {
        initSpecifics();
        myClosingPrices = aDayPriceArray;
        calcIndicator();
        calcSignalLine();
        calcSignalen();
    }

    public ArrayList<IndicatorSignal> getSignalen() {
        return signalen;
    }

    // Because field initializations of the specific indicator only take place AFTER the object
    // is created via super() we need to initialize MACD fields from within the Indicator superclass
    protected abstract void initSpecifics();

    protected abstract void calcSignalen();

    protected abstract void calcSignalLine() throws Exception;

    protected abstract void calcIndicator();
}
